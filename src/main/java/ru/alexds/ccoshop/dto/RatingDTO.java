package ru.alexds.ccoshop.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RatingDTO {
    private Long userId;     // Идентификатор пользователя
    private Long itemId;     // Идентификатор товара
    private Double rating;   // Рейтинг, выставленный пользователем
}
