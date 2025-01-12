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

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {
    private Long id;
    private Long userId;
    private List<OrderItemDTO> items;
    private LocalDateTime orderDate;
    private Status status;
    private BigDecimal totalPrice;




    public OrderDTO(Order order) {
        this.id = order.getId();
        this.userId = order.getId();
        this.items = convertToOrderItemDTOList(order.getItems());
        this.orderDate = order.getOrderDate();
        this.status = order.getStatus();
        this.totalPrice =order.calculateTotalPrice();
    }

    public List<OrderItemDTO> convertToOrderItemDTOList(List<OrderItem> orderItems) {
        return orderItems.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private OrderItemDTO convertToDTO(OrderItem orderItem) {
        return OrderItemDTO.builder()
                .id(orderItem.getId())
                .productId(orderItem.getProduct().getId())
                .productName(orderItem.getProduct().getName())
                .quantity(orderItem.getQuantity())
                .price(orderItem.getPrice())
                .build();
    }

}





