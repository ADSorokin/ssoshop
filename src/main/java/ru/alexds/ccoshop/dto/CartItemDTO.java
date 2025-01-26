/**
 * Пакет для DTO (Data Transfer Objects) приложения.
 */
package ru.alexds.ccoshop.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.alexds.ccoshop.entity.CartItem;
import java.math.BigDecimal;

/**
 * Класс CartItemDTO представляет собой DTO (Data Transfer Object), используемый для передачи данных о позиции в корзине между слоями приложения.
 * Этот класс используется для упрощения и защиты передачи данных, избегая передачи ненужных или чувствительных полей.
 * Для удобства использования Lombok генерирует геттеры, сеттеры, equals, hashCode и toString методы,
 * а также пустой и полностью параметризованный конструкторы.
 */
@Data // Генерирует геттеры, сеттеры, equals, hashCode и toString
@NoArgsConstructor // Генерирует пустой конструктор
@AllArgsConstructor // Генерирует конструктор с аргументами
@Builder
public class CartItemDTO {

    /**
     * Уникальный идентификатор позиции в корзине.
     * Это уникальный идентификатор записи, представляющей позицию в корзине.
     */
    private Long id;          // ID товара в корзине

    /**
     * Идентификатор пользователя, которому принадлежит корзина.
     * Это уникальный идентификатор пользователя, связанного с данной позицией в корзине.
     */
    private Long userId;      // ID пользователя

    /**
     * Идентификатор продукта, добавленного в корзину.
     * Это уникальный идентификатор продукта, который был добавлен в корзину.
     */
    private Long productId;   // ID продукта

    /**
     * Количество единиц данного продукта в корзине.
     * Это целочисленное значение, представляющее количество единиц данного продукта, добавленного в корзину.
     */
    private Integer quantity; // Количество

    /**
     * Цена за единицу продукта на момент добавления в корзину.
     * Это числовое значение типа BigDecimal, представляющее цену одной единицы данного продукта на момент добавления в корзину.
     */
    private BigDecimal price; // Цена

    /**
     * Конструктор для создания объекта CartItemDTO на основе существующего объекта CartItem.
     *
     * @param cartItem Объект CartItem, из которого будут извлечены данные.
     */
    public CartItemDTO(CartItem cartItem) {
        if (cartItem != null) {
            this.id = cartItem.getId();
            this.userId = cartItem.getUser().getId();  // Предполагается, что у объекта User есть метод getId()
            this.productId = cartItem.getProduct().getId();  // Предполагается, что у объекта Product есть метод getId()
            this.quantity = cartItem.getQuantity();
            this.price = cartItem.getPrice();
        }
    }
}