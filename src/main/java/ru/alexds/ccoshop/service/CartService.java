package ru.alexds.ccoshop.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.alexds.ccoshop.dto.CartItemDTO;
import ru.alexds.ccoshop.entity.CartItem;
import ru.alexds.ccoshop.entity.Order;
import ru.alexds.ccoshop.entity.Product;
import ru.alexds.ccoshop.entity.User;
import ru.alexds.ccoshop.exeption.CartItemNotFoundException;
import ru.alexds.ccoshop.exeption.InsufficientStockException;
import ru.alexds.ccoshop.exeption.ProductNotFoundException;
import ru.alexds.ccoshop.repository.CartItemRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Slf4j
public class CartService {


    private final CartItemRepository cartItemRepository;
    private final ProductService productService;

    /**
     * Получить все товары в корзине для пользователя
     */

    public List<CartItemDTO> getCartItemsForUser(Long userId) {
        List<CartItem> cartItems = cartItemRepository.findByUserId(userId);
        return cartItems.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    // Удалить все товары из корзины пользователя (после оформления заказа)
    public void clearCartForUser(Long userId) {
        cartItemRepository.deleteByUserId(userId);
    }



    /**
     * //     * Удаление отдельного товара из корзины
     * //
     */
    @Transactional
    public void removeCartItem(Long id) {
        log.debug("Removing cart item with ID: {}", id);

        CartItem cartItem = cartItemRepository.findById(id).orElseThrow(() -> new CartItemNotFoundException("Cart item not found with id: " + id));

        try {
            cartItemRepository.deleteById(id);
            log.debug("Successfully removed cart item with ID: {}", id);
        } catch (Exception e) {
            log.error("Error removing cart item with ID: {}", id, e);
            throw new RuntimeException("Failed to remove cart item", e);
        }
    }

    /**
     * //     * Обновление товара в корзине
     * //
     */
    @Transactional
    public CartItemDTO updateCartItem(CartItemDTO cartItemDTO) {
        log.debug("Updating cart item: {}", cartItemDTO);

        // Проверяем существование товара в корзине
        CartItem existingItem = cartItemRepository.findById(cartItemDTO.getId()).orElseThrow(() -> new CartItemNotFoundException("Cart item not found"));

        // Проверяем наличие товара на складе
        Product product = productService.getProductEntityById(cartItemDTO.getProductId()).orElseThrow(() -> new ProductNotFoundException("Product not found"));

        if (product.getStockQuantity() < cartItemDTO.getQuantity()) {
            throw new InsufficientStockException("Insufficient stock for product: " + product.getName());
        }

        // Обновляем данные существующего товара
        existingItem.setQuantity(cartItemDTO.getQuantity());


        // Сохраняем обновленный товар
        CartItem savedItem = cartItemRepository.save(existingItem);
        return new CartItemDTO(savedItem);
    }

    @Transactional
    public CartItemDTO addCartItem(CartItemDTO cartItemDTO) {
        // Проверка существования продукта
        Product product = productService.getProductEntityById(cartItemDTO.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found: " + cartItemDTO.getProductId()));

        // Проверяем существование элемента в корзине
        Optional<CartItem> existingCartItem = cartItemRepository.findByUserIdAndProductId(
                cartItemDTO.getUserId(),
                cartItemDTO.getProductId()
        );
        // TODO: 19.01.2025 добавление рейтингов написать функцию
        ///saveRatingsForOrder(Order order)
        CartItem cartItem;

        if (existingCartItem.isPresent()) {
            // Если элемент уже существует, обновляем количество
            cartItem = existingCartItem.get();
            cartItem.setQuantity(cartItem.getQuantity() + cartItemDTO.getQuantity());
        } else {
            // Создаем новый элемент корзины
            cartItem = CartItem.builder()
                    .user(new User(cartItemDTO.getUserId())) // Можно также искомый метод в UserService
                    .product(product)
                    .quantity(cartItemDTO.getQuantity())
                    .price(product.getPrice()) // Здесь лучше использовать цену продукта
                    .build();
        }

        // Сохраняем или обновляем элемент корзины
        CartItem savedCartItem = cartItemRepository.save(cartItem);

        // Конвертируем в DTO и возвращаем
        return convertToDTO(savedCartItem);
    }

    // Вспомогательный метод для преобразования CartItem в CartItemDTO
    private CartItemDTO convertToDTO(CartItem cartItem) {
        return new CartItemDTO(
                cartItem.getId(),
                cartItem.getUser().getId(),
                cartItem.getProduct().getId(),
                cartItem.getQuantity(),
                cartItem.getPrice()
        );
    }

}
