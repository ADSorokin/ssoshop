package ru.alexds.ccoshop.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.alexds.ccoshop.dto.OrderDTO;
import ru.alexds.ccoshop.dto.OrderItemDTO;
import ru.alexds.ccoshop.entity.Status;
import ru.alexds.ccoshop.service.OrderItemService;
import ru.alexds.ccoshop.service.OrderService;

import java.util.List;
import java.util.Optional;

/**
 * Контроллер для управления заказами пользователей.
 * Обеспечивает API для создания, получения, обновления и удаления заказов,
 * а также для получения элементов заказов.
 */
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Validated
@Slf4j
@Tag(name = "Order Controller", description = "API для работы с заказами")
public class OrderController {
    private final OrderService orderService; // Сервис для управления заказами
    private final OrderItemService orderItemService; // Сервис для управления элементами заказов

    /**
     * Создает новый заказ на основе корзины пользователя.
     *
     * @param userId Идентификатор пользователя, чья корзина используется для создания заказа
     * @return HTTP-ответ с созданным заказом в формате DTO и статусом 200 (OK)
     * @throws Exception если произошла ошибка при создании заказа
     */
    @Operation(summary = "Создать заказ на основе корзины пользователя")
    @PostMapping("/{userId}/create")
    public ResponseEntity<OrderDTO> createOrderFromCart(@PathVariable Long userId){
        try{
        log.debug("Запрос на создание заказа из корзины для пользователя ID: {}", userId);
        OrderDTO order = orderService.createOrderFromCart(userId);
        return ResponseEntity.ok(order);} catch (Exception e) {

            throw new EntityNotFoundException("Ошибка при создании заказа для пользователя с ID: " + userId, e);
        }
    }

    /**
     * Получает все заказы для указанного пользователя.
     *
     * @param userId Идентификатор пользователя, чьи заказы необходимо получить
     * @return HTTP-ответ со списком всех заказов пользователя в формате DTO
     * Если заказы отсутствуют, возвращается статус 204 (No Content)
     */
    @Operation(summary = "Получить все заказы пользователя")
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<OrderDTO>> getOrdersByUserId(@PathVariable Long userId) {
        log.debug("Запрос на получение заказов пользователя ID: {}", userId);
        List<OrderDTO> orders = orderService.getOrdersByUserId(userId);
        if (orders.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build(); // Возвращаем 204, если заказы отсутствуют
        }
        return ResponseEntity.ok(orders); // Возвращаем список заказов с кодом 200
    }

    /**
     * Получает заказ по его идентификатору.
     *
     * @param orderId Идентификатор заказа, который необходимо получить
     * @return HTTP-ответ с заказом в формате DTO и статусом 200 (OK), если заказ найден,
     * или статусом 404 (Not Found), если заказ не найден
     */
    @Operation(summary = "Получить заказ по ID")
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDTO> getOrderById(@PathVariable Long orderId) {
        log.debug("Запрос на получение заказа ID: {}", orderId);
        Optional<OrderDTO> optionalOrder = orderService.getOrderById(orderId);
        return optionalOrder
                .map(ResponseEntity::ok) // Если заказ найден, возвращаем его
                .orElseGet(() -> ResponseEntity.notFound().build()); // Если не найден, возвращаем 404
    }

    /**
     * Обновляет статус заказа по его идентификатору.
     *
     * @param orderId Идентификатор заказа, который необходимо обновить
     * @param status  Новый статус заказа
     * @return HTTP-ответ с обновленным заказом в формате DTO и статусом 200 (OK)
     * @throws EntityNotFoundException если заказ с указанным идентификатором не найден
     */
    @Operation(summary = "Обновить статус заказа")
    @PutMapping("/{orderId}/status")
    public ResponseEntity<OrderDTO> updateOrderStatus(@PathVariable Long orderId, @RequestParam Status status) {
        try{
        log.debug("Request to update order status with ID: {} and new status: {}", orderId, status);
        OrderDTO updatedOrder = orderService.updateOrderStatus(orderId, status);
        return ResponseEntity.ok(updatedOrder);}
        catch (Exception e) {

            throw new EntityNotFoundException("Ошибка при создании заказа для заказа с ID: " + orderId, e);
        }
    }

    /**
     * Отменяет заказ.
     *
     * @param orderId Идентификатор заказа, который необходимо отменить
     * @return ResponseEntity с DTO объекта отмененного заказа
     * @throws RuntimeException если заказ не найден или уже завершен
     */
    @Operation(summary = "Отменить заказ")
    @PutMapping("/{orderId}/cancel")
    public ResponseEntity<OrderDTO> cancelOrder(@PathVariable Long orderId) {
        log.debug("REST запрос на отмену заказа с помощью ID: {}", orderId);

        try {
            // Вызов сервиса для отмены заказа
            OrderDTO cancelledOrder = orderService.cancelOrder(orderId);
            return ResponseEntity.ok(cancelledOrder);
        } catch (RuntimeException ex) {
            // Если заказ не найден или отмена невозможна
            log.error("Не удалось отменить заказ ID {}: {}", orderId, ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }


    /**
     * Удаляет заказ по его идентификатору.
     *
     * @param orderId Идентификатор заказа, который необходимо удалить
     * @return HTTP-ответ без содержимого и статусом 204 (No Content), подтверждающий успешное удаление
     * @throws EntityNotFoundException если заказ с указанным идентификатором не найден
     */
    @Operation(summary = "Удалить заказ")
    @DeleteMapping("/{orderId}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long orderId) {
        log.debug("Запрос на удаление заказа с ID: {}", orderId);
        orderService.deleteOrder(orderId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Получает все элементы заказа по идентификатору заказа.
     *
     * @param orderId Идентификатор заказа, элементы которого необходимо получить
     * @return HTTP-ответ со списком всех элементов заказа в формате DTO и статусом 200 (OK)
     * @throws EntityNotFoundException если заказ с указанным идентификатором не найден
     */
    @Operation(summary = "Получить все элементы заказа по идентификатору заказа")
    @GetMapping("/{orderId}/items")
    public ResponseEntity<List<OrderItemDTO>> getOrderItems(@PathVariable Long orderId) {
        log.debug("Запрос на получение товаров для заказа ID: {}", orderId);
        List<OrderItemDTO> orderItems = orderItemService.getOrderItemsByOrderId(orderId);
        return ResponseEntity.ok(orderItems);
    }
}