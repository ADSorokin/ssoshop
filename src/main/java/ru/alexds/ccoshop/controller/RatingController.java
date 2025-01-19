package ru.alexds.ccoshop.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.alexds.ccoshop.dto.RatingDTO;
import ru.alexds.ccoshop.entity.Rating;
import ru.alexds.ccoshop.service.RatingService;

@RestController
@RequestMapping("/api/ratings")
@RequiredArgsConstructor
public class RatingController {

    private final RatingService ratingService;
    @Operation(summary = "Изменение рейтинга товара после заказа(отзыв)")
    @PostMapping
    public ResponseEntity<Rating> saveRating(@RequestBody RatingDTO ratingDTO) {
        try {
            Rating savedRating = ratingService.saveRating(ratingDTO);
            return ResponseEntity.ok(savedRating);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
