package ru.alexds.ccoshop.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.alexds.ccoshop.dto.CartItemDTO;
import ru.alexds.ccoshop.entity.CartItem;
import ru.alexds.ccoshop.exeption.CartItemNotFoundException;
import ru.alexds.ccoshop.service.CartService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    /**
     * Получение всех товаров в корзине
     */
//    @GetMapping("/{userId}")
//    public ResponseEntity<List<CartItemDTO>> getCartItems(@PathVariable Long userId) {
////        List<CartItem> cartItems = cartService.getCartItemsByUserId(userId);
////        System.out.println(cartItems);
////        return ResponseEntity.ok(cartItems);
//
//        // Преобразуем CartItem в CartItemDTO
//        List<CartItemDTO> cartItems = cartService.getCartItemsByUserId(userId);
//        List<CartItemDTO> cartItemDTOs = cartItems.stream()
//                .map(CartItemDTO::new)
//                .collect(Collectors.toList());
//
//        return ResponseEntity.ok(cartItemDTOs);
//    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<CartItemDTO>> getCartItems(@PathVariable Long userId) {
        // Получаем список CartItem из сервиса
        List<CartItem> cartItems = cartService.getCartItemsByUserId(userId);

        // Преобразуем List<CartItem> в List<CartItemDTO>
        List<CartItemDTO> cartItemDTOs = cartItems.stream()
                .map(CartItemDTO::new) // Используем конструктор CartItemDTO
                .collect(Collectors.toList());

        return ResponseEntity.ok(cartItemDTOs);
    }


    /**
     * Добавление товара в корзину
     */
    @PostMapping
    public ResponseEntity<CartItem> addCartItem(@RequestBody CartItem cartItem) {
        CartItem createdCartItem = cartService.addCartItem(cartItem);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCartItem);
    }

    /**
     * Обновление товара в корзине
     */
    @PutMapping("/{id}")
    public ResponseEntity<CartItem> updateCartItem(@PathVariable Long id, @RequestBody CartItem cartItem) {
        cartItem.setId(id); // Устанавливаем ID для обновления
        CartItem updatedCartItem = cartService.updateCartItem(cartItem);
        return ResponseEntity.ok(updatedCartItem);
    }

    /**
     * Удаление товара из корзины
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeCartItem(@PathVariable Long id) {
        cartService.removeCartItem(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Очистка всех товаров в корзине
     */
    @DeleteMapping("/clear/{userId}")
    public ResponseEntity<Void> clearCart(@PathVariable Long userId) {
        cartService.clearCart(userId);
        return ResponseEntity.noContent().build();
    }


    /**
     * Обработчик исключений
     */
    @ExceptionHandler(CartItemNotFoundException.class)
    public ResponseEntity<String> handleCartItemNotFound(CartItemNotFoundException e) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneralException(Exception e) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("An error occurred while processing your request");
    }
}
