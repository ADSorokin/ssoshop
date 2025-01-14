package ru.alexds.ccoshop.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RatingDTO {
    private Long userId;     // Идентификатор пользователя
    private Long itemId;     // Идентификатор товара
    private Double rating;   // Рейтинг, выставленный пользователем
}
