package ru.alexds.ccoshop.service;


import ru.alexds.ccoshop.dto.CartItemDTO;
import ru.alexds.ccoshop.dto.OrderDTO;
import ru.alexds.ccoshop.dto.UserDTO;
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
    public OrderDTO createOrderFromCart(Long userId) {
        User user = userService.getUserEntityById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<CartItem> cartItems = cartService.getCartItemEntityByUserId(userId);
        if (cartItems.isEmpty()) {
            throw new RuntimeException("Cart is empty. Cannot create an order.");
        }

        Order order = new Order();
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());

        BigDecimal totalOrderPrice = BigDecimal.ZERO;

        for (CartItem cartItem : cartItems) {
            totalOrderPrice = totalOrderPrice.add(cartItem.getTotalPrice());
        }

        order.setTotalPrice(totalOrderPrice);
        Order savedOrder = orderRepository.save(order);
        cartService.clearCart(userId);

        return new OrderDTO(savedOrder);
    }

    /**
     * Создание нового заказа
     */
    @Transactional
    public OrderDTO createOrder(Long userId, Long productId, int quantity) {
        User user = userService.getUserEntityById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Product product = productService.getProductById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (product.getStockQuantity() < quantity) {
            throw new RuntimeException("Insufficient stock");
        }

        Order order = new Order();
        order.setUser(user);
        order.setProduct(product);
        order.setQuantity(quantity);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(Status.NEW);

        BigDecimal totalPrice = product.getPrice().multiply(BigDecimal.valueOf(quantity));
        order.setTotalPrice(totalPrice);

        product.setStockQuantity(product.getStockQuantity() - quantity);
        productService.updateProduct(product);

        Order savedOrder = orderRepository.save(order);
        return new OrderDTO(savedOrder);
    }

    /**
     * Получение всех заказов пользователя
     */
    public List<OrderDTO> getUserOrders(Long userId) {
        return orderRepository.findByUserId(userId).stream()
                .map(OrderDTO::new)
                .collect(Collectors.toList());
    }

    /**
     * Получение заказа по ID
     */
    public Optional<OrderDTO> getOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .map(OrderDTO::new);
    }

    /**
     * Обновление статуса заказа
     */
    @Transactional
    public OrderDTO updateOrderStatus(Long orderId, Status status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        order.setStatus(status);
        Order updatedOrder = orderRepository.save(order);
        return new OrderDTO(updatedOrder);
    }

    /**
     * Отмена заказа
     */
    @Transactional
    public OrderDTO cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (Status.COMPLETED.equals(order.getStatus())) {
            throw new RuntimeException("Cannot cancel completed order");
        }

        Product product = order.getProduct();
        product.setStockQuantity(product.getStockQuantity() + order.getQuantity());
        productService.updateProduct(product);

        order.setStatus(Status.CANCELLED);
        Order savedOrder = orderRepository.save(order);
        return new OrderDTO(savedOrder);
    }

    /**
     * Преобразование Order в OrderDTO
     */
    private OrderDTO convertToDTO(Order order) {
        return new OrderDTO(order);
    }

    /**
     * Преобразование списка Orders в список OrderDTOs
     */
    private List<OrderDTO> convertToDTOList(List<Order> orders) {
        return orders.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    // Остальные методы ...

    /**
     * Получение списка купленных продуктов пользователем
     */
    public List<Product> getPurchasedProducts(Long userId) {
        List<Order> completedOrders = orderRepository.findCompletedOrdersByUserId(userId);

        // Преобразуем список заказов в поток, извлекаем продукты и собираем уникальные значения в список
        return completedOrders.stream()
                .map(Order::getProduct) // Получаем продукт из каждого заказа
                .filter(Objects::nonNull) // Исключаем null значения
                .distinct() // Убираем дубликаты
                .collect(Collectors.toList()); // Собираем результаты в список
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
    public Page<OrderDTO> getAllOrders(Pageable pageable) {
        Page<Order> ordersPage = orderRepository.findAll(pageable);
        return ordersPage.map(OrderDTO::new); // Преобразуем каждый заказ в OrderDTO
    }

    /**
     * Получение заказов за определенный период
     */
    public List<OrderDTO> getOrdersByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return orderRepository.findByOrderDateBetween(startDate, endDate).stream()
                .map(OrderDTO::new) // Преобразуем заказы в OrderDTO
                .collect(Collectors.toList());
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

        List<Order> completedOrders = orderRepository.findByUserIdAndStatus(userId, Status.COMPLETED);

        BigDecimal totalSpent = calculateUserTotalSpent(userId);
        BigDecimal averageOrderAmount = calculateUserAverageOrderAmount(userId);
        long totalOrders = orderRepository.countByUserIdAndStatus(userId, Status.COMPLETED);

        Optional<BigDecimal> maxOrderAmount = completedOrders.stream()
                .map(Order::getTotalPrice)
                .max(BigDecimal::compareTo);

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
    public List<OrderDTO> getOrdersAboveAmount(Long userId, BigDecimal amount) {
        return orderRepository.findByUserIdAndStatus(userId, Status.COMPLETED).stream()
                .filter(order -> order.getTotalPrice().compareTo(amount) > 0)
                .map(OrderDTO::new) // Преобразуем заказы в OrderDTO
                .collect(Collectors.toList());
    }

    /**
     * Расчет общей суммы заказов по месяцам
     */
    public Map<YearMonth, BigDecimal> calculateMonthlySpending(Long userId) {
        return orderRepository.findByUserIdAndStatus(userId, Status.COMPLETED).stream()
                .collect(Collectors.groupingBy(
                        order -> YearMonth.from(order.getOrderDate()),
                        Collectors.reducing(BigDecimal.ZERO, Order::getTotalPrice, BigDecimal::add)
                ));
    }

    public List<OrderDTO> getOrdersByUserId(Long userId) {
        return orderRepository.findByUserId(userId).stream()
                .map(OrderDTO::new) // Преобразуем заказы в OrderDTO
                .collect(Collectors.toList());
    }

    /**
     * Получить заказ по ID
     *
     * @param orderId Идентификатор заказа
     * @return OrderDTO – заказ с указанным ID
     * @throws RuntimeException если заказ не найден
     */
    public OrderDTO getOrderByIdOrThrow(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with ID: " + orderId));

        return new OrderDTO(order); // Преобразуем Order в OrderDTO и возвращаем
    }

}