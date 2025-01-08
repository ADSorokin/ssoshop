package ru.alexds.ccoshop.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.alexds.ccoshop.dto.CartItemDTO;
import ru.alexds.ccoshop.entity.CartItem;
import ru.alexds.ccoshop.entity.Product;
import ru.alexds.ccoshop.entity.User;
import ru.alexds.ccoshop.exeption.CartItemNotFoundException;
import ru.alexds.ccoshop.exeption.InsufficientStockException;
import ru.alexds.ccoshop.exeption.ProductNotFoundException;
import ru.alexds.ccoshop.repository.CartItemRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;



@Service
@RequiredArgsConstructor
@Slf4j
public class CartService {

    private final CartItemRepository cartItemRepository;
    private final ProductService productService;
    private final UserService userService;

    /**
     * Обновление товара в корзине
     */
    @Transactional
    public CartItemDTO updateCartItem(CartItemDTO cartItemDTO) {
        log.debug("Updating cart item: {}", cartItemDTO);

        // Проверяем существование товара в корзине
        CartItem existingItem = cartItemRepository.findById(cartItemDTO.getId())
                .orElseThrow(() -> new CartItemNotFoundException("Cart item not found"));

        // Проверяем наличие товара на складе
        Product product = productService.getProductById(cartItemDTO.getProductId())
                .orElseThrow(() -> new ProductNotFoundException("Product not found"));

        if (product.getStockQuantity() < cartItemDTO.getQuantity()) {
            throw new InsufficientStockException("Insufficient stock for product: " + product.getName());
        }

        // Обновляем данные существующего товара
        existingItem.setQuantity(cartItemDTO.getQuantity());
        existingItem.setTotalPrice(calculateTotalPrice(product.getPrice(), cartItemDTO.getQuantity()));

        // Сохраняем обновленный товар
        CartItem savedItem = cartItemRepository.save(existingItem);
        return new CartItemDTO(savedItem);
    }

    /**
     * Добавление товара в корзину
     */
    @Transactional
    public CartItemDTO addCartItem(CartItemDTO cartItemDTO) {
        log.debug("Adding cart item: {}", cartItemDTO);

        // Получаем пользователя
        User user = userService.getUserEntityById(cartItemDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Проверяем существование продукта
        Product product = productService.getProductById(cartItemDTO.getProductId())
                .orElseThrow(() -> new ProductNotFoundException("Product not found"));

        // Проверяем наличие достаточного количества товара на складе
        if (product.getStockQuantity() < cartItemDTO.getQuantity()) {
            throw new InsufficientStockException("Insufficient stock for product: " + product.getName());
        }

        // Проверяем, есть ли уже такой товар в корзине у пользователя
        Optional<CartItem> existingItem = cartItemRepository
                .findByUserIdAndProductId(cartItemDTO.getUserId(), product.getId());

        if (existingItem.isPresent()) {
            // Если товар уже есть в корзине, обновляем количество
            CartItem existing = existingItem.get();
            int newQuantity = existing.getQuantity() + cartItemDTO.getQuantity();

            // Проверяем достаточно ли товара на складе для общего количества
            if (product.getStockQuantity() < newQuantity) {
                throw new InsufficientStockException("Insufficient stock for total quantity requested");
            }

            existing.setQuantity(newQuantity);
            existing.setTotalPrice(calculateTotalPrice(product.getPrice(), newQuantity));
            return new CartItemDTO(cartItemRepository.save(existing));
        } else {
            // Создаем новый элемент корзины
            CartItem cartItem = new CartItem();
            cartItem.setUser(user);
            cartItem.setProduct(product);
            cartItem.setQuantity(cartItemDTO.getQuantity());
            cartItem.setTotalPrice(calculateTotalPrice(product.getPrice(), cartItemDTO.getQuantity()));
            return new CartItemDTO(cartItemRepository.save(cartItem));
        }
    }

    /**
     * Получение всех товаров в корзине пользователя
     */
    public List<CartItemDTO> getCartItemsByUserId(Long userId) {
        log.debug("Retrieving cart items for user ID: {}", userId);
        return cartItemRepository.findByUserId(userId).stream()
                .map(CartItemDTO::new)
                .collect(Collectors.toList());
    }

    public List<CartItem> getCartItemEntityByUserId(Long userId) {
        log.debug("Retrieving cart items for user ID: {}", userId);
        return cartItemRepository.findByUserId(userId);
    }

    /**
     * Удаление отдельного товара из корзины
     */
    @Transactional
    public void removeCartItem(Long id) {
        log.debug("Removing cart item with ID: {}", id);

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

    /**
     * Вспомогательный метод для расчета общей стоимости
     */
    private BigDecimal calculateTotalPrice(BigDecimal price, int quantity) {
        return price.multiply(BigDecimal.valueOf(quantity));
    }
}



