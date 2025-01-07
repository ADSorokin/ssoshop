package ru.alexds.ccoshop.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.alexds.ccoshop.entity.Order;
import ru.alexds.ccoshop.entity.Status;
import ru.alexds.ccoshop.service.OrderService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    /**
     * Создать заказ на основе корзины пользователя
     */
    @PostMapping("/from-cart/{userId}")
    public ResponseEntity<Order> createOrderFromCart(@PathVariable Long userId) {
        Order order = orderService.createOrderFromCart(userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(order);
    }

    /**
     * Получить все заказы пользователя
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Order>> getOrdersByUserId(@PathVariable Long userId) {
        List<Order> orders = orderService.getOrdersByUserId(userId);

        // Проверяем, есть ли заказы
        if (orders.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build(); // Возвращаем 204, если заказы отсутствуют
        }

        return ResponseEntity.ok(orders); // Возвращаем список заказов с кодом 200
    }

    /**
     * Получить заказ по ID
     *
     * @param orderId Идентификатор заказа
     * @return ResponseEntity<Order> – заказ, если найден, или 404 если не найден
     */
    @GetMapping("/{orderId}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long orderId) {
        Optional<Order> optionalOrder = orderService.getOrderById(orderId);

        return optionalOrder
                .map(ResponseEntity::ok) // Если заказ найден, возвращаем его
                .orElseGet(() -> ResponseEntity.notFound().build()); // Если не найден, возвращаем 404
    }
    /**
     * Обновить статус заказа
     */
//    @PutMapping("/{orderId}/status")
//    public ResponseEntity<Order> updateOrderStatus(@PathVariable Long orderId, @RequestParam String status) {
//        Order updatedOrder = orderService.updateOrderStatus(orderId, Status.valueOf(status));
//        return ResponseEntity.ok(updatedOrder);
//    }

    /**
     * Удалить заказ
     */
    @DeleteMapping("/{orderId}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long orderId) {
        orderService.deleteOrder(orderId);
        return ResponseEntity.noContent().build();
    }
}
