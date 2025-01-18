package ru.alexds.ccoshop.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.alexds.ccoshop.dto.OrderDTO;
import ru.alexds.ccoshop.dto.OrderItemDTO;
import ru.alexds.ccoshop.entity.Status;
import ru.alexds.ccoshop.service.OrderItemService;
import ru.alexds.ccoshop.service.OrderService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final OrderItemService orderItemService;

    /**
     * Создать заказ на основе корзины пользователя
     */

    @Operation(summary = "Создать заказ на основе корзины пользователя")
    @PostMapping("/{userId}/create")
    public ResponseEntity<OrderDTO> createOrderFromCart(@PathVariable Long userId) {
        OrderDTO order = orderService.createOrderFromCart(userId);
        return ResponseEntity.ok(order);
    }

    /**
     * Получить все заказы пользователя
     */
    @Operation(summary = "Получить все заказы пользователя")
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<OrderDTO>> getOrdersByUserId(@PathVariable Long userId) {
        List<OrderDTO> orders = orderService.getOrdersByUserId(userId);

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
     * @return ResponseEntity<OrderDTO> – заказ, если найден, или 404 если не найден
     */
    @Operation(summary = "Получить заказ по ID")
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDTO> getOrderById(@PathVariable Long orderId) {
        Optional<OrderDTO> optionalOrder = orderService.getOrderById(orderId);
        return optionalOrder
                .map(ResponseEntity::ok) // Если заказ найден, возвращаем его
                .orElseGet(() -> ResponseEntity.notFound().build()); // Если не найден, возвращаем 404
    }

    /**
     * Обновить статус заказа
     */
    @Operation(summary = "Обновить статус заказа")
    @PutMapping("/{orderId}/status")
    public ResponseEntity<OrderDTO> updateOrderStatus(@PathVariable Long orderId, @RequestParam Status status) {
        OrderDTO updatedOrder = orderService.updateOrderStatus(orderId, status);
        return ResponseEntity.ok(updatedOrder);
    }

    /**
     * Удалить заказ
     */
    @Operation(summary = "Удалить заказ")
    @DeleteMapping("/{orderId}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long orderId) {
        orderService.deleteOrder(orderId);
        return ResponseEntity.noContent().build();
    }


    /**
     * Получить все элементы заказа по идентификатору заказа.
     */
    @Operation(summary = "Получить все элементы заказа по идентификатору заказа.")
    @GetMapping("/{orderId}/items")
    public ResponseEntity<List<OrderItemDTO>> getOrderItems(@PathVariable Long orderId) {
        List<OrderItemDTO> orderItems = orderItemService.getOrderItemsByOrderId(orderId);
        return ResponseEntity.ok(orderItems);
    }
}
