package ru.alexds.ccoshop.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.alexds.ccoshop.entity.Product;
import ru.alexds.ccoshop.service.RecommendationService;

import java.util.List;






@RestController
@RequestMapping("/api/recommendations")
@RequiredArgsConstructor
@Tag(name = "Recommendations", description = "Рекомендации на основе пользовательских данных, ART1 и гибридные подходы")
public class RecommendationController {

    private final RecommendationService recommendationService;

    /**
     * Получение рекомендаций на основе поведения пользователя (User-Based)
     */
    @GetMapping("/user-based/{userId}")
    public ResponseEntity<List<RecommendationService.RecommendationDTO>> getUserBasedRecommendations(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "10") int numRecommendations,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice) {
        try {
            List<RecommendationService.RecommendationDTO> recommendations = recommendationService.getUserBasedRecommendations(userId, numRecommendations, minPrice, maxPrice);
            return ResponseEntity.ok(recommendations);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Получение рекомендаций на основе похожих товаров (Item-Based)
     */
    @GetMapping("/item-based/{productId}")
    public ResponseEntity<List<RecommendationService.RecommendationDTO>> getItemBasedRecommendations(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "10") int numRecommendations) {
        try {
            List<RecommendationService.RecommendationDTO> recommendations = recommendationService.getItemBasedRecommendations(productId, numRecommendations);
            return ResponseEntity.ok(recommendations);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Получение рекомендаций на основе ART1
     */
    @GetMapping("/art/{userId}")
    public ResponseEntity<List<RecommendationService.RecommendationDTO>> getARTRecommendations(
            @PathVariable Long userId) {
        try {
            List<RecommendationService.RecommendationDTO> recommendations = recommendationService.getARTRecommendations(userId);
            return ResponseEntity.ok(recommendations);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Получение гибридных рекомендаций
     */
    @GetMapping("/hybrid/{userId}")
    public ResponseEntity<List<RecommendationService.RecommendationDTO>> getHybridRecommendations(
            @PathVariable Long userId) {
        try {
            List<RecommendationService.RecommendationDTO> recommendations = recommendationService.getHybridRecommendations(userId);
            return ResponseEntity.ok(recommendations);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}


//@RestController
//@RequestMapping("/api/recommendations")
//@RequiredArgsConstructor
//public class RecommendationController {
//
//    private final RecommendationService recommendationService;
//
//    /**
//     * Получить User-Based рекомендации для пользователя.
//     */
//    @GetMapping("/user/{userId}")
//    public ResponseEntity<List<Product>> getUserBasedRecommendations(
//            @PathVariable Long userId,
//            @RequestParam(defaultValue = "5") int numRecommendations,
//            @RequestParam(required = false) Double minPrice,
//            @RequestParam(required = false) Double maxPrice
//    ) {
//        try {
//            List<Product> recommendations = recommendationService.getUserBasedRecommendations(userId, numRecommendations, minPrice, maxPrice);
//
//            if (recommendations.isEmpty()) {
//                return ResponseEntity.noContent().build();
//            }
//
//            return ResponseEntity.ok(recommendations);
//        } catch (Exception e) {
//            return ResponseEntity.internalServerError().body(null);
//        }
//    }
//
//    /**
//     * Получить Item-Based рекомендации для товара.
//     */
//    @GetMapping("/item/{productId}")
//    public ResponseEntity<List<Product>> getItemBasedRecommendations(
//            @PathVariable Long productId,
//            @RequestParam(defaultValue = "5") int numRecommendations
//    ) {
//        try {
//            List<Product> recommendations = recommendationService.getItemBasedRecommendations(productId, numRecommendations);
//
//            if (recommendations.isEmpty()) {
//                return ResponseEntity.noContent().build();
//            }
//
//            return ResponseEntity.ok(recommendations);
//        } catch (Exception e) {
//            return ResponseEntity.internalServerError().body(null);
//        }
//    }
//
//    /**
//     * Получить гибридные рекомендации для пользователя.
//     */
//    @GetMapping("/hybrid/{userId}")
//    public ResponseEntity<List<Product>> getHybridRecommendations(@PathVariable Long userId) {
//        try {
//            List<Product> recommendations = recommendationService.getHybridRecommendations(userId);
//
//            if (recommendations.isEmpty()) {
//                return ResponseEntity.noContent().build();
//            }
//
//            return ResponseEntity.ok(recommendations);
//        } catch (Exception e) {
//            return ResponseEntity.internalServerError().body(null);
//        }
//    }
//
//    /**
//     * Пинг для проверки на работоспособность сервиса рекомендаций.
//     */
//    @GetMapping("/ping")
//    public ResponseEntity<String> ping() {
//        return ResponseEntity.ok("Recommendation API is working!");
//    }
//}
