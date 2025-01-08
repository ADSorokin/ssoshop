package ru.alexds.ccoshop.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.alexds.ccoshop.entity.Order;
import ru.alexds.ccoshop.entity.Status;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {
    private Long id;
    private Long userId; // Ссылка на пользователя
    private BigDecimal totalAmount;
    private LocalDateTime orderDate;
    private Status status; // Можно использовать перечисление за пределами DTO

    public OrderDTO(Order order) {
        this.id = order.getId();
        this.userId = order.getUser().getId();
        this.totalAmount = order.getTotalPrice();
        this.orderDate = order.getOrderDate();
        this.status = order.getStatus(); // Если у вас есть Enum для статусов заказа
    }
}
