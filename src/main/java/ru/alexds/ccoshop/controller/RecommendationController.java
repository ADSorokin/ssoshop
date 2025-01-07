package ru.alexds.ccoshop.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.alexds.ccoshop.entity.Product;
import ru.alexds.ccoshop.service.RecommendationService;

import java.util.List;

@RestController
@RequestMapping("/api/recommendations")
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationService recommendationService;

    /**
     * Получить User-Based рекомендации для пользователя.
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Product>> getUserBasedRecommendations(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "5") int numRecommendations,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice
    ) {
        try {
            List<Product> recommendations = recommendationService.getUserBasedRecommendations(userId, numRecommendations, minPrice, maxPrice);

            if (recommendations.isEmpty()) {
                return ResponseEntity.noContent().build();
            }

            return ResponseEntity.ok(recommendations);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(null);
        }
    }

    /**
     * Получить Item-Based рекомендации для товара.
     */
    @GetMapping("/item/{productId}")
    public ResponseEntity<List<Product>> getItemBasedRecommendations(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "5") int numRecommendations
    ) {
        try {
            List<Product> recommendations = recommendationService.getItemBasedRecommendations(productId, numRecommendations);

            if (recommendations.isEmpty()) {
                return ResponseEntity.noContent().build();
            }

            return ResponseEntity.ok(recommendations);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(null);
        }
    }

    /**
     * Получить гибридные рекомендации для пользователя.
     */
    @GetMapping("/hybrid/{userId}")
    public ResponseEntity<List<Product>> getHybridRecommendations(@PathVariable Long userId) {
        try {
            List<Product> recommendations = recommendationService.getHybridRecommendations(userId);

            if (recommendations.isEmpty()) {
                return ResponseEntity.noContent().build();
            }

            return ResponseEntity.ok(recommendations);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(null);
        }
    }

    /**
     * Пинг для проверки на работоспособность сервиса рекомендаций.
     */
    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok("Recommendation API is working!");
    }
}
