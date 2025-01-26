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
import ru.alexds.ccoshop.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Сервис для управления корзинами пользователей.
 * Обеспечивает API для получения, удаления, обновления и добавления товаров в корзины пользователей.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CartService {

    private final CartItemRepository cartItemRepository; // Репозиторий для работы с элементами корзины
    private final ProductService productService; // Сервис для работы с продуктами
    private final UserRepository userRepository; // Репозиторий для работы с пользователями (необходимо добавить)

    /**
     * Получает все товары в корзине для указанного пользователя.
     *
     * @param userId Идентификатор пользователя, чья корзина должна быть получена
     * @return Список товаров в формате DTO, которые находятся в корзине пользователя
     */
    public List<CartItemDTO> getCartItemsForUser(Long userId) {
        log.debug("Request to get all cart items for user ID: {}", userId);
        List<CartItem> cartItems = cartItemRepository.findByUserId(userId); // Получаем все элементы корзины для пользователя
        return cartItems.stream()
                .map(this::convertToDTO) // Преобразуем каждый элемент корзины в DTO
                .collect(Collectors.toList()); // Возвращаем список DTO
    }

    /**
     * Очищает корзину для указанного пользователя.
     *
     * @param userId Идентификатор пользователя, чья корзина должна быть очищена
     */
    public void clearCartForUser(Long userId) {
        log.debug("Request to clear cart for user ID: {}", userId);
        cartItemRepository.deleteByUserId(userId); // Удаляем все элементы корзины для пользователя
    }

    /**
     * Удаляет отдельный товар из корзины пользователя по его идентификатору.
     *
     * @param id Идентификатор элемента корзины, который необходимо удалить
     * @throws CartItemNotFoundException если элемент корзины не найден
     * @throws RuntimeException          если произошла ошибка при удалении элемента корзины
     */
    @Transactional
    public void removeCartItem(Long id) {
        log.debug("Request to remove cart item with ID: {}", id);
        CartItem cartItem = cartItemRepository.findById(id)
                .orElseThrow(() -> new CartItemNotFoundException("Cart item not found with id: " + id)); // Проверяем наличие элемента корзины

        try {
            cartItemRepository.deleteById(id); // Удаляем элемент корзины
            log.debug("Successfully removed cart item with ID: {}", id);
        } catch (Exception e) {
            log.error("Error removing cart item with ID: {}", id, e);
            throw new RuntimeException("Failed to remove cart item", e); // Перехватываем ошибки и выбрасываем RuntimeException
        }
    }

    /**
     * Обновляет количество товара в корзине пользователя.
     *
     * @param cartItemDTO DTO объект с информацией о товаре, который необходимо обновить (идентификатор, количество)
     * @return Обновленный элемент корзины в формате DTO
     * @throws CartItemNotFoundException  если элемент корзины не найден
     * @throws ProductNotFoundException   если продукт не найден
     * @throws InsufficientStockException если на складе недостаточно запасов продукта
     */
    @Transactional
    public CartItemDTO updateCartItem(CartItemDTO cartItemDTO) {
        log.debug("Request to update cart item: {}", cartItemDTO);

        // Проверяем существование товара в корзине
        CartItem existingItem = cartItemRepository.findById(cartItemDTO.getId())
                .orElseThrow(() -> new CartItemNotFoundException("Cart item not found"));

        // Проверяем наличие товара на складе
        Product product = productService.getProductEntityById(cartItemDTO.getProductId())
                .orElseThrow(() -> new ProductNotFoundException("Product not found"));

        if (product.getStockQuantity() < cartItemDTO.getQuantity()) {
            throw new InsufficientStockException("Insufficient stock for product: " + product.getName());
        }

        // Обновляем данные существующего товара
        existingItem.setQuantity(cartItemDTO.getQuantity());

        // Сохраняем обновленный товар
        CartItem savedItem = cartItemRepository.save(existingItem);
        log.debug("Successfully updated cart item with ID: {}", savedItem.getId());
        return convertToDTO(savedItem); // Преобразуем обновленный элемент корзины в DTO и возвращаем
    }

    /**
     * Добавляет новый товар в корзину пользователя или обновляет количество, если товар уже существует.
     *
     * @param cartItemDTO DTO объект с информацией о товаре, который необходимо добавить (идентификатор пользователя, идентификатор продукта, количество)
     * @return DTO объект добавленного или обновленного элемента корзины
     * @throws RuntimeException если продукт не найден
     */
    @Transactional
    public CartItemDTO addCartItem(CartItemDTO cartItemDTO) {
        log.debug("Request to add cart item: {}", cartItemDTO);

        // Проверка существования продукта
        Product product = productService.getProductEntityById(cartItemDTO.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found: " + cartItemDTO.getProductId()));

        // Проверяем существование элемента в корзине
        Optional<CartItem> existingCartItem = cartItemRepository.findByUserIdAndProductId(
                cartItemDTO.getUserId(),
                cartItemDTO.getProductId()
        );

        CartItem cartItem;
        if (existingCartItem.isPresent()) {
            // Если элемент уже существует, обновляем количество
            cartItem = existingCartItem.get();
            cartItem.setQuantity(cartItem.getQuantity() + cartItemDTO.getQuantity());
            log.debug("Updating existing cart item quantity for user ID {} and product ID {}: new quantity is {}",
                    cartItemDTO.getUserId(), cartItemDTO.getProductId(), cartItem.getQuantity());
        } else {
            // Создаем новый элемент корзины
            User user = userRepository.findById(cartItemDTO.getUserId())
                    .orElseThrow(() -> new RuntimeException("User not found: " + cartItemDTO.getUserId()));
            cartItem = CartItem.builder()
                    .user(user) // Привязываем пользователя к новому элементу корзины
                    .product(product) // Привязываем продукт к новому элементу корзины
                    .quantity(cartItemDTO.getQuantity()) // Устанавливаем количество товара
                    .price(product.getPrice()) // Устанавливаем цену продукта
                    .build();

            log.debug("Creating new cart item for user ID {} and product ID {}: quantity is {}",
                    cartItemDTO.getUserId(), cartItemDTO.getProductId(), cartItemDTO.getQuantity());
        }

        // Сохраняем или обновляем элемент корзины
        CartItem savedCartItem = cartItemRepository.save(cartItem);
        log.debug("Saved cart item with ID: {}", savedCartItem.getId());

        // Конвертируем в DTO и возвращаем
        return convertToDTO(savedCartItem);
    }

    /**
     * Вспомогательный метод для преобразования элемента корзины в DTO.
     *
     * @param cartItem Элемент корзины, который необходимо преобразовать
     * @return DTO объект элемента корзины
     */
    private CartItemDTO convertToDTO(CartItem cartItem) {
        log.debug("Converting cart item with ID {} to DTO", cartItem.getId());
        return new CartItemDTO(
                cartItem.getId(),
                cartItem.getUser().getId(),
                cartItem.getProduct().getId(),
                cartItem.getQuantity(),
                cartItem.getPrice()
        );
    }
}