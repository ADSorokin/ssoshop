package ru.alexds.ccoshop.service;


import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.model.jdbc.MySQLJDBCDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericItemBasedRecommender;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.TanimotoCoefficientSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.springframework.stereotype.Service;
import ru.alexds.ccoshop.dto.RecommendationDTO;
import ru.alexds.ccoshop.entity.ARTClusterEntity;
import ru.alexds.ccoshop.entity.Rating;
import ru.alexds.ccoshop.repository.ProductRepository;
import ru.alexds.ccoshop.repository.RatingRepository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final DataSource dataSource; // JDBC источник данных для Mahout
    private final RatingRepository ratingRepository; // Репозиторий для хранения рейтингов
    private final ARTClusterService artClusterService; // Сервис для управления ART-кластерами
    private final ProductRepository productRepository;
    private final ProductService productService;

    private DataModel dataModel; // Модель данных Mahout
    private List<ARTClusterEntity> artClusters = new ArrayList<>(); // Список ART-кластеров

    private static final double VIGILANCE_PARAMETER = 0.9;
    private static final double LEARNING_RATE = 0.35;
    private static final int NEIGHBORHOOD_SIZE = 5;

    /**
     * Инициализация модели Mahout и загрузка ART-кластеров из базы при старте приложения.
     */
    @PostConstruct
    public void init() throws TasteException {
        // Инициализация Mahout DataModel через подключение к базе данных
        dataModel = new MySQLJDBCDataModel(dataSource, "ratings", "user_id", "item_id", "rating", "timestamp");

        // Загрузка всех ART кластеров из базы данных
        artClusters = artClusterService.getAllClusters();
        // Логируем количество пользователей и товаров в модели данных
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement stmt = connection.prepareStatement("SELECT COUNT(DISTINCT user_id) AS user_count, COUNT(DISTINCT item_id) AS item_count FROM ratings");
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                log.info("Number of users: {}", rs.getInt("user_count"));
                log.info("Number of items: {}", rs.getInt("item_count"));
            }
        } catch (SQLException e) {
            log.error("Error fetching data from database", e);
        }
    }

    /**
     * Генерация User-Based рекомендаций.
     */
    public List<RecommendationDTO> getUserBasedRecommendations(Long userId, int numRecommendations) throws TasteException {
        // Создаем объект Similarity для определения похожести между пользователями
        //PearsonCorrelationSimilarity similarity = new PearsonCorrelationSimilarity(dataModel);
        //EuclideanDistanceSimilarity similarity = new EuclideanDistanceSimilarity(dataModel);
        //UncenteredCosineSimilarity similarity = new UncenteredCosineSimilarity(dataModel);
        TanimotoCoefficientSimilarity similarity = new TanimotoCoefficientSimilarity(dataModel);

        // Формируем соседство пользователей
        UserNeighborhood neighborhood = new NearestNUserNeighborhood(NEIGHBORHOOD_SIZE, similarity, dataModel);

        // Создаем User-Based рекомендатель
        GenericUserBasedRecommender recommender = new GenericUserBasedRecommender(dataModel, neighborhood, similarity);

        // Получаем рекомендации для пользователя
        List<RecommendedItem> recommendedItems = recommender.recommend(userId, numRecommendations);

        // Логируем количество соседей для текущего пользователя
        long[] neighborIds = neighborhood.getUserNeighborhood(userId);
        log.info("Number of neighbors for user {}: {}", userId, neighborIds.length);

        // Преобразуем список в рекомендационные DTO
        return mapRecommendationsToDTO(recommendedItems);
    }

    /**
     * Генерация Item-Based рекомендаций.
     */
    public List<RecommendationDTO> getItemBasedRecommendations(Long itemId, int numRecommendations) throws TasteException {
        // Используем сходство на основе Item для определения похожих товаров
        //ItemSimilarity similarity = new PearsonCorrelationSimilarity(dataModel);
        // ItemSimilarity similarity = new TanimotoCoefficientSimilarity(dataModel);
        AdjustedCosineSimilarity similarity = new AdjustedCosineSimilarity(dataModel);
        // Создаем Item-Based рекомендатель
        GenericItemBasedRecommender recommender = new GenericItemBasedRecommender(dataModel, similarity);

        // Получаем похожие товары
        List<RecommendedItem> recommendedItems = recommender.mostSimilarItems(itemId, numRecommendations);

        // Преобразуем в формат DTO
        return mapRecommendationsToDTO(recommendedItems);
    }

    /**
     * Генерация ART рекомендаций на основе кластеров.
     */
    public List<RecommendationDTO> getARTRecommendations(Long userId) {
        // Генерируем вектор пользователя
        double[] userVector = createUserVector(userId);

        // Находим лучший кластер
        ARTClusterEntity bestCluster = findBestCluster(userVector);

        if (bestCluster == null) {
            // Если подходящего кластера нет, создаем новый
            bestCluster = new ARTClusterEntity();
            bestCluster.setWeights(Arrays.asList(Arrays.stream(userVector).boxed().toArray(Double[]::new)));
            bestCluster.setUserIds(new ArrayList<>(Collections.singleton(userId)));

            // Сохраняем кластер в базе
            artClusterService.saveCluster(bestCluster);
            artClusters.add(bestCluster);
        } else {
            // Обновляем обучением веса кластера
            adaptClusterWeights(bestCluster, userVector);
            artClusterService.saveCluster(bestCluster);
        }

        // Генерируем рекомендации из данного кластера
        return generateRecommendationsFromCluster(bestCluster);
    }

    /**
     * Поиск подходящего кластера для пользователя (ART1).
     */
    private ARTClusterEntity findBestCluster(double[] userVector) {
        return artClusters.stream().filter(cluster -> matchCluster(cluster, userVector) >= VIGILANCE_PARAMETER).findFirst().orElse(null);
    }

    /**
     * Генерация рекомендаций из кластера.
     */
    private List<RecommendationDTO> generateRecommendationsFromCluster(ARTClusterEntity cluster) {
        // Найдем товары, которыми интересуются пользователи из текущего кластера
        Set<Long> productIds = cluster.getUserIds().stream().flatMap(userId -> ratingRepository.findByUserId(userId).stream()).map(Rating::getItemId) // Собираем все товары
                .collect(Collectors.toSet());

        // Преобразуем в список `RecommendationDTO`
        return productIds.stream().map(productId -> new RecommendationDTO(productId, productService.getProductById(productId).get().getName(),productService.getProductById(productId).get().getPopularity() )) // Можно заменить имя товара из ProductService
                .collect(Collectors.toList());
    }

    /**
     * Сравнение вектора пользователя с кластером (метод ART1).
     */
    private double matchCluster(ARTClusterEntity cluster, double[] userVector) {
        List<Double> weights = cluster.getWeights();
        if (weights.isEmpty() || weights.size() < userVector.length) { // Если веса еще не инициализированы
            for (int i = 0; i < userVector.length; i++) {
                weights.add(userVector[i]); // Инициализация весов вектора пользователя для нового кластера
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
     * Обновление весов кластера с учетом вектора пользователя (адаптация ART1).
     */
    private void adaptClusterWeights(ARTClusterEntity cluster, double[] userVector) {
        List<Double> updatedWeights = new ArrayList<>();
        List<Double> currentWeights = cluster.getWeights();

        for (int i = 0; i < userVector.length; i++) {
            double adaptedWeight = currentWeights.get(i) + LEARNING_RATE * (userVector[i] - currentWeights.get(i));
            updatedWeights.add(adaptedWeight);
        }

        cluster.setWeights(updatedWeights);
    }

    /**
     * Создание вектора пользователя на основе рейтингов из базы данных.
     */
    private double[] createUserVector(Long userId) {
        // Получаем все рейтинги пользователя
        List<Rating> ratings = ratingRepository.findByUserId(userId);

        // Создаем вектор, длина которого равна количеству товаров в системе
        int vectorSize = (int) ratingRepository.count(); // Можно заменить на количество уникальных товаров
        double[] vector = new double[vectorSize];
        System.out.println(vectorSize);

        for (Rating rating : ratings) {
            vector[rating.getItemId().intValue() - 1 - 100] = 1.0; // Простое бинарное представление 101 первый индекс так получилось
            System.out.println(rating.getItemId());
        }

        return vector;
    }

    // Метод для слияния рекомендаций
//    private List<RecommendationDTO> mergeRecommendations(
//            List<RecommendationDTO> userBased, List<RecommendationDTO> artBased) {
//        Set<Long> seenItems = new HashSet<>();
//        List<RecommendationDTO> combined = new ArrayList<>();
//
//        for (RecommendationDTO rec : userBased) {
//            if (seenItems.add(rec.getItemId())) {
//                combined.add(rec);
//            }
//        }
//        for (RecommendationDTO rec : artBased) {
//            if (seenItems.add(rec.getItemId())) {
//                combined.add(rec);
//            }
//        }
//        return combined;
//    }


    // Метод для объединения User-Based и ART-Based рекомендаций
    public List<RecommendationDTO> mergeRecommendations(List<RecommendationDTO> userBased, List<RecommendationDTO> artBased, int numRecommendations) {

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
     * Преобразование Mahout RecommendedItem в RecommendationDTO.
     */
    private List<RecommendationDTO> mapRecommendationsToDTO(List<RecommendedItem> recommendedItems) {
        return recommendedItems.stream().map(item -> new RecommendationDTO(item.getItemID(), productService.getProductById(item.getItemID()).get().getName(), item.getValue())).collect(Collectors.toList());
    }
}