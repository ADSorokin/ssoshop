package ru.alexds.ccoshop.service;


import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.model.jdbc.MySQLJDBCDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericItemBasedRecommender;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.EuclideanDistanceSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;
import org.springframework.stereotype.Service;
import ru.alexds.ccoshop.dto.ProductDTO;
import ru.alexds.ccoshop.dto.RecommendationDTO;
import ru.alexds.ccoshop.entity.ARTClusterEntity;
import ru.alexds.ccoshop.entity.Rating;
import ru.alexds.ccoshop.repository.RatingRepository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;


/**
 * Сервис для управления рекомендациями.
 * Обеспечивает API для генерации User-Based, Item-Based и ART-Based рекомендаций,
 * а также для объединения этих рекомендаций и нормализации рейтингов.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RecommendationService {
    private final DataSource dataSource; // JDBC источник данных для Mahout
    public final RatingRepository ratingRepository; // Репозиторий для хранения рейтингов
    private final ARTClusterService artClusterService; // Сервис для управления ART-кластерами

    public final ProductService productService;

    public DataModel dataModel; // Модель данных Mahout
    private List<ARTClusterEntity> artClusters = new ArrayList<>(); // Список ART-кластеров

    private static final double VIGILANCE_PARAMETER = 0.6; // Параметр бдительности для ART кластеризации
    private static final double LEARNING_RATE = 0.3; // Коэффициент обучения для ART кластеризации
    private static final int NEIGHBORHOOD_SIZE = 8; // Размер соседства для User-Based рекомендаций

    /**
     * Инициализация модели Mahout и загрузка ART-кластеров из базы при старте приложения.
     *
     * @throws TasteException если произошла ошибка при инициализации модели данных или загрузке кластеров
     */
    @PostConstruct
    public void init() throws TasteException {
        RecommendationService.log.debug("Инициализация рекомендаций");

        // Инициализация Mahout DataModel через подключение к базе данных
        dataModel = new MySQLJDBCDataModel(dataSource, "ratings", "user_id", "item_id", "rating", "timestamp");

        // Загрузка всех ART кластеров из базы данных
        artClusters = artClusterService.getAllClusters();

        // Логируем количество пользователей и товаров в модели данных
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement stmt = connection.prepareStatement("SELECT COUNT(DISTINCT user_id) AS user_count, COUNT(DISTINCT item_id) AS item_count FROM ratings");
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                RecommendationService.log.info("Number of users: {}", rs.getInt("user_count"));
                RecommendationService.log.info("Number of items: {}", rs.getInt("item_count"));
            }
        } catch (SQLException e) {
            RecommendationService.log.error("Ошибка извлечения данных из базы данных", e);
        }
    }

    /**
     * Генерация User-Based рекомендаций для указанного пользователя.
     *
     * @param userId             Идентификатор пользователя, для которого необходимо сформировать рекомендации
     * @param numRecommendations Количество рекомендаций для возврата
     * @return Список рекомендаций в формате DTO
     * @throws TasteException если произошла ошибка при вычислении рекомендаций
     */
    public List<RecommendationDTO> getUserBasedRecommendations(Long userId, int numRecommendations) throws TasteException {
        RecommendationService.log.debug("Запрос на получение пользовательских рекомендаций для пользователя ID: {}", userId);

        // Создаем объект Similarity для определения похожести между пользователями
        EuclideanDistanceSimilarity similarity = new EuclideanDistanceSimilarity(dataModel);

        // Формируем соседство пользователей
        UserNeighborhood neighborhood = new NearestNUserNeighborhood(NEIGHBORHOOD_SIZE, similarity, dataModel);

        // Создаем User-Based рекомендатель
        GenericUserBasedRecommender recommender = new GenericUserBasedRecommender(dataModel, neighborhood, similarity);

        // Получаем рекомендации для пользователя
        List<RecommendedItem> recommendedItems = recommender.recommend(userId, numRecommendations);

        // Логируем количество соседей для текущего пользователя
        long[] neighborIds = neighborhood.getUserNeighborhood(userId);
        RecommendationService.log.info("Количество соседей для пользователя {}: {}", userId, neighborIds.length);

        // Преобразуем список в рекомендационные DTO
        return mapRecommendationsToDTO(recommendedItems);
    }

    /**
     * Генерация Item-Based рекомендаций для указанного товара.
     *
     * @param itemId             Идентификатор товара, для которого необходимо найти похожие товары
     * @param numRecommendations Количество рекомендаций для возврата
     * @return Список рекомендаций в формате DTO
     * @throws TasteException если произошла ошибка при вычислении рекомендаций
     */
    public List<RecommendationDTO> getItemBasedRecommendations(Long itemId, int numRecommendations) throws TasteException {
        RecommendationService.log.debug("Запросить рекомендации по товару ID: {}", itemId);

        // Используем сходство на основе Item для определения похожих товаров
        ItemSimilarity similarity = new AdjustedCosineSimilarity(dataModel);

        // Создаем Item-Based рекомендатель
        GenericItemBasedRecommender recommender = new GenericItemBasedRecommender(dataModel, similarity);

        // Получаем похожие товары
        List<RecommendedItem> recommendedItems = recommender.mostSimilarItems(itemId, numRecommendations);

        // Преобразуем в формат DTO
        return mapRecommendationsToDTO(recommendedItems);
    }

    /**
     * Генерация ART-Based рекомендаций на основе кластеров.
     *
     * @param userId Идентификатор пользователя, для которого необходимо сформировать рекомендации
     * @return Список рекомендаций в формате DTO
     */
    public List<RecommendationDTO> getARTRecommendations(Long userId) {
        RecommendationService.log.debug("Запрос на получение рекомендаций на основе ART для пользователя ID: {}", userId);

        // Генерируем вектор пользователя
        double[] userVector = createUserVector(userId);

        // Находим лучший кластер
        ARTClusterEntity bestCluster = findBestCluster(userVector);
        if (bestCluster == null) {
            RecommendationService.log.warn("Для пользователя не найден подходящий кластер ID: {}, creating a new one", userId);
            // Если подходящего кластера нет, создаем новый
            bestCluster = new ARTClusterEntity();
            bestCluster.setWeights(Arrays.asList(Arrays.stream(userVector).boxed().toArray(Double[]::new)));
            bestCluster.setUserIds(new ArrayList<>(Collections.singleton(userId)));

            // Сохраняем кластер в базе
            artClusterService.saveCluster(bestCluster);
            artClusters.add(bestCluster);
        } else {
            RecommendationService.log.debug("Обновление существующего кластера для пользователя ID: {}", userId);
            // Обновляем обучением веса кластера
            adaptClusterWeights(bestCluster, userVector);
            artClusterService.saveCluster(bestCluster);
        }

        // Генерируем рекомендации из данного кластера
        List<RecommendationDTO> recommendations = generateRecommendationsFromCluster(bestCluster, userId);
        return normalizeRatings(recommendations); // Нормализуем рейтинги перед возвратом
    }

    /**
     * Поиск подходящего кластера для пользователя (ART1).
     *
     * @param userVector Вектор пользователя, который необходимо сравнить с кластерами
     * @return ART-кластер, если найден, или null в противном случае
     */
    private ARTClusterEntity findBestCluster(double[] userVector) {
        RecommendationService.log.debug("Поиск лучшего кластера ART для пользовательского вектора: {}", Arrays.toString(userVector));

        return artClusters.stream()
                .filter(cluster -> matchCluster(cluster, userVector) >= VIGILANCE_PARAMETER)
                .findFirst()
                .orElse(null);
    }

    /**
     * Генерация рекомендаций из кластера с учетом скоринга похожести как у Mahout.
     *
     * @param cluster ART-кластер, из которого необходимо получить рекомендации
     * @param userId  Идентификатор пользователя, для которого формируются рекомендации
     * @return Список рекомендаций в формате DTO
     */
    private List<RecommendationDTO> generateRecommendationsFromCluster(ARTClusterEntity cluster, Long userId) {
        RecommendationService.log.debug("Генерация рекомендаций из кластера: {}для пользователя ID: {}", cluster.getId(), userId);

        // Найдем товары, которыми интересуются пользователи из текущего кластера
        Set<Long> productIds = cluster.getUserIds().stream()
                .flatMap(otherUserId -> ratingRepository.findByUserId(otherUserId).stream())
                .map(Rating::getItemId) // Собираем все товары

                .collect(Collectors.toSet());

        // Получим все товары, с которыми взаимодействовал текущий пользователь
        List<Long> userProductIds = ratingRepository.findByUserId(userId).stream()
                .map(Rating::getItemId)
                .collect(Collectors.toList());

        // Преобразуем в список `RecommendationDTO` с расчетом рейтинга похожести
        return productIds.stream()
                .map(productId -> {
                    // Получаем информацию о товаре
                    ProductDTO product = productService.getProductById(productId)
                            .orElseThrow(() -> new RuntimeException("Product not found"));

                    // Рассчитываем рейтинг похожести с текущим пользователем
                    double similarityScore = calculateSimilarityScore(productId, userProductIds);

                    // Формируем DTO
                    return new RecommendationDTO(productId, product.getName(), similarityScore);
                })
                .sorted(Comparator.comparingDouble(RecommendationDTO::getRating).reversed()) // Сортируем по убыванию похожести
                .collect(Collectors.toList());
    }

    /**
     * Рассчитывает рейтинг похожести товара на основе метрик, схожих с Mahout.
     * Использует косинусное сходство между векторами рейтингов.
     *
     * @param productId      ID текущего товара для оценки
     * @param userProductIds список ID товаров, которыми интересуется пользователь
     * @return рейтинг похожести в диапазоне [0,1], где 1 означает полное сходство
     */


    public double calculateSimilarityScore(Long productId, List<Long> userProductIds) {
        // Находим рейтинг других пользователей для данного товара
        List<Rating> itemRatings = ratingRepository.findByItemId(productId);
        // Получаем метаданные товара
        ProductDTO product = productService.getProductById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // Вычисляем сумму отклонений рейтингов от среднего рейтинга
        double numerator = 0.0; // Числитель
        double denominator = 0.0; // Знаменатель
        double contentWeight = 0.8; // Вес метаданных
        double averageRating = 0.0;
        double metadataSimilarity=1;
        boolean allRatingsEqual = true;


        for (Rating rating : itemRatings) {
            if (userProductIds.contains(rating.getItemId())) {
                double averageUserRating = getUserAverageRating(rating.getUserId()); // Средний рейтинг пользователя
                numerator += (rating.getRating() - averageUserRating) * (rating.getRating() - averageUserRating);
                denominator += Math.pow((rating.getRating() - averageUserRating), 2);

                // Проверяем, равны ли все рейтинги
//                if (allRatingsEqual && !rating.getRating().equals(averageRating)) {
//                    allRatingsEqual = false;
//                }

                // Учитываем метаданные
                ProductDTO otherProduct = productService.getProductById(rating.getItemId())
                        .orElseThrow(() -> new RuntimeException("Продукт не найден"));



                 metadataSimilarity = calculateMetadataSimilarity(product, otherProduct);
                System.out.println(metadataSimilarity);
                if (metadataSimilarity > 0) {

                    denominator *= metadataSimilarity;

                }


            }


        }

        // Обработка случаев
        if (denominator == 0) {
            return 0.0;
        } else if (numerator == 0 && denominator == 1) {
            return 1.0; // Полное сходство
        } else {
            double similarity = numerator / Math.sqrt(denominator);
            // Нормализация в диапазон [0, 1]
          similarity = Math.min(1.0, Math.max(0.0, similarity));
       double   finalScore = (1 - contentWeight) * similarity + contentWeight * metadataSimilarity;
            log.debug("Final similarity score for product {}: {}", productId, finalScore);
            return finalScore ;


        }
 //      return denominator == 0 ? 0 : numerator / Math.sqrt(denominator); // Возвращаем рейтинг похожести
    }

    private double calculateMetadataSimilarity(ProductDTO product1, ProductDTO product2) {
        if (product1.getCharacteristic() != null && product2.getCharacteristic() != null) {
            // Пример: считаем схожесть по характеристикам
            Set<String> characteristic_1 = new HashSet<>(product1.getCharacteristic());
            Set<String> characteristic_2 = new HashSet<>(product2.getCharacteristic());
            Set<String> intersection = new HashSet<>(characteristic_1);
            intersection.retainAll(characteristic_2);
            double similarity = (double) intersection.size() / Math.max(characteristic_1.size(), characteristic_2.size());
            // Возвращаем 0.0, если схожесть равна 0, чтобы пропустить этот товар
            return similarity > 0 ? similarity : 0.0;
        } else {
            return 0.0;
        }
    }





    /**
     * Вычисляет средний рейтинг для заданного пользователя.
     *
     * @param userId ID пользователя
     * @return средний рейтинг пользователя или 0.0, если рейтингов нет
     */
    private double getUserAverageRating(Long userId) {
        List<Rating> ratings = ratingRepository.findByUserId(userId);
        return ratings.stream()
                .mapToDouble(Rating::getRating)
                .average()
                .orElse(0.0);
    }

    /**
     * Нормализует рейтинги рекомендаций в диапазоне [1, 10].
     * Использует линейное масштабирование для преобразования исходных рейтингов.
     *
     * @param recommendations список рекомендаций для нормализации
     * @return список рекомендаций с нормализованными рейтингами
     */
    public List<RecommendationDTO> normalizeRatings(List<RecommendationDTO> recommendations) {
        if (recommendations.isEmpty()) {
            return recommendations; // Если список пустой, возвращаем его без изменений
        }

        double minRating = recommendations.stream()
                .mapToDouble(RecommendationDTO::getRating)
                .min()
                .orElse(0.0);
        double maxRating = recommendations.stream()
                .mapToDouble(RecommendationDTO::getRating)
                .max()
                .orElse(1.0);

        double newMin = 1.0;
        double newMax = 10.0;


        // Если все рейтинги одинаковые, возвращаем среднее значение между newMin и newMax
        if (maxRating == minRating) {
            double averageRating = (newMin + newMax) / 2.0;
            return recommendations.stream()
                    .map(recommendation -> new RecommendationDTO(
                            recommendation.getItemId(),
                            recommendation.getItemName(),
                            averageRating))
                    .collect(Collectors.toList());
        }




        return recommendations.stream().map(recommendation -> {
            double normalizedRating = newMin + ((recommendation.getRating() - minRating) * (newMax - newMin)) / (maxRating - minRating);
            return new RecommendationDTO(recommendation.getItemId(), recommendation.getItemName(), normalizedRating);
        }).collect(Collectors.toList());
    }


    /**
     * Нормализует шкалу score от 1 до 10.
     *
     * @param score    Исходный рейтинг
     * @param minScore Минимальное значение в наборе
     * @param maxScore Максимальное значение в наборе
     * @return Нормализованное значение
     */
    private double normalizeScore(double score, double minScore, double maxScore) {
        if (maxScore == minScore) {
            // Если все значения одинаковы, возвращаем 5 (средняя оценка на шкале от 1 до 10)
            return 5.0;
        }
        return 1 + ((score - minScore) * 9) / (maxScore - minScore); // Нормализация от 1 до 10
    }


    /**
     * Вычисляет степень соответствия вектора пользователя кластеру по алгоритму ART1.
     * Использует косинусное сходство между векторами.
     *
     * @param cluster    кластер для сравнения
     * @param userVector вектор предпочтений пользователя
     * @return значение сходства в диапазоне [0,1]
     */
    public double matchCluster(ARTClusterEntity cluster, double[] userVector) {
        List<Double> weights = cluster.getWeights();
        if (weights.isEmpty() || weights.size() < userVector.length) { // Если веса еще не инициализированы
            for (double v : userVector) {
                weights.add(v); // Инициализация весов вектора пользователя для нового кластера
            }
        }
        double dotProduct = 0.0;
        double magnitudeWeights = 0.0;
        double magnitudeVector = 0.0;

        for (int i = 0; i < userVector.length; i++) {
            double weight = weights.get(i);
            double input = userVector[i];

            dotProduct += weight * input;
            magnitudeWeights += weight * weight;
            magnitudeVector += input * input;
        }

        return (magnitudeVector == 0 || magnitudeWeights == 0) ? 0 : dotProduct / Math.sqrt(magnitudeVector);
    }

    /**
     * Обновляет веса кластера с учетом нового вектора пользователя.
     * Использует правило обучения ART1 с заданным коэффициентом обучения.
     *
     * @param cluster    кластер для обновления
     * @param userVector вектор предпочтений пользователя
     */
    public void adaptClusterWeights(ARTClusterEntity cluster, double[] userVector) {
        List<Double> updatedWeights = new ArrayList<>();
        List<Double> currentWeights = cluster.getWeights();

        for (int i = 0; i < userVector.length; i++) {
            double adaptedWeight = currentWeights.get(i) + LEARNING_RATE * (userVector[i] - currentWeights.get(i));
            updatedWeights.add(adaptedWeight);
        }

        cluster.setWeights(updatedWeights);
    }

    /**
     * Создает бинарный вектор предпочтений пользователя на основе его рейтингов.
     * Размерность вектора определяется максимальным ID товара в системе.
     *
     * @param userId ID пользователя
     * @return бинарный вектор предпочтений, где 1 означает интерес к товару
     */
    public double[] createUserVector(Long userId) {
        // Получаем все рейтинги пользователя
        List<Rating> ratings = ratingRepository.findByUserId(userId);

        // Создаем вектор, длина которого равна количеству товаров в системе
        int vectorSize = Math.toIntExact(ratingRepository.findMaxItemId());
        double[] vector = new double[vectorSize];
        System.out.println(vectorSize);

        for (Rating rating : ratings) {
            vector[rating.getItemId().intValue() - 1] = 1.0; // Простое бинарное представление
            System.out.println(rating.getItemId());
        }

        return vector;
    }


    /**
     * Объединяет рекомендации, полученные разными методами (User-Based и ART-Based).
     * При конфликтах выбирает рекомендацию с наивысшим рейтингом.
     *
     * @param userBased          рекомендации на основе коллаборативной фильтрации
     * @param artBased           рекомендации на основе ART-кластеризации
     * @param numRecommendations максимальное количество рекомендаций в результате
     * @return отсортированный список топ-N рекомендаций
     */
    public List<RecommendationDTO> mergeRecommendations(List<RecommendationDTO> userBased,
                                                        List<RecommendationDTO> artBased,
                                                        int numRecommendations) {

        // Сначала объединяем все рекомендации в одну коллекцию
        List<RecommendationDTO> allRecommendations = new ArrayList<>();
        allRecommendations.addAll(userBased);
        allRecommendations.addAll(artBased);

        // Группируем рекомендации по ID объектов и берем лучший `score`
        Map<Long, RecommendationDTO> recommendationMap = new HashMap<>();

        for (RecommendationDTO rec : allRecommendations) {
            recommendationMap.merge(rec.getItemId(), rec, (existing, newRec) -> {
                // Сохраняем рекомендацию с наивысшим скорингом
                if (newRec.getRating() > existing.getRating()) {
                    return newRec;
                }
                return existing;
            });
        }

        // Преобразуем обратно в список и отбираем топ-N по скору
        return recommendationMap.values().stream().sorted(Comparator.comparingDouble(RecommendationDTO::getRating).reversed()).limit(numRecommendations) // Берем только top-N рекомендаций
                .collect(Collectors.toList());
    }


    /**
     * Преобразует рекомендации из формата Mahout в DTO объекты.
     * Дополняет рекомендации информацией о названии товара.
     *
     * @param recommendedItems список рекомендаций в формате Mahout
     * @return список рекомендаций в формате DTO
     * @throws NoSuchElementException если товар не найден в базе данных
     */
    private List<RecommendationDTO> mapRecommendationsToDTO(List<RecommendedItem> recommendedItems) {
        System.out.println(recommendedItems);
        return recommendedItems.stream().map(item -> new RecommendationDTO(
                item.getItemID(),
                productService.getProductById(item.getItemID()).get().getName(),
                item.getValue())).collect(Collectors.toList());
    }
}


