package ru.alexds.ccoshop.service;


import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.PostConstruct;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.model.jdbc.MySQLJDBCDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericItemBasedRecommender;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import ru.alexds.ccoshop.dto.RecommendationDTO;
import ru.alexds.ccoshop.entity.ARTClusterEntity;
import ru.alexds.ccoshop.entity.Rating;
import ru.alexds.ccoshop.repository.RatingRepository;

import javax.sql.DataSource;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final DataSource dataSource; // JDBC источник данных для Mahout
    private final RatingRepository ratingRepository; // Репозиторий для хранения рейтингов
    private final ARTClusterService artClusterService; // Сервис для управления ART-кластерами

    private DataModel dataModel; // Модель данных Mahout
    private List<ARTClusterEntity> artClusters = new ArrayList<>(); // Список ART-кластеров

    private static final double VIGILANCE_PARAMETER = 0.8;
    private static final double LEARNING_RATE = 0.2;
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
    }

    /**
     * Генерация User-Based рекомендаций.
     */
    public List<RecommendationDTO> getUserBasedRecommendations(Long userId, int numRecommendations) throws TasteException {
        // Создаем объект Similarity для определения похожести между пользователями
        PearsonCorrelationSimilarity similarity = new PearsonCorrelationSimilarity(dataModel);

        // Формируем соседство пользователей
        UserNeighborhood neighborhood = new NearestNUserNeighborhood(NEIGHBORHOOD_SIZE, similarity, dataModel);

        // Создаем User-Based рекомендатель
        GenericUserBasedRecommender recommender = new GenericUserBasedRecommender(dataModel, neighborhood, similarity);

        // Получаем рекомендации для пользователя
        List<RecommendedItem> recommendedItems = recommender.recommend(userId, numRecommendations);

        // Преобразуем список в рекомендационные DTO
        return mapRecommendationsToDTO(recommendedItems);
    }

    /**
     * Генерация Item-Based рекомендаций.
     */
    public List<RecommendationDTO> getItemBasedRecommendations(Long itemId, int numRecommendations) throws TasteException {
        // Используем сходство на основе Item для определения похожих товаров
        ItemSimilarity similarity = new PearsonCorrelationSimilarity(dataModel);

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
        return artClusters.stream()
                .filter(cluster -> matchCluster(cluster, userVector) >= VIGILANCE_PARAMETER)
                .findFirst()
                .orElse(null);
    }

    /**
     * Генерация рекомендаций из кластера.
     */
    private List<RecommendationDTO> generateRecommendationsFromCluster(ARTClusterEntity cluster) {
        // Найдем товары, которыми интересуются пользователи из текущего кластера
        Set<Long> productIds = cluster.getUserIds().stream()
                .flatMap(userId -> ratingRepository.findByUserId(userId).stream())
                .map(Rating::getItemId) // Собираем все товары
                .collect(Collectors.toSet());

        // Преобразуем в список `RecommendationDTO`
        return productIds.stream()
                .map(productId -> new RecommendationDTO(productId, "Product Name Placeholder", 0.0)) // Можно заменить имя товара из ProductService
                .collect(Collectors.toList());
    }

    /**
     * Сравнение вектора пользователя с кластером (метод ART1).
     */
    private double matchCluster(ARTClusterEntity cluster, double[] userVector) {
        List<Double> weights = cluster.getWeights();
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

        for (Rating rating : ratings) {
            vector[rating.getItemId().intValue() - 1] = 1.0; // Простое бинарное представление
        }

        return vector;
    }

    // Метод для слияния рекомендаций
    private List<RecommendationDTO> mergeRecommendations(
            List<RecommendationDTO> userBased, List<RecommendationDTO> artBased) {
        Set<Long> seenItems = new HashSet<>();
        List<RecommendationDTO> combined = new ArrayList<>();

        for (RecommendationDTO rec : userBased) {
            if (seenItems.add(rec.getItemId())) {
                combined.add(rec);
            }
        }
        for (RecommendationDTO rec : artBased) {
            if (seenItems.add(rec.getItemId())) {
                combined.add(rec);
            }
        }
        return combined;
    }

    @Operation(summary = "Получить гибридные рекомендации")
    @GetMapping("/hybrid/{userId}")
    public ResponseEntity<List<RecommendationDTO>> getHybridRecommendations(
            @PathVariable @Min(1) Long userId,
            @RequestParam(defaultValue = "10") @Min(1) int numRecommendations
    ) {
        try {
            log.debug("Получение гибридных рекомендаций для пользователя с ID: {}", userId);

            // Получаем User-Based рекомендации
            List<RecommendationDTO> userBased =
                    getUserBasedRecommendations(userId, numRecommendations);

            // Получаем ART-Based рекомендации
            List<RecommendationDTO> artBased =
                    getARTRecommendations(userId);

            // Объединяем рекомендации
            List<RecommendationDTO> combinedRecommendations = mergeRecommendations(
                    userBased, artBased, numRecommendations
            );

            return ResponseEntity.ok(combinedRecommendations);
        } catch (Exception e) {
            log.error("Ошибка при получении гибридных рекомендаций для пользователя {}: {}",
                    userId, e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    // Метод для объединения User-Based и ART-Based рекомендаций
    private List<RecommendationDTO> mergeRecommendations(
            List<RecommendationDTO> userBased,
            List<RecommendationDTO> artBased,
            int numRecommendations) {

        // Сначала объединяем все рекомендации в одну коллекцию
        List<RecommendationDTO> allRecommendations = new ArrayList<>();
        allRecommendations.addAll(userBased);
        allRecommendations.addAll(artBased);

        // Группируем рекомендации по ID объектов и берем лучший `score`
        Map<Long, RecommendationDTO> recommendationMap = new HashMap<>();

        for (RecommendationDTO rec : allRecommendations) {
            recommendationMap.merge(
                    rec.getItemId(),
                    rec,
                    (existing, newRec) -> {
                        // Сохраняем рекомендацию с наивысшим скорингом
                        if (newRec.getRating() > existing.getRating()) {
                            return newRec;
                        }
                        return existing;
                    }
            );
        }

        // Преобразуем обратно в список и отбираем топ-N по скору
        return recommendationMap.values().stream()
                .sorted(Comparator.comparingDouble(RecommendationDTO::getRating).reversed())
                .limit(numRecommendations) // Берем только top-N рекомендаций
                .collect(Collectors.toList());
    }



    /**
     * Преобразование Mahout RecommendedItem в RecommendationDTO.
     */
    private List<RecommendationDTO> mapRecommendationsToDTO(List<RecommendedItem> recommendedItems) {
        return recommendedItems.stream()
                .map(item -> new RecommendationDTO(item.getItemID(), "Product Name Placeholder", item.getValue()))
                .collect(Collectors.toList());
    }
}