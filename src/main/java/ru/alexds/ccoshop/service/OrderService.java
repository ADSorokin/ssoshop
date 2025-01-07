package ru.alexds.ccoshop.service;


import ru.alexds.ccoshop.dto.CartItemDTO;
import ru.alexds.ccoshop.entity.*;

import java.math.RoundingMode;
import java.time.YearMonth;
import java.util.*;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.alexds.ccoshop.exeption.OrderNotFoundException;
import ru.alexds.ccoshop.repository.OrderRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserService userService;
    private final ProductService productService;
    private final CartService cartService;


    /**
     * Создание нового заказа на основе содержимого корзины пользователя
     */
    @Transactional
    public Order createOrderFromCart(Long userId) {
        User user = userService.getUserById(userId) // Получите пользователя по userId
                .orElseThrow(() -> new RuntimeException("User not found")); // выбросьте исключение, если пользователь не найден
        List<CartItem> cartItems = cartService.getCartItemsByUserId(userId);
        if (cartItems.isEmpty()) {
            throw new RuntimeException("Cart is empty. Cannot create an order.");
        }

        Order order = new Order();

        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());

        BigDecimal totalOrderPrice = BigDecimal.ZERO;

        // Рассчитать общую стоимость и добавить товары в заказ
        for (CartItem cartItem : cartItems) {
            totalOrderPrice = totalOrderPrice.add(cartItem.getTotalPrice());
            // Здесь можно добавить логику по добавлению товаров в заказ, если у вас есть такая модель
        }

        order.setTotalPrice(totalOrderPrice);

        // Сохранить заказ
        Order savedOrder = orderRepository.save(order);

        // Очистить корзину после создания заказа
        cartService.clearCart(userId);

        return savedOrder;
    }

    /**
     * Создание нового заказа
     */
    @Transactional
    public Order createOrder(Long userId, Long productId, int quantity) {
        User user = userService.getUserById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Product product = productService.getProductById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // Проверка наличия товара
        if (product.getStockQuantity() < quantity) {
            throw new RuntimeException("Insufficient stock");
        }

        // Создание заказа
        Order order = new Order();
        order.setUser(user);
        order.setProduct(product);
        order.setQuantity(quantity);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(Status.NEW);

        // Расчет общей стоимости
        BigDecimal totalPrice = product.getPrice().multiply(BigDecimal.valueOf(quantity));
        order.setTotalPrice(totalPrice);

        // Обновление остатка товара
        product.setStockQuantity(product.getStockQuantity() - quantity);
        productService.updateProduct(product);

        return orderRepository.save(order);
    }

    /**
     * Получение всех заказов пользователя
     */
    public List<Order> getUserOrders(Long userId) {
        return orderRepository.findByUserId(userId);
    }

    /**
     * Получение заказа по ID
     */
    /**
     /* Получение заказа по ID



    /**
     * Обновление статуса заказа
     */
    @Transactional
    public Order updateOrderStatus(Long orderId, Status status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        order.setStatus(status);
        return orderRepository.save(order);
    }

    /**
     * Отмена заказа
     */
    @Transactional
    public Order cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (Status.COMPLETED.equals(order.getStatus())) {
            throw new RuntimeException("Cannot cancel completed order");
        }

        // Возврат товара на склад
        Product product = order.getProduct();
        product.setStockQuantity(product.getStockQuantity() + order.getQuantity());
        productService.updateProduct(product);

        order.setStatus(Status.CANCELLED);
        return orderRepository.save(order);
    }

    /**
     * Получение списка купленных продуктов пользователем
     */
    public List<Product> getPurchasedProducts(Long userId) {
        List<Order> completedOrders = orderRepository.findCompletedOrdersByUserId(userId); // Получаем список завершенных заказов

        // Преобразуем список заказов в поток, извлекаем продукты и собираем уникальные значения в список
        List<Product> purchasedProducts = completedOrders.stream()
                .map(order -> order.getProduct()) // Вызываем метод getProduct() для каждого заказа
                .filter(Objects::nonNull) // Исключаем null значения
                .distinct() // Убираем дубликаты
                .collect(Collectors.toList()); // Собираем результаты в список

        return purchasedProducts; // Возвращаем итоговый список купленных продуктов
    }
    /**
     * Получение статистики заказов по продукту
     */
    public long getProductOrderCount(Long productId) {
        return orderRepository.countByProductIdAndStatus(productId, Status.COMPLETED);
    }

    /**
     * Получение пагинированного списка всех заказов
     */
    public Page<Order> getAllOrders(Pageable pageable) {
        return orderRepository.findAll(pageable);
    }

    /**
     * Получение заказов за определенный период
     */
    public List<Order> getOrdersByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return orderRepository.findByOrderDateBetween(startDate, endDate);
    }


    /**
     * Проверка, покупал ли пользователь определенный продукт
     */
    public boolean hasUserPurchasedProduct(Long userId, Long productId) {
        return orderRepository.existsByUserIdAndProductIdAndStatus(userId, productId, Status.COMPLETED);
    }


    /**
     * Удалить заказ по ID
     */


    @Transactional
    public void deleteOrder(Long orderId) {
        if (!orderRepository.existsById(orderId)) {
            throw new OrderNotFoundException("Order not found with ID: " + orderId);
        }
        orderRepository.deleteById(orderId);
    }


    /**
     * Расчет общей суммы заказов пользователя
     */
    public BigDecimal calculateUserTotalSpent(Long userId) {
        return orderRepository.findByUserIdAndStatus(userId, Status.COMPLETED).stream()
                .map(Order::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Расчет средней суммы заказа пользователя
     */
    public BigDecimal calculateUserAverageOrderAmount(Long userId) {
        List<Order> completedOrders = orderRepository.findByUserIdAndStatus(userId, Status.COMPLETED);
        if (completedOrders.isEmpty()) {
            return BigDecimal.ZERO;
        }

        BigDecimal totalSpent = calculateUserTotalSpent(userId);
        return totalSpent.divide(BigDecimal.valueOf(completedOrders.size()), 2, RoundingMode.HALF_UP);
    }

    /**
     * Получение статистики заказов пользователя
     */
    public Map<String, Object> getUserOrderStatistics(Long userId) {
        Map<String, Object> statistics = new HashMap<>();

        // Получаем все завершенные заказы пользователя
        List<Order> completedOrders = orderRepository.findByUserIdAndStatus(userId, Status.COMPLETED);

        // Общая сумма заказов
        BigDecimal totalSpent = calculateUserTotalSpent(userId);

        // Средняя сумма заказа
        BigDecimal averageOrderAmount = calculateUserAverageOrderAmount(userId);

        // Количество заказов
        long totalOrders = orderRepository.countByUserIdAndStatus(userId, Status.COMPLETED);

        // Максимальная сумма заказа
        Optional<BigDecimal> maxOrderAmount = completedOrders.stream()
                .map(Order::getTotalPrice)
                .max(BigDecimal::compareTo);

        // Минимальная сумма заказа
        Optional<BigDecimal> minOrderAmount = completedOrders.stream()
                .map(Order::getTotalPrice)
                .min(BigDecimal::compareTo);

        statistics.put("totalSpent", totalSpent);
        statistics.put("averageOrderAmount", averageOrderAmount);
        statistics.put("totalOrders", totalOrders);
        statistics.put("maxOrderAmount", maxOrderAmount.orElse(BigDecimal.ZERO));
        statistics.put("minOrderAmount", minOrderAmount.orElse(BigDecimal.ZERO));

        return statistics;
    }

    /**
     * Получение списка заказов по сумме больше указанной
     */
    public List<Order> getOrdersAboveAmount(Long userId, BigDecimal amount) {
        return orderRepository.findByUserIdAndStatus(userId, Status.COMPLETED).stream()
                .filter(order -> order.getTotalPrice().compareTo(amount) > 0)
                .collect(Collectors.toList());
    }

    /**
     * Расчет общей суммы заказов по месяцам
     */
    public Map<YearMonth, BigDecimal> calculateMonthlySpending(Long userId) {
        return orderRepository.findByUserIdAndStatus(userId, Status.COMPLETED).stream()
                .collect(Collectors.groupingBy(
                        order -> YearMonth.from(order.getOrderDate()),
                        Collectors.mapping(
                                Order::getTotalPrice,
                                Collectors.reducing(BigDecimal.ZERO, BigDecimal::add)
                        )
                ));
    }

    public List<Order> getOrdersByUserId(Long userId) {
        return orderRepository.findByUserId(userId);
    }


    /**
     * Получить заказ по ID
     *
     * @param orderId идентификатор заказа
     * @return заказ (Optional) с заданным ID
     */

    /**
     * Получить заказ по ID
     *
     * @param orderId Идентификатор заказа
     * @return Optional<Order> – заказ с указанным ID, если найден; иначе пустой Optional
     */
    public Optional<Order> getOrderById(Long orderId) {
        return orderRepository.findById(orderId);
    }
}