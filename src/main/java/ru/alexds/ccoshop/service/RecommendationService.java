package ru.alexds.ccoshop.service;


import jakarta.annotation.PostConstruct;
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
import ru.alexds.ccoshop.entity.Category;
import ru.alexds.ccoshop.entity.Product;
import ru.alexds.ccoshop.entity.User;
import ru.alexds.ccoshop.repository.ProductRepository;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final ProductRepository productRepository;
    private DataModel dataModel; // DataModel от Mahout

    private UserService userService;

    private OrderService orderService;

    private ProductService productService;

    @PostConstruct
    public void init() throws Exception {
        dataModel = new FileDataModel(new File("src/main/resources/data/ratings.csv"));
    }

    /**
     * Генерация User-Based рекомендаций с кэшированием.
     */
    @Cacheable(value = "user_recommendations", key = "#userId + '_' + #minPrice + '_' + #maxPrice")
    public List<Product> getUserBasedRecommendations(Long userId, int numRecommendations, Double minPrice, Double maxPrice) throws TasteException {
        UserSimilarity similarity = new PearsonCorrelationSimilarity(dataModel);
        UserNeighborhood neighborhood = new NearestNUserNeighborhood(5, similarity, dataModel);
        GenericUserBasedRecommender recommender = new GenericUserBasedRecommender(dataModel, neighborhood, similarity);
        List<RecommendedItem> recommendations = recommender.recommend(userId, numRecommendations);

        List<Product> products = mapRecommendationsToProducts(recommendations);
        return filterRecommendations(products, minPrice, maxPrice);
    }

    /**
     * Генерация Item-Based рекомендаций с кэшированием.
     */
    @Cacheable(value = "item_recommendations", key = "#productId")
    public List<Product> getItemBasedRecommendations(Long productId, int numRecommendations) throws TasteException {
        ItemSimilarity itemSimilarity = new TanimotoCoefficientSimilarity(dataModel);
        GenericItemBasedRecommender recommender = new GenericItemBasedRecommender(dataModel, itemSimilarity);
        List<RecommendedItem> recommendations = recommender.mostSimilarItems(productId, numRecommendations);
        return mapRecommendationsToProducts(recommendations);
    }

    /**
     * Фильтрация продуктов по цене.
     * @param products список продуктов
     * @param minPrice минимальная цена (может быть null)
     * @param maxPrice максимальная цена (может быть null)
     * @return отфильтрованный список продуктов
     */
    private List<Product> filterRecommendations(List<Product> products, Double minPrice, Double maxPrice) {
        if (products == null || products.isEmpty()) {
            return products;
        }

        return products.stream()
                .filter(product -> product.getPrice() != null &&
                        isPriceInRange(product.getPrice(), minPrice, maxPrice))
                .toList();
    }

    /**
     * Проверяет, находится ли цена в заданном диапазоне
     */
    private boolean isPriceInRange(BigDecimal price, Double minPrice, Double maxPrice) {
        if (price == null) {
            return false;
        }

        boolean isAboveMinPrice = minPrice == null ||
                price.compareTo(BigDecimal.valueOf(minPrice)) >= 0;
        boolean isBelowMaxPrice = maxPrice == null ||
                price.compareTo(BigDecimal.valueOf(maxPrice)) <= 0;

        return isAboveMinPrice && isBelowMaxPrice;
    }

    /**
     * Преобразование результатов Mahout в список объектов Product.
     */
    private List<Product> mapRecommendationsToProducts(List<RecommendedItem> recommendations) {
        List<Product> products = new ArrayList<>();
        for (RecommendedItem recommendation : recommendations) {
            productRepository.findById(recommendation.getItemID())
                    .ifPresent(products::add);
        }
        return products;
    }

    /**
     * Получить гибридные рекомендации продуктов для пользователя.
     *
     * @param userId идентификатор пользователя
     * @return список рекомендованных продуктов
     */
    public List<Product> getHybridRecommendations(Long userId) {
        // 1. Получаем пользователя

        Optional<User> userOptional = userService.getUserById(userId);
        if (userOptional.isEmpty()) {
            return List.of(); // Возвращаем пустой список, если пользователь не найден
        }
        User user = userOptional.get();

        // 2. Получаем список ранее приобретенных продуктов пользователя

        List<Product> purchasedProducts = orderService.getPurchasedProducts(userId);

        // 3. Если у пользователя нет приобретенных продуктов, можно возвращать популярных продуктов

        if (purchasedProducts.isEmpty()) {
            return productService.getPopularProducts(); // Метод для получения популярных продуктов
        }

        // 4. Извлекаем предпочтенные категории или другие параметры
        List<Category> preferredCategories = getPreferredCategories(user, purchasedProducts);

        // 5. Фильтруем продукты на основе предпочтений
        List<Product> recommendedProducts = productService.getAllProducts();

        // 6. Применяем фильтрацию на основе категории, цены и других критериев
        // (например, показываем только те продукты, которые принадлежат к предпочтительным категориям)
        recommendedProducts = recommendedProducts.stream()
                .filter(product -> preferredCategories.contains(product.getCategory()))
                .toList();

        // 7. Возможно, сортируем результаты для улучшения качества рекомендаций
        recommendedProducts.sort((p1, p2) ->
                p1.getPopularity().compareTo(p2.getPopularity())); // Пример сортировки по популярности

        return recommendedProducts;
    }

    // Пример метода для получения предпочтительных категорий пользователя
    private List<Category> getPreferredCategories(User user, List<Product> purchasedProducts) {
        // Логика для извлечения предпочтительных категорий на основе приобретенных продуктов
        return purchasedProducts.stream()
                .map(Product::getCategory)
                .distinct()
                .toList();
    }
}

