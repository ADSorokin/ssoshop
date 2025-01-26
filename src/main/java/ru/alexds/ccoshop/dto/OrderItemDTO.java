/**
 * Пакет для DTO (Data Transfer Objects) приложения.
 */
package ru.alexds.ccoshop.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

/**
 * Класс OrderItemDTO представляет собой DTO (Data Transfer Object), используемый для передачи данных о позиции в заказе между слоями приложения.
 * Этот класс используется для упрощения и защиты передачи данных, избегая передачи ненужных или чувствительных полей.
 * Для удобства использования Lombok генерирует геттеры, сеттеры, equals, hashCode и toString методы,
 * а также полностью параметризованный конструкторы и builder-конструктор.
 */
@Data // Генерирует геттеры, сеттеры, equals, hashCode и toString
@Builder // Генерирует builder-конструктор для удобного создания объектов
@AllArgsConstructor // Генерирует конструктор с аргументами
@NoArgsConstructor // Генерирует пустой конструктор
public class OrderItemDTO {

    /**
     * Уникальный идентификатор позиции в заказе.
     * Это уникальный идентификатор записи, представляющей позицию в заказе.
     */
    private Long id; // Уникальный идентификатор OrderItem

    /**
     * Идентификатор заказа, к которому относится данная позиция.
     * Это уникальный идентификатор заказа, содержащего данную позицию.
     */
    private Long orderId;

    /**
     * Идентификатор продукта.
     * Это уникальный идентификатор продукта, который был добавлен в заказ.
     */
    private Long productId; // Идентификатор продукта

    /**
     * Название продукта.
     * Это строковое значение, представляющее название продукта, добавленного в заказ.
     * Может быть добавлено при необходимости для более детального представления информации о товаре.
     */
    private String productName; // Название продукта (можно добавить при необходимости)

    /**
     * Количество заказанного товара.
     * Это целочисленное значение, представляющее количество единиц данного продукта, добавленного в заказ.
     */
    private Integer quantity; // Количество заказанного товара

    /**
     * Цена за единицу товара.
     * Это числовое значение типа BigDecimal, представляющее цену одной единицы данного продукта на момент добавления в заказ.
     */
    private BigDecimal price; // Цена за единицу товара

    /**
     * Общая стоимость позиции в заказе.
     * Это числовое значение типа BigDecimal, представляющее общую стоимость данной позиции в заказе (количество * цена).
     */
    private BigDecimal totalPrice; // Общая стоимость (quantity * price)

    /**
     * Конструктор для создания объекта OrderItemDTO с минимальным набором данных.
     *
     * @param id          Уникальный идентификатор позиции в заказе.
     * @param orderId     Идентификатор заказа, к которому относится данная позиция.
     * @param productId   Идентификатор продукта.
     * @param productName Название продукта.
     * @param quantity    Количество заказанного товара.
     * @param price       Цена за единицу товара.
     */
    public OrderItemDTO(Long id, Long orderId, Long productId, String productName, Integer quantity, BigDecimal price) {
        this.id = id;
        this.orderId = orderId;
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.price = price;
        this.totalPrice = calculateTotalPrice();
    }

    /**
     * Метод для вычисления общей стоимости позиции в заказе.
     *
     * @return Общая стоимость позиции в заказе, рассчитанная как произведение количества и цены за единицу товара.
     */
    private BigDecimal calculateTotalPrice() {
        if (this.price != null && this.quantity != null) {
            return this.price.multiply(BigDecimal.valueOf(this.quantity));
        }
        return BigDecimal.ZERO;
    }
}