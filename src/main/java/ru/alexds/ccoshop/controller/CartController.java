package ru.alexds.ccoshop.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import ru.alexds.ccoshop.dto.CartItemDTO;
import ru.alexds.ccoshop.dto.OrderDTO;
import ru.alexds.ccoshop.exeption.CartItemNotFoundException;
import ru.alexds.ccoshop.exeption.InsufficientStockException;
import ru.alexds.ccoshop.service.CartService;
import ru.alexds.ccoshop.service.OrderService;

import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
@Slf4j
public class CartController {

    private final CartService cartService;
    private final OrderService orderService;
    /**
     * Получение содержимого корзины пользователя
     */
    @PostMapping("/{userId}/create")
    public ResponseEntity<OrderDTO> createOrderFromCart(@PathVariable Long userId) {

        OrderDTO order = orderService.createOrderFromCart(userId);
        return ResponseEntity.ok(order);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<CartItemDTO>> getCartItems(@PathVariable Long userId) {

        log.debug("Request to get cart items for user ID: {}", userId);
        List<CartItemDTO> cartItems = cartService.getCartItemsForUser(userId);
        return ResponseEntity.ok(cartItems);
    }

    /**
     * Добавление товара в корзину
     */
    /**
     * Добавить товар в корзину
     */
    @PostMapping("/add")
    public ResponseEntity<CartItemDTO> addCartItem(@RequestBody @Valid CartItemDTO cartItemDTO) {
        CartItemDTO addedCartItem = cartService.addCartItem(cartItemDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(addedCartItem);
    }

//    @PostMapping
//    public ResponseEntity<CartItemDTO> addToCart(@Valid @RequestBody CartItemDTO cartItemDTO) {
//        log.debug("Request to add item to cart: {}", cartItemDTO);
//        CartItemDTO addedItemDTO = cartService.addCartItem(cartItemDTO);
//        return new ResponseEntity<>(addedItemDTO, HttpStatus.CREATED);
//    }

    /**
     * Обновление количества товара в корзине
     */
    @PutMapping("/{cartItemId}")
    public ResponseEntity<CartItemDTO> updateCartItem(@PathVariable Long cartItemId, @Valid @RequestBody CartItemDTO cartItemDTO) {
        log.debug("Request to update cart item ID: {} with data: {}", cartItemId, cartItemDTO);

        if (!cartItemId.equals(cartItemDTO.getId())) {
            return ResponseEntity.badRequest().build();
        }

        CartItemDTO updatedItem = cartService.updateCartItem(cartItemDTO);
        return ResponseEntity.ok(updatedItem);
    }

    /**
     * Удаление товара из корзины
     */
    @DeleteMapping("/{cartItemId}")
    public ResponseEntity<Void> removeFromCart(@PathVariable Long cartItemId) {
        log.debug("Request to remove cart item ID: {}", cartItemId);
        cartService.removeCartItem(cartItemId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Очистка корзины пользователя
     */
    @DeleteMapping("/user/{userId}")
    public ResponseEntity<Void> clearCart(@PathVariable Long userId) {
        log.debug("Request to clear cart for user ID: {}", userId);
        cartService.clearCartForUser(userId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Обработка исключений при недостаточном количестве товара
     */
    @ExceptionHandler(InsufficientStockException.class)
    public ResponseEntity<ErrorResponse> handleInsufficientStock(InsufficientStockException ex) {
        log.error("Insufficient stock error: {}", ex.getMessage());
        ErrorResponse error = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "INSUFFICIENT_STOCK", ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    /**
     * Обработка исключений при отсутствии товара в корзине
     */
    @ExceptionHandler(CartItemNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleCartItemNotFound(CartItemNotFoundException ex) {
        log.error("Cart item not found: {}", ex.getMessage());
        ErrorResponse error = new ErrorResponse(HttpStatus.NOT_FOUND.value(), "CART_ITEM_NOT_FOUND", ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    /**
     * Обработка ошибок валидации
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        log.error("Validation error: {}", ex.getMessage());

        List<String> errors = ex.getBindingResult().getFieldErrors().stream().map(error -> error.getField() + ": " + error.getDefaultMessage()).collect(Collectors.toList());

        ErrorResponse error = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "VALIDATION_ERROR", "Validation failed: " + String.join(", ", errors));

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
}

