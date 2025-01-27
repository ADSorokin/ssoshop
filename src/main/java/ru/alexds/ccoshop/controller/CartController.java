package ru.alexds.ccoshop.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import ru.alexds.ccoshop.dto.CartItemDTO;
import ru.alexds.ccoshop.dto.OrderDTO;
import ru.alexds.ccoshop.entity.ErrorResponse;
import ru.alexds.ccoshop.exeption.CartItemNotFoundException;
import ru.alexds.ccoshop.exeption.InsufficientStockException;
import ru.alexds.ccoshop.service.CartService;
import ru.alexds.ccoshop.service.OrderService;

import java.util.List;
import java.util.stream.Collectors;



/**
 * Контроллер для управления корзиной пользователя.
 * Обеспечивает API для создания заказа из содержимого корзины,
 * получения, добавления, обновления и удаления товаров в корзине.
 */
@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Cart Controller", description = "API для работы корзиной")
public class CartController {
    private final CartService cartService; // Сервис для управления корзинами пользователей
    private final OrderService orderService; // Сервис для управления заказами

    /**
     * Создает заказ на основе содержимого корзины указанного пользователя.
     *
     * @param userId Идентификатор пользователя, чья корзина используется для создания заказа
     * @return HTTP-ответ с созданным заказом в формате DTO
     */
    @Operation(summary = "Создать заказ из содержимого корзины")
    @PostMapping("/{userId}/create")
    public ResponseEntity<OrderDTO> createOrderFromCart(@PathVariable Long userId) {
        log.debug("Request to create order from cart for user ID: {}", userId);
        OrderDTO order = orderService.createOrderFromCart(userId);
        return ResponseEntity.ok(order);
    }

    /**
     * Получает содержимое корзины для указанного пользователя.
     *
     * @param userId Идентификатор пользователя, чья корзина запрашивается
     * @return HTTP-ответ со списком товаров в корзине в формате DTO
     */
    @Operation(summary = "Получение содержимого корзины пользователя")
    @GetMapping("/{userId}")
    public ResponseEntity<List<CartItemDTO>> getCartItems(@PathVariable Long userId) {
        log.debug("Request to get cart items for user ID: {}", userId);
        List<CartItemDTO> cartItems = cartService.getCartItemsForUser(userId);
        return ResponseEntity.ok(cartItems);
    }

    /**
     * Добавляет товар в корзину пользователя.
     *
     * @param cartItemDTO DTO объект с информацией о добавляемом товаре (идентификатор товара, количество)
     * @return HTTP-ответ с добавленным товаром в корзине в формате DTO
     */
    @Operation(summary = "Добавить товар в корзину")
    @PostMapping("/add")
    public ResponseEntity<CartItemDTO> addCartItem(@RequestBody @Valid CartItemDTO cartItemDTO) {
        log.debug("Request to add cart item: {}", cartItemDTO);
        CartItemDTO addedCartItem = cartService.addCartItem(cartItemDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(addedCartItem);
    }

    /**
     * Обновляет количество товара в корзине.
     *
     * @param cartItemId  Идентификатор товара в корзине, который нужно обновить
     * @param cartItemDTO DTO объект с новыми данными о количестве товара
     * @return HTTP-ответ с обновленным товаром в корзине в формате DTO
     * @throws IllegalArgumentException если переданный идентификатор товара в теле запроса не совпадает с идентификатором в URL
     * @throws InsufficientStockException если на складе недостаточно товара для выполнения обновления
     */
    @Operation(summary = "Обновление количества товара в корзине")
    @PutMapping("/{cartItemId}")
    public ResponseEntity<CartItemDTO> updateCartItem(@PathVariable Long cartItemId, @Valid @RequestBody CartItemDTO cartItemDTO) {
        log.debug("Request to update cart item ID: {} with data: {}", cartItemId, cartItemDTO);
        cartItemDTO.setId(cartItemId);
        CartItemDTO updatedItem = cartService.updateCartItem(cartItemDTO);
        return ResponseEntity.ok(updatedItem);
    }

    /**
     * Удаляет товар из корзины по его идентификатору.
     *
     * @param cartItemId Идентификатор товара в корзине, который нужно удалить
     * @return HTTP-ответ без содержимого, подтверждающий успешное удаление
     * @throws CartItemNotFoundException если товар с указанным идентификатором не найден в корзине
     */
    @Operation(summary = "Удаление товара из корзины")
    @DeleteMapping("/{cartItemId}")
    public ResponseEntity<Void> removeFromCart(@PathVariable Long cartItemId) {
        log.debug("Request to remove cart item ID: {}", cartItemId);
        cartService.removeCartItem(cartItemId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Очищает всю корзину для указанного пользователя.
     *
     * @param userId Идентификатор пользователя, чью корзину нужно очистить
     * @return HTTP-ответ без содержимого, подтверждающий успешную очистку корзины
     */
    @Operation(summary = "Очистка корзины пользователя")
    @DeleteMapping("/user/{userId}")
    public ResponseEntity<Void> clearCart(@PathVariable Long userId) {
        log.debug("Request to clear cart for user ID: {}", userId);
        cartService.clearCartForUser(userId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Обрабатывает исключение при недостаточном количестве товара на складе.
     *
     * @param ex Исключение, которое возникает при попытке добавить или обновить товар в корзине,
     *           если на складе недостаточно товара
     * @return HTTP-ответ с сообщением об ошибке
     */
    @ExceptionHandler(InsufficientStockException.class)
    public ResponseEntity<ErrorResponse> handleInsufficientStock(InsufficientStockException ex) {
        log.error("Insufficient stock error: {}", ex.getMessage());
        ErrorResponse error = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "INSUFFICIENT_STOCK", ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    /**
     * Обрабатывает исключение при отсутствии товара в корзине.
     *
     * @param ex Исключение, которое возникает при попытке удалить или обновить товар в корзине,
     *           если товар с указанным идентификатором не найден
     * @return HTTP-ответ с сообщением об ошибке
     */
    @ExceptionHandler(CartItemNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleCartItemNotFound(CartItemNotFoundException ex) {
        log.error("Cart item not found: {}", ex.getMessage());
        ErrorResponse error = new ErrorResponse(HttpStatus.NOT_FOUND.value(), "CART_ITEM_NOT_FOUND", ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    /**
     * Обрабатывает исключения, связанные с ошибками валидации входных данных.
     *
     * @param ex Исключение, которое возникает при невалидных данных в запросе
     * @return HTTP-ответ с сообщением об ошибке и деталями валидации
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        log.error("Validation error: {}", ex.getMessage());
        List<String> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.toList());
        ErrorResponse error = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "VALIDATION_ERROR", "Validation failed: " + String.join(", ", errors));
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
}