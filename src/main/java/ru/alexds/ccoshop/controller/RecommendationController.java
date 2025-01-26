package ru.alexds.ccoshop.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.mahout.cf.taste.common.TasteException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import ru.alexds.ccoshop.dto.RecommendationDTO;
import ru.alexds.ccoshop.entity.ErrorResponse;
import ru.alexds.ccoshop.service.RecommendationService;

import java.util.List;


/**
 * Контроллер для управления рекомендациями.
 * Обеспечивает API для получения User-Based, Item-Based и ART-Based рекомендаций,
 * а также гибридных рекомендаций и проверки работоспособности сервиса.
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/api/v1/recommendations")
@RequiredArgsConstructor
@Tag(name = "Recommendation Controller", description = "API для получения рекомендаций")
public class RecommendationController {
    private final RecommendationService recommendationService; // Сервис для управления рекомендациями

    /**
     * Получает рекомендации на основе поведения пользователя (User-Based).
     *
     * @param userId         Идентификатор пользователя, для которого необходимо сформировать рекомендации
     * @param numRecommendations Количество рекомендаций для возврата
     * @return HTTP-ответ со списком рекомендаций в формате DTO и статусом 200 (OK)
     * @throws TasteException если произошла ошибка при вычислении рекомендаций
     */
    @Operation(summary = "Получить рекомендации на основе пользователя")
    @GetMapping("/user-based/{userId}")
    public ResponseEntity<List<RecommendationDTO>> getUserBasedRecommendations(
            @Parameter(description = "ID пользователя", required = true)
            @PathVariable @Min(1) Long userId,
            @Parameter(description = "Количество рекомендаций")
            @RequestParam(defaultValue = "5") @Min(1) int numRecommendations
    ) {
        log.debug("Request to get user-based recommendations for user ID: {}", userId);
        try {
            List<RecommendationDTO> recommendations =
                    recommendationService.getUserBasedRecommendations(userId, numRecommendations);
            return ResponseEntity.ok(recommendations);
        } catch (TasteException e) {
            log.error("Error getting user-based recommendations for user {}: {}", userId, e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Получает рекомендации на основе товара (Item-Based).
     *
     * @param itemId         Идентификатор товара, для которого необходимо получить похожие товары
     * @param numRecommendations Количество рекомендаций для возврата
     * @return HTTP-ответ со списком рекомендаций в формате DTO и статусом 200 (OK)
     * @throws TasteException если произошла ошибка при вычислении рекомендаций
     */
    @Operation(summary = "Получить рекомендации на основе товара")
    @GetMapping("/item-based/{itemId}")
    public ResponseEntity<List<RecommendationDTO>> getItemBasedRecommendations(
            @Parameter(description = "ID товара", required = true)
            @PathVariable @Min(1) Long itemId,
            @Parameter(description = "Количество рекомендаций")
            @RequestParam(defaultValue = "3") @Min(1) int numRecommendations
    ) {
        log.debug("Request to get item-based recommendations for item ID: {}", itemId);
        try {
            List<RecommendationDTO> recommendations =
                    recommendationService.getItemBasedRecommendations(itemId, numRecommendations);
            return ResponseEntity.ok(recommendations);
        } catch (TasteException e) {
            log.error("Error getting item-based recommendations for item {}: {}", itemId, e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Получает рекомендации на основе кластеризации пользователей (ART-Based).
     *
     * @param userId Идентификатор пользователя, для которого необходимо сформировать рекомендации
     * @return HTTP-ответ со списком рекомендаций в формате DTO и статусом 200 (OK)
     * @throws Exception если произошла ошибка при вычислении рекомендаций
     */
    @Operation(summary = "Получить ART рекомендации")
    @GetMapping("/art/{userId}")
    public ResponseEntity<List<RecommendationDTO>> getARTRecommendations(
            @Parameter(description = "ID пользователя", required = true)
            @PathVariable @Min(1) Long userId
    ) {
        log.debug("Request to get ART-based recommendations for user ID: {}", userId);
        try {
            List<RecommendationDTO> recommendations = recommendationService.getARTRecommendations(userId);
            return ResponseEntity.ok(recommendations);
        } catch (Exception e) {
            log.error("Error getting ART-based recommendations for user {}: {}", userId, e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Получает гибридные рекомендации, объединяя User-Based и ART-Based подходы.
     *
     * @param userId         Идентификатор пользователя, для которого необходимо сформировать рекомендации
     * @param numRecommendations Количество рекомендаций для возврата
     * @return HTTP-ответ со списком рекомендаций в формате DTO и статусом 200 (OK)
     * @throws Exception если произошла ошибка при вычислении рекомендаций
     */
    @Operation(summary = "Получить гибридные рекомендации")
    @GetMapping("/hybrid/{userId}")
    public ResponseEntity<List<RecommendationDTO>> getHybridRecommendations(
            @PathVariable @Min(1) Long userId,
            @RequestParam(defaultValue = "3") @Min(1) int numRecommendations
    ) {
        log.debug("Request to get hybrid recommendations for user ID: {}", userId);
        try {
            // Получаем User-Based рекомендации
            List<RecommendationDTO> userBased = recommendationService.getUserBasedRecommendations(userId, numRecommendations);
            // Получаем ART-Based рекомендации
            List<RecommendationDTO> artBased = recommendationService.getARTRecommendations(userId);
            // Объединяем рекомендации
            List<RecommendationDTO> combinedRecommendations = recommendationService.mergeRecommendations(userBased, artBased, numRecommendations);
            return ResponseEntity.ok(combinedRecommendations);
        } catch (Exception e) {
            log.error("Error getting hybrid recommendations for user {}: {}", userId, e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Обработчик общих исключений.
     *
     * @param e Исключение, которое возникает при выполнении операций
     * @return HTTP-ответ с сообщением об ошибке
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        log.error("Uncaught exception occurred: ", e);
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "INTERNAL_SERVER_ERROR", e.getMessage());
        return ResponseEntity.internalServerError().body(errorResponse);
    }

    /**
     * Пинг для проверки работоспособности сервиса рекомендаций.
     *
     * @return HTTP-ответ с сообщением о работоспособности сервиса
     */
    @Operation(summary = "Пинг для проверки на работоспособность сервиса рекомендаций")
    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        log.debug("Ping request received");
        return ResponseEntity.ok("Recommendation API is working!");
    }
}
