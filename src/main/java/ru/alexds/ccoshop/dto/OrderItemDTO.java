package ru.alexds.ccoshop.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemDTO {
    private Long id; // Уникальный идентификатор OrderItem
    private Long productId; // Идентификатор продукта
    private String productName; // Название продукта (можно добавить при необходимости)
    private Integer quantity; // Количество заказанного товара
    private BigDecimal price; // Цена за единицу товара
    private BigDecimal totalPrice; // Общая стоимость (quantity * price)
}
