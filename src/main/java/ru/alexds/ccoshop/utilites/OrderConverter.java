package ru.alexds.ccoshop.utilites;

//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Component;
//import ru.alexds.ccoshop.dto.OrderDTO;
//import ru.alexds.ccoshop.dto.OrderItemDTO;
//import ru.alexds.ccoshop.entity.Order;
//import ru.alexds.ccoshop.entity.OrderItem;
//
//import java.util.stream.Collectors;
//
//@Component
//@RequiredArgsConstructor
//public class OrderConverter {
//
//    public OrderDTO toDTO(Order order) {
//        if (order == null) {
//            return null;
//        }
//
//        return OrderDTO.builder()
//                .id(order.getId())
//                .userId(order.getUser().getId())
//                .items(order.getItems().stream()
//                        .map(this::toDTO)
//                        .collect(Collectors.toList()))
//                .orderDate(order.getOrderDate())
//                .status(order.getStatus())
//                .totalPrice(order.getTotalPrice())
//                .build();
//    }
//
//    public OrderItemDTO toDTO(OrderItem orderItem) {
//        if (orderItem == null) {
//            return null;
//        }
//
//        return OrderItemDTO.builder()
//                .id(orderItem.getId())
//                .orderId(orderItem.getOrder().getId())
//                .productId(orderItem.getProduct().getId())
//                .productName(orderItem.getProduct().getName())
//                .quantity(orderItem.getQuantity())
//                .price(orderItem.getPrice())
////                .totalPrice(orderItem.getTotalPrice())
//                .build();
//    }
//
//    public OrderItem toEntity(OrderItemDTO dto) {
//        if (dto == null) {
//            return null;
//        }
//
//        return OrderItem.builder()
//                .id(dto.getId())
//                .quantity(dto.getQuantity())
//                .price(dto.getPrice())
//                .build();
//    }
//}
