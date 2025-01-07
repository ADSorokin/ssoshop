package ru.alexds.ccoshop.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.alexds.ccoshop.dto.CartItemDTO;
import ru.alexds.ccoshop.entity.CartItem;
import ru.alexds.ccoshop.entity.Product;
import ru.alexds.ccoshop.exeption.CartItemNotFoundException;
import ru.alexds.ccoshop.exeption.InsufficientStockException;
import ru.alexds.ccoshop.exeption.ProductNotFoundException;
import ru.alexds.ccoshop.repository.CartItemRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartService {

    private final CartItemRepository cartItemRepository;
    private final ProductService productService;

    /**
     * Обновление товара в корзине
     */
    @Transactional
    public CartItem updateCartItem(CartItem cartItem) {
        log.debug("Updating cart item: {}", cartItem);

        // Проверяем существование товара в корзине
        CartItem existingItem = cartItemRepository.findById(cartItem.getId())
                .orElseThrow(() -> new CartItemNotFoundException("Cart item not found"));

        // Проверяем наличие товара на складе
        Product product = productService.getProductById(cartItem.getProduct().getId())
                .orElseThrow(() -> new ProductNotFoundException("Product not found"));

        if (product.getStockQuantity() < cartItem.getQuantity()) {
            throw new InsufficientStockException("Insufficient stock for product: " + product.getName());
        }

        // Обновляем данные существующего товара
        existingItem.setQuantity(cartItem.getQuantity());
        existingItem.setTotalPrice(calculateTotalPrice(product.getPrice(), cartItem.getQuantity()));

        // Сохраняем обновленный товар
        return cartItemRepository.save(existingItem);
    }

    /**
     * Добавление товара в корзину
     */
    @Transactional
    public CartItem addCartItem(CartItem cartItem) {
        log.debug("Adding cart item: {}", cartItem);

        // Проверяем существование продукта
        Product product = productService.getProductById(cartItem.getProduct().getId())
                .orElseThrow(() -> new ProductNotFoundException("Product not found"));

        // Проверяем наличие достаточного количества товара на складе
        if (product.getStockQuantity() < cartItem.getQuantity()) {
            throw new InsufficientStockException("Insufficient stock for product: " + product.getName());
        }

        // Проверяем, есть ли уже такой товар в корзине у пользователя
        Optional<CartItem> existingItem = cartItemRepository
                .findByUserIdAndProductId(cartItem.getUser().getId(), product.getId());

        if (existingItem.isPresent()) {
            // Если товар уже есть в корзине, обновляем количество
            CartItem existing = existingItem.get();
            int newQuantity = existing.getQuantity() + cartItem.getQuantity();

            // Проверяем достаточно ли товара на складе для общего количества
            if (product.getStockQuantity() < newQuantity) {
                throw new InsufficientStockException("Insufficient stock for total quantity requested");
            }

            existing.setQuantity(newQuantity);
            existing.setTotalPrice(calculateTotalPrice(product.getPrice(), newQuantity));
            return cartItemRepository.save(existing);
        } else {
            // Создаем новый элемент корзины
            cartItem.setTotalPrice(calculateTotalPrice(product.getPrice(), cartItem.getQuantity()));
            return cartItemRepository.save(cartItem);
        }
    }

    /**
     * Вспомогательный метод для расчета общей стоимости
     */
    private BigDecimal calculateTotalPrice(BigDecimal price, int quantity) {
        return price.multiply(BigDecimal.valueOf(quantity));
    }

    /**
     * Получение всех товаров в корзине пользователя
     */
    public List<CartItem> getCartItemsByUserId(Long userId) {
        log.debug("Retrieving cart items for user ID: {}", userId);

        // Получаем все предметы корзины по идентификатору пользователя
        List<CartItem> cartItems = cartItemRepository.findByUserId(userId);

        // Дополнительная обработка (например, можно получить связанные данные о продукте, если это нужно)
        // cartItems.forEach(item -> {
        //     // Пример: Запрос информации о продукте или что-то еще
        // });

        return cartItems;
    }

    /**
     * Удаление отдельного товара из корзины
     */
    @Transactional
    public void removeCartItem(Long id) {
        log.debug("Removing cart item with ID: {}", id);

        // Проверяем существование товара в корзине
        CartItem cartItem = cartItemRepository.findById(id)
                .orElseThrow(() -> new CartItemNotFoundException("Cart item not found with id: " + id));

        try {
            cartItemRepository.deleteById(id);
            log.debug("Successfully removed cart item with ID: {}", id);
        } catch (Exception e) {
            log.error("Error removing cart item with ID: {}", id, e);
            throw new RuntimeException("Failed to remove cart item", e);
        }
    }

    /**
     * Очистка всей корзины пользователя
     */
    @Transactional
    public void clearCart(Long userId) {
        log.debug("Clearing cart for user ID: {}", userId);

        try {
            // Проверяем существование товаров в корзине пользователя
            List<CartItem> userCart = cartItemRepository.findByUserId(userId);

            if (userCart.isEmpty()) {
                log.debug("Cart is already empty for user ID: {}", userId);
                return;
            }

            cartItemRepository.deleteByUserId(userId);
            log.debug("Successfully cleared cart for user ID: {}", userId);
        } catch (Exception e) {
            log.error("Error clearing cart for user ID: {}", userId, e);
            throw new RuntimeException("Failed to clear cart", e);
        }
    }
}



