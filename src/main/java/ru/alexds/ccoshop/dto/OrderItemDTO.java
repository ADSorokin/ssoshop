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

    private Long orderId;
    private Long productId; // Идентификатор продукта
    private String productName; // Название продукта (можно добавить при необходимости)
    private Integer quantity; // Количество заказанного товара
    private BigDecimal price; // Цена за единицу товара
    private BigDecimal totalPrice; // Общая стоимость (quantity * price)



    public OrderItemDTO(Long id, Long orderId, Long productId, String productName, Integer quantity, BigDecimal price) {
        this.id = id;
        this.orderId =orderId;
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.price = price;

    }



}
