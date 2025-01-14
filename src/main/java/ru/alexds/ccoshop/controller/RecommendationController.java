package ru.alexds.ccoshop.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.mahout.cf.taste.common.TasteException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.alexds.ccoshop.dto.RecommendationDTO;
import ru.alexds.ccoshop.service.RecommendationService;

import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping("/api/v1/recommendations")
@RequiredArgsConstructor
@Tag(name = "Recommendation Controller", description = "API для получения рекомендаций")
public class RecommendationController {

    private final RecommendationService recommendationService;

    @Operation(summary = "Получить рекомендации на основе пользователя")
    @GetMapping("/user-based/{userId}")
    public ResponseEntity<List<RecommendationDTO>> getUserBasedRecommendations(
            @Parameter(description = "ID пользователя", required = true)
            @PathVariable @Min(1) Long userId,

            @Parameter(description = "Количество рекомендаций")
            @RequestParam(defaultValue = "5") @Min(1) int numRecommendations
    ) {
        try {
            log.debug("Получение рекомендаций для пользователя с ID: {}", userId);
            List<RecommendationDTO> recommendations =
                    recommendationService.getUserBasedRecommendations(userId, numRecommendations);
            return ResponseEntity.ok(recommendations);
        } catch (TasteException e) {
            log.error("Ошибка при получении рекомендаций для пользователя {}: {}", userId, e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Получить рекомендации на основе товара")
    @GetMapping("/item-based/{itemId}")
    public ResponseEntity<List<RecommendationDTO>> getItemBasedRecommendations(
            @Parameter(description = "ID товара", required = true)
            @PathVariable @Min(1) Long itemId,

            @Parameter(description = "Количество рекомендаций")
            @RequestParam(defaultValue = "5") @Min(1) int numRecommendations
    ) {
        try {
            log.debug("Получение рекомендаций для товара с ID: {}", itemId);
            List<RecommendationDTO> recommendations =
                    recommendationService.getItemBasedRecommendations(itemId, numRecommendations);
            return ResponseEntity.ok(recommendations);
        } catch (TasteException e) {
            log.error("Ошибка при получении рекомендаций для товара {}: {}", itemId, e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Получить ART рекомендации")
    @GetMapping("/art/{userId}")
    public ResponseEntity<List<RecommendationDTO>> getARTRecommendations(
            @Parameter(description = "ID пользователя", required = true)
            @PathVariable @Min(1) Long userId
    ) {
        try {
            log.debug("Получение ART рекомендаций для пользователя с ID: {}", userId);
            List<RecommendationDTO> recommendations = recommendationService.getARTRecommendations(userId);
            return ResponseEntity.ok(recommendations);
        } catch (Exception e) {
            log.error("Ошибка при получении ART рекомендаций для пользователя {}: {}", userId, e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Получить гибридные рекомендации")
    @GetMapping("/hybrid/{userId}")
    public ResponseEntity<List<RecommendationDTO>> getHybridRecommendations(
            @Parameter(description = "ID пользователя", required = true)
            @PathVariable @Min(1) Long userId,

            @Parameter(description = "Количество рекомендаций")
            @RequestParam(defaultValue = "5") @Min(1) int numRecommendations
    ) {
        try {
            log.debug("Получение гибридных рекомендаций для пользователя с ID: {}", userId);

            // Получаем рекомендации разных типов
            List<RecommendationDTO> userBased =
                    recommendationService.getUserBasedRecommendations(userId, numRecommendations);
            List<RecommendationDTO> artBased =
                    recommendationService.getARTRecommendations(userId);

            // Здесь можно добавить логику объединения рекомендаций
            // Например, взять топ-N из каждого типа рекомендаций

            return ResponseEntity.ok(userBased); // Временно возвращаем только user-based
        } catch (Exception e) {
            log.error("Ошибка при получении гибридных рекомендаций для пользователя {}: {}",
                    userId, e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        log.error("Необработанная ошибка: ", e);
        return ResponseEntity
                .internalServerError()
                .body(new ErrorResponse(505, "Внутренняя ошибка сервера", e.getMessage()));
    }
}


//    @Operation(summary = "Получить гибридные рекомендации")
//    @GetMapping("/hybrid/{userId}")
//    public ResponseEntity<List<RecommendationDTO>> getHybridRecommendations(
//            @Parameter(description = "ID пользователя", required = true)
//            @PathVariable @Min(1) Long userId,
//
//            @Parameter(description = "Количество рекомендаций")
//            @RequestParam(defaultValue = "5") @Min(1) int numRecommendations
//    ) {
//        try {
//            log.debug("Получение гибридных рекомендаций для пользователя с ID: {}", userId);
//
//            // Получаем рекомендации разных типов
//            List<RecommendationDTO> userBased =
//                    recommendationService.getUserBasedRecommendations(userId, numRecommendations);
//            List<RecommendationDTO> artBased =
//                    recommendationService.getARTRecommendations(userId);
//
//            // Здесь можно добавить логику объединения рекомендаций
//            // Например, взять топ-N из каждого типа рекомендаций
//
//            return ResponseEntity.ok(userBased); // Временно возвращаем только user-based
//        } catch (Exception e) {
//            log.error("Ошибка при получении гибридных рекомендаций для пользователя {}: {}",
//                    userId, e.getMessage());
//            return ResponseEntity.badRequest().build();
//        }
//    }


//@Data
//@AllArgsConstructor
//class ErrorResponse {
//    private String error;
//    private String message;
//}
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import ru.alexds.ccoshop.entity.Product;
//import ru.alexds.ccoshop.service.RecommendationService;
//
//import java.util.List;

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
