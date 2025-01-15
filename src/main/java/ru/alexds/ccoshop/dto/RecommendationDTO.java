package ru.alexds.ccoshop.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class RecommendationDTO {
    private Long itemId;        // Идентификатор рекомендованного товара
    private String itemName;    // Название товара (или описание)
    private double rating;      // Предполагаемая оценка (рейтинг) товара для пользователя
}
