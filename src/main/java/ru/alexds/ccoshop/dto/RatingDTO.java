package ru.alexds.ccoshop.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RatingDTO {
    @NotEmpty(message = "Rating Id cannot be empty")
    private Long userId;     // Идентификатор пользователя
    @NotEmpty(message = "Item Id cannot be empty")
    private Long itemId;     // Идентификатор товара

    private Double rating;   // Рейтинг, выставленный пользователем
}
