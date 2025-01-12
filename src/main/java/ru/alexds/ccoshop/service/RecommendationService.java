package ru.alexds.ccoshop.service;


import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericItemBasedRecommender;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.TanimotoCoefficientSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import ru.alexds.ccoshop.dto.ProductDTO;
import ru.alexds.ccoshop.dto.UserDTO;
import ru.alexds.ccoshop.entity.Category;
import ru.alexds.ccoshop.entity.Product;
import ru.alexds.ccoshop.repository.ProductRepository;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecommendationService {
    private final ProductRepository productRepository;
    private final UserService userService;
    private final OrderService orderService;
    private final ProductService productService;

    private DataModel dataModel;
    private final List<ARTCluster> artClusters = new ArrayList<>();
    private static final double VIGILANCE_PARAMETER = 0.8;

    @Data
    @AllArgsConstructor
    private static class ARTCluster {
        private double[] weights;
        private List<Long> userIds;

        public ARTCluster(double[] weights) {
            this.weights = weights;
            this.userIds = new ArrayList<>();
        }

        public double match(double[] input) {
            double dotProduct = 0;
            double inputMagnitude = 0;
            for (int i = 0; i < input.length; i++) {
                dotProduct += input[i] * weights[i];
                inputMagnitude += input[i] * input[i];
            }
            return inputMagnitude == 0 ? 0 : dotProduct / Math.sqrt(inputMagnitude);
        }

        public void adapt(double[] input, double learningRate) {
            for (int i = 0; i < weights.length; i++) {
                weights[i] = weights[i] + learningRate * (input[i] - weights[i]);
            }
        }
    }

    @PostConstruct
    public void init() throws Exception {
        dataModel = new FileDataModel(new File("src/main/resources/data/ratings.csv"));
    }

    /**
     * DTO для рекомендаций
     */
    @Data
    @Builder
    public static class RecommendationDTO {
        private Long productId;
        private String productName;
        private BigDecimal price;
        private Double score;
    }

    /**
     * Генерация User-Based рекомендаций с кэшированием
     */
    @Cacheable(value = "user_recommendations", key = "#userId + '_' + #minPrice + '_' + #maxPrice")
    public List<RecommendationDTO> getUserBasedRecommendations(Long userId, int numRecommendations,
                                                               Double minPrice, Double maxPrice) throws TasteException {
        UserSimilarity similarity = new PearsonCorrelationSimilarity(dataModel);
        UserNeighborhood neighborhood = new NearestNUserNeighborhood(5, similarity, dataModel);
        GenericUserBasedRecommender recommender = new GenericUserBasedRecommender(dataModel, neighborhood, similarity);
        List<RecommendedItem> recommendations = recommender.recommend(userId, numRecommendations);

        return mapRecommendationsToDTO(recommendations, minPrice, maxPrice);
    }

    /**
     * Генерация Item-Based рекомендаций с кэшированием
     */
    @Cacheable(value = "item_recommendations", key = "#productId")
    public List<RecommendationDTO> getItemBasedRecommendations(Long productId, int numRecommendations)
            throws TasteException {
        ItemSimilarity itemSimilarity = new TanimotoCoefficientSimilarity(dataModel);
        GenericItemBasedRecommender recommender = new GenericItemBasedRecommender(dataModel, itemSimilarity);
        List<RecommendedItem> recommendations = recommender.mostSimilarItems(productId, numRecommendations);
        return mapRecommendationsToDTO(recommendations, null, null);
    }

    /**
     * ART1 обучение и рекомендации
     */
    public List<RecommendationDTO> getARTRecommendations(Long userId) {
        double[] userVector = createUserVector(userId);
        ARTCluster bestCluster = findBestCluster(userVector);

        if (bestCluster == null) {
            bestCluster = new ARTCluster(userVector);
            artClusters.add(bestCluster);
        } else {
            bestCluster.adapt(userVector, 0.2);
        }
        bestCluster.getUserIds().add(userId);

        return generateRecommendationsFromCluster(bestCluster);
    }

    /**
     * Получить гибридные рекомендации
     */
    public List<RecommendationDTO> getHybridRecommendations(Long userId) {
        Optional<UserDTO> userOptional = userService.getUserById(userId);
        if (userOptional.isEmpty()) {
            return Collections.emptyList();
        }

        // Пробуем получить рекомендации через ART
        List<RecommendationDTO> artRecommendations = getARTRecommendations(userId);
        if (!artRecommendations.isEmpty()) {
            return artRecommendations;
        }

        // Fallback на обычные рекомендации
        try {
            return getUserBasedRecommendations(userId, 3, null, null);
        } catch (TasteException e) {
            return productService.getPopularProducts().stream()
                    .map(this::convertToRecommendationDTO)
                    .collect(Collectors.toList());
        }
    }

    // Вспомогательные методы
    private List<RecommendationDTO> mapRecommendationsToDTO(List<RecommendedItem> recommendations,
                                                            Double minPrice, Double maxPrice) {
        return recommendations.stream()
                .map(rec -> productRepository.findById(rec.getItemID())
                        .map(product -> RecommendationDTO.builder()
                                .productId(product.getId())
                                .productName(product.getName())
                                .price(product.getPrice())
                                .score((double) rec.getValue())
                                .build()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .filter(dto -> isPriceInRange(dto.getPrice(), minPrice, maxPrice))
                .collect(Collectors.toList());
    }

    private boolean isPriceInRange(BigDecimal price, Double minPrice, Double maxPrice) {
        if (price == null) return false;
        boolean isAboveMin = minPrice == null || price.compareTo(BigDecimal.valueOf(minPrice)) >= 0;
        boolean isBelowMax = maxPrice == null || price.compareTo(BigDecimal.valueOf(maxPrice)) <= 0;
        return isAboveMin && isBelowMax;
    }

    private double[] createUserVector(Long userId) {
        List<Product> purchasedProducts = orderService.getPurchasedProducts(userId);
        int vectorSize = (int) productRepository.count();
        double[] vector = new double[vectorSize];

        for (Product product : purchasedProducts) {
            vector[product.getId().intValue() - 1] = 1.0;
        }
        return vector;
    }

    private ARTCluster findBestCluster(double[] userVector) {
        return artClusters.stream()
                .filter(cluster -> cluster.match(userVector) >= VIGILANCE_PARAMETER)
                .findFirst()
                .orElse(null);
    }

    private List<RecommendationDTO> generateRecommendationsFromCluster(ARTCluster cluster) {
        List<Product> clusterProducts = productRepository.findAll().stream()
                .filter(product -> cluster.getWeights()[product.getId().intValue() - 1] > 0.5)
                .collect(Collectors.toList());

        return clusterProducts.stream()
                .map(this::convertToRecommendationDTO)
                .collect(Collectors.toList());
    }

    private RecommendationDTO convertToRecommendationDTO(Product product) {
        return RecommendationDTO.builder()
                .productId(product.getId())
                .productName(product.getName())
                .price(product.getPrice())
                .build();
    }

    private RecommendationDTO convertToRecommendationDTO(ProductDTO productDTO) {
        return RecommendationDTO.builder()
                .productId(productDTO.getId())
                .productName(productDTO.getName())
                .price(productDTO.getPrice())
                .build();
    }
}