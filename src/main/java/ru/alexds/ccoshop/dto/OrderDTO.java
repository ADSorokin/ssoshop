/**
 * Пакет для DTO (Data Transfer Objects) приложения.
 */
package ru.alexds.ccoshop.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.alexds.ccoshop.entity.Order;
import ru.alexds.ccoshop.entity.OrderItem;
import ru.alexds.ccoshop.entity.Status;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Класс OrderDTO представляет собой DTO (Data Transfer Object), используемый для передачи данных о заказе между слоями приложения.
 * Этот класс используется для упрощения и защиты передачи данных, избегая передачи ненужных или чувствительных полей.
 * Для удобства использования Lombok генерирует геттеры, сеттеры, equals, hashCode и toString методы,
 * а также полностью параметризованный конструкторы и builder-конструктор.
 */
@Data // Генерирует геттеры, сеттеры, equals, hashCode и toString
@Builder // Генерирует builder-конструктор для удобного создания объектов
@NoArgsConstructor // Генерирует пустой конструктор
@AllArgsConstructor // Генерирует конструктор с аргументами
public class OrderDTO {

    /**
     * Уникальный идентификатор заказа.
     * Это уникальный идентификатор записи, представляющей заказ.
     */
    private Long id;

    /**
     * Идентификатор пользователя, который сделал заказ.
     * Это уникальный идентификатор пользователя, связанного с данным заказом.
     */
    private Long userId;

    /**
     * Список позиций в данном заказе.
     * Это список объектов типа OrderItemDTO, представляющих продукты, добавленные в данный заказ.
     */
    private List<OrderItemDTO> items;

    /**
     * Дата и время оформления заказа.
     * Это дата и время, когда заказ был создан.
     */
    private LocalDateTime orderDate;

    /**
     * Статус заказа.
     * Это перечисление, представляющее текущее состояние заказа (например, "NEW", "PAID", "SHIPPED").
     */
    private Status status;

    /**
     * Общая стоимость заказа.
     * Это числовое значение типа BigDecimal, представляющее общую стоимость всех позиций в заказе.
     */
    private BigDecimal totalPrice;

    /**
     * Конструктор для создания объекта OrderDTO на основе существующего объекта Order.
     *
     * @param order Объект Order, из которого будут извлечены данные.
     */
    public OrderDTO(Order order) {
        this.id = order.getId();
        this.userId = order.getUser().getId(); // Предполагается, что есть связь с пользователем через метод getId()
        this.items = convertToOrderItemDTOList(order.getItems());
        this.orderDate = order.getOrderDate();
        this.status = order.getStatus();
        this.totalPrice = order.calculateTotalPrice();
    }

    /**
     * Метод для преобразования списка позиций в заказе (OrderItem) в список DTO (OrderItemDTO).
     *
     * @param orderItems Список позиций в заказе.
     * @return Преобразованный список позиций в формате DTO.
     */
    public List<OrderItemDTO> convertToOrderItemDTOList(List<OrderItem> orderItems) {
        return orderItems.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Метод для преобразования одной позиции в заказе (OrderItem) в DTO (OrderItemDTO).
     *
     * @param orderItem Позиция в заказе.
     * @return Преобразованная позиция в формате DTO.
     */
    private OrderItemDTO convertToDTO(OrderItem orderItem) {
        return OrderItemDTO.builder()
                .id(orderItem.getId())
                .orderId(orderItem.getOrder().getId())
                .productId(orderItem.getProduct().getId())
                .productName(orderItem.getProduct().getName())
                .quantity(orderItem.getQuantity())
                .price(orderItem.getPrice())
                .totalPrice(calculateTotalPrice(orderItem))

                .build();
    }

    /**
     * Метод для вычисления общей стоимости позиции в заказе.
     *
     * @return Общая стоимость позиции в заказе, рассчитанная как произведение количества и цены за единицу товара.
     */
    private BigDecimal calculateTotalPrice(OrderItem orderItem) {
        if (orderItem.getPrice() != null && orderItem.getQuantity() != null) {
            return orderItem.getPrice().multiply(BigDecimal.valueOf(orderItem.getQuantity()));
        }
        return BigDecimal.ZERO;
    }
}

