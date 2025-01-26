package ru.alexds.ccoshop.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.alexds.ccoshop.dto.RatingDTO;
import ru.alexds.ccoshop.entity.ErrorResponse;
import ru.alexds.ccoshop.entity.Rating;
import ru.alexds.ccoshop.service.RatingService;

/**
 * Контроллер для управления рейтингами.
 * Обеспечивает API для сохранения рейтингов товаров после заказа (отзывы).
 */
@Slf4j
@RestController
@RequestMapping("/api/ratings")
@RequiredArgsConstructor
@Validated
@Tag(name = "Rating controller", description = "Работа с рейтингами")
public class RatingController {
    private final RatingService ratingService; // Сервис для управления рейтингами

    /**
     * Сохраняет новый рейтинг товара после заказа (отзыв).
     *
     * @param ratingDTO DTO объект с информацией о новом рейтинге (идентификатор пользователя, идентификатор товара и значение рейтинга)
     * @return HTTP-ответ с сохраненным рейтингом в формате entity и статусом 200 (OK), если рейтинг успешно сохранен,
     * или статусом 400 (Bad Request) при возникновении ошибки
     */
    @Operation(summary = "Изменение рейтинга товара после заказа(отзыв)")
    @PostMapping
    public ResponseEntity<Rating> saveRating(@RequestBody @Valid RatingDTO ratingDTO) {
        log.debug("Request to save new rating: {}", ratingDTO);
        try {
            Rating savedRating = ratingService.saveRating(ratingDTO);
            log.info("Rating successfully saved for user ID {} and item ID {}: {}",
                    ratingDTO.getUserId(), ratingDTO.getItemId(), savedRating);
            return ResponseEntity.ok(savedRating);
        } catch (IllegalArgumentException e) {
            log.error("Invalid data provided for saving rating: {}", e.getMessage());
            ErrorResponse errorResponse = new ErrorResponse(
                    HttpStatus.BAD_REQUEST.value(),
                    "INVALID_DATA",
                    "Invalid data provided for saving rating: " + e.getMessage()
            );
            return ResponseEntity.badRequest().body(null); // Возвращаем null body, так как метод возвращает Rating
        } catch (Exception e) {
            log.error("Unexpected error occurred while saving rating: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}