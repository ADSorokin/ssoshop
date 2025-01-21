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
            @RequestParam(defaultValue = "3") @Min(1) int numRecommendations
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
            @PathVariable @Min(1) Long userId,
            @RequestParam(defaultValue = "3") @Min(1) int numRecommendations
    ) {
        try {
            log.debug("Получение гибридных рекомендаций для пользователя с ID: {}", userId);

            // Получаем User-Based рекомендации
            List<RecommendationDTO> userBased = recommendationService.getUserBasedRecommendations(userId, numRecommendations);

            // Получаем ART-Based рекомендации
            List<RecommendationDTO> artBased = recommendationService.getARTRecommendations(userId);

            // Объединяем рекомендации
            List<RecommendationDTO> combinedRecommendations = recommendationService.mergeRecommendations(
                    userBased, artBased, numRecommendations
            );

            return ResponseEntity.ok(combinedRecommendations);
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

    /**
     * Пинг для проверки на работоспособность сервиса рекомендаций.
     */
    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok("Recommendation API is working!");
    }
}
