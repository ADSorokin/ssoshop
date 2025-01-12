package ru.alexds.ccoshop.service;


import org.springframework.data.domain.jaxb.SpringDataJaxb;
import ru.alexds.ccoshop.dto.CartItemDTO;
import ru.alexds.ccoshop.dto.OrderDTO;

import ru.alexds.ccoshop.dto.OrderItemDTO;
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
     * Создание нового заказа
     */
//    @Transactional
//    public OrderDTO createOrder(Long userId, Long productId, int quantity) {
//        User user = userService.getUserEntityById(userId)
//                .orElseThrow(() -> new RuntimeException("User not found"));
//
//        Product product = productService.getProductById(productId)
//                .orElseThrow(() -> new RuntimeException("Product not found"));
//
//        if (product.getStockQuantity() < quantity) {
//            throw new RuntimeException("Insufficient stock");
//        }
//
//        Order order = new Order();
//        order.setUser(user);
//        order.setProduct(product);
//        order.setQuantity(quantity);
//        order.setOrderDate(LocalDateTime.now());
//        order.setStatus(Status.NEW);
//
//        BigDecimal totalPrice = product.getPrice().multiply(BigDecimal.valueOf(quantity));
//        order.setTotalPrice(totalPrice);
//
//        product.setStockQuantity(product.getStockQuantity() - quantity);
//        productService.updateProduct(product);
//
//        Order savedOrder = orderRepository.save(order);
//        return new OrderDTO(savedOrder);
//    }

    @Transactional
    public OrderDTO createOrder(Long userId, Long productId, int quantity) {
        // Получение пользователя
        User user = userService.getUserEntityById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Получение продукта
        Product product = productService.getProductEntityById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // Проверка наличия на складе
        if (product.getStockQuantity() < quantity) {
            throw new RuntimeException("Insufficient stock");
        }

        // Создание нового заказа
        Order order = new Order();
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(Status.NEW);

        // Создание OrderItem для заказа
        OrderItem orderItem = new OrderItem();
        orderItem.setProduct(product);
        orderItem.setQuantity(quantity);
        orderItem.setPrice(product.getPrice()); // Устанавливаем цену товара

        // Добавление элемента заказа к заказу
        order.addItem(orderItem);

        // Вычисление общей стоимости заказа
        BigDecimal totalPrice = order.calculateTotalPrice();
        order.setTotalPrice(totalPrice);

        // Обновление количества товара на складе
        product.setStockQuantity(product.getStockQuantity() - quantity);
        productService.updateProduct(productService.convertToDTO(product));

        // Сохранение заказа, включая элементы заказа
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

//    /**
//     * Отмена заказа
//     */
//    @Transactional
//    public OrderDTO cancelOrder(Long orderId) {
//        Order order = orderRepository.findById(orderId)
//                .orElseThrow(() -> new RuntimeException("Order not found"));
//
//        if (Status.COMPLETED.equals(order.getStatus())) {
//            throw new RuntimeException("Cannot cancel completed order");
//        }
//
//        Product product = order.getProduct();
//        product.setStockQuantity(product.getStockQuantity() + order.getQuantity());
//        productService.updateProduct(product);
//
//        order.setStatus(Status.CANCELLED);
//        Order savedOrder = orderRepository.save(order);
//        return new OrderDTO(savedOrder);
//    }

    /**
     * Отмена заказа
     */
    @Transactional
    public OrderDTO cancelOrder(Long orderId) {
        // Получаем заказ
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        // Проверяем, можно ли отменить заказ
        if (Status.COMPLETED.equals(order.getStatus())) {
            throw new RuntimeException("Cannot cancel completed order");
        }

        // Возвращаем товары на склад для каждого элемента заказа
        for (OrderItem orderItem : order.getItems()) {
            Product product = orderItem.getProduct();
            // Увеличиваем количество товара на складе
            product.setStockQuantity(
                    product.getStockQuantity() + orderItem.getQuantity()
            );
            // Обновляем информацию о продукте
            productService.updateProduct(productService.convertToDTO(product));
        }

        // Устанавливаем статус заказа как отмененный
        order.setStatus(Status.CANCELLED);

        // Сохраняем обновленный заказ
        Order savedOrder = orderRepository.save(order);

        // Возвращаем DTO отмененного заказа
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


    /**
     * Получение списка купленных продуктов пользователем
     */
//    public List<Product> getPurchasedProducts(Long userId) {
////        List<Order> completedOrders = orderRepository.findCompletedOrdersByUserId(userId);
////
////        // Преобразуем список заказов в поток, извлекаем продукты и собираем уникальные значения в список
////        return completedOrders.stream()
////                .map(Order::getProduct) // Получаем продукт из каждого заказа
////                .filter(Objects::nonNull) // Исключаем null значения
////                .distinct() // Убираем дубликаты
////                .collect(Collectors.toList()); // Собираем результаты в список
////    }

    /**
     * Получение списка купленных продуктов пользователем
     */
    public List<Product> getPurchasedProducts(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID must not be null");
        }

        try {
            // Получаем все завершенные заказы пользователя
            List<Order> completedOrders = orderRepository.findCompletedOrdersByUserId(userId);

            if (completedOrders.isEmpty()) {
                return Collections.emptyList(); // Возвращаем пустой список, если нет заказов
            }

            // Преобразуем список заказов в список уникальных продуктов
            return completedOrders.stream()
                    .filter(order -> order.getItems() != null) // Проверяем, что у заказа есть элементы
                    .flatMap(order -> order.getItems().stream()) // Получаем поток OrderItem
                    .filter(Objects::nonNull) // Проверяем, что OrderItem не null
                    .map(OrderItem::getProduct) // Получаем продукт из OrderItem
                    .filter(Objects::nonNull) // Проверяем, что продукт не null
                    .distinct() // Убираем дубликаты
                    .collect(Collectors.toCollection(ArrayList::new)); // Собираем в ArrayList
        } catch (Exception e) {
            // Логирование ошибки
         //   log.error("Error while getting purchased products for user {}: {}", userId, e.getMessage());
            throw new RuntimeException("Failed to get purchased products", e);
        }
    }

    /**
     * Получение статистики заказов по продукту
     */
//    public long getProductOrderCount(Long productId) {
//        return orderRepository.countByProductIdAndStatus(productId, Status.COMPLETED);
//    }

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
        return orderRepository.existsByUser_IdAndItems_Product_IdAndStatus(userId, productId, Status.COMPLETED);
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

    // Создать заказ из всех товаров в корзине
    @Transactional
    public OrderDTO createOrderFromCart(Long userId) {
        // Получить все товары в корзине
        List<CartItemDTO> cartItems = cartService.getCartItemsForUser(userId);

        if (cartItems.isEmpty()) {
            throw new RuntimeException("Cart is empty, cannot create order");
        }

        // Создать новый заказ
        Order order = new Order();
        order.setUser(userService.getUserEntityById(userId).orElseThrow());
        order.setStatus(Status.NEW);
        order.setOrderDate(LocalDateTime.now());

        // Создаем и добавляем элементы заказа
        cartItems.forEach(cartItem -> {
            // Проверяем наличие продукта
            Product product = productService.getProductEntityById(cartItem.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found: " + cartItem.getProductId()));

            // Проверяем количество на складе
            if (product.getStockQuantity() < cartItem.getQuantity()) {
                throw new RuntimeException("Insufficient stock for product: " + product.getName());
            }

            // Уменьшаем количество на складе
            product.setStockQuantity(product.getStockQuantity() - cartItem.getQuantity());
            productService.updateProduct(productService.convertToDTO(product));

            // Создаем OrderItem и устанавливаем двустороннюю связь
            OrderItem orderItem = OrderItem.builder()
                    .product(product)
                    .quantity(cartItem.getQuantity())
                    .price(cartItem.getPrice())
                    .build();

            // Важно: устанавливаем двустороннюю связь
            order.addItem(orderItem);
        });

        // Рассчитываем общую стоимость
        order.setTotalPrice(order.calculateTotalPrice());

        // Сохраняем заказ
        Order savedOrder = orderRepository.save(order);

        // Очищаем корзину
        cartService.clearCartForUser(userId);

        // Конвертируем в DTO и возвращаем
        return convertOrderToDTO(savedOrder);
    }



    private OrderDTO convertOrderToDTO(Order order) {
        List<OrderItemDTO> itemsDto = order.getItems().stream()
                .map(item -> new OrderItemDTO(
                        item.getId(),
                        order.getId(),
                        item.getProduct().getId(),
                        item.getProduct().getName(),
                        item.getQuantity(),
                        item.getPrice()
 //                       item.getTotalPrice()
                ))
                .collect(Collectors.toList());

        return new OrderDTO(
                order.getId(),
                order.getUser().getId(),
                itemsDto,
                order.getOrderDate(),
                order.getStatus(),
                order.getTotalPrice()
        );
    }

}