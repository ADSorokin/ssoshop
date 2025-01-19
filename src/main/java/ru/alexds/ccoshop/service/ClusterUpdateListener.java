//package ru.alexds.ccoshop.service;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.context.event.EventListener;
//import org.springframework.stereotype.Component;
//import ru.alexds.ccoshop.entity.Order;
//import ru.alexds.ccoshop.repository.OrderRepository;
//
//@Component
//@RequiredArgsConstructor
//public class ClusterUpdateListener {
//
//    private final ARTClusterService clusterService;
//    private final OrderRepository orderRepository;
//
//    @EventListener
//    public void handleOrderCreated(OrderService.OrderCreatedEvent event) {
//        Long orderId = event.getOrderId();
//        Order order = orderRepository.findById(orderId)
//                .orElseThrow(() -> new RuntimeException("Order not found"));
//
//        // Получаем ID пользователя и общий вес для добавления в кластер
//        Long userId = order.getUserId();
//        Double weight = calculateWeight(order);
//// TODO: 19.01.2025 настроить слушатель
//        // Логика привязки к существующему кластеру (в данном случае clusterId = 1)
////        clusterService.populateClusterWithUserAndWeights(1L, userId, weight);
//    }
//
//    private Double calculateWeight(Order order) {
//        return order.getTotalPrice().doubleValue() / order.getItems().size();
//    }
//}