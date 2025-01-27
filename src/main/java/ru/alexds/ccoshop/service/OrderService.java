package ru.alexds.ccoshop.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.alexds.ccoshop.dto.CartItemDTO;
import ru.alexds.ccoshop.dto.OrderDTO;
import ru.alexds.ccoshop.dto.OrderItemDTO;
import ru.alexds.ccoshop.dto.RatingDTO;
import ru.alexds.ccoshop.entity.*;
import ru.alexds.ccoshop.exeption.GlobalExceptionHandler;
import ru.alexds.ccoshop.exeption.OrderNotFoundException;
import ru.alexds.ccoshop.repository.OrderRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Сервис для управления заказами пользователей.
 * Обеспечивает API для создания, получения, обновления и удаления заказов,
 * а также для получения статистики по заказам и продуктам.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository; // Репозиторий для работы с заказами
    private final UserService userService; // Сервис для работы с пользователями
    private final ProductService productService; // Сервис для работы с продуктами
    private final CartService cartService; // Сервис для работы с корзинами
    private final RatingService ratingService; // Сервис для работы с рейтингами

    /**
     * Создает новый заказ на основе одного продукта.
     *
     * @param userId    Идентификатор пользователя, который создает заказ
     * @param productId Идентификатор продукта, который будет включен в заказ
     * @param quantity  Количество продуктов для заказа
     * @return DTO объект созданного заказа
     * @throws RuntimeException если пользователь или продукт не найдены, либо недостаточно запасов на складе
     */
    @Transactional
    public OrderDTO createOrder(Long userId, Long productId, int quantity) {
        log.debug("Request to create a new order for user ID: {} and product ID: {}", userId, productId);

        User user = userService.getUserEntityById(userId)
                .orElseThrow(() -> new RuntimeException("User not found")); // Проверяем наличие пользователя

        Product product = productService.getProductEntityById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found")); // Проверяем наличие продукта

        if (product.getStockQuantity() < quantity) {
            throw new RuntimeException("Insufficient stock for product: " + product.getName()); // Проверяем количество на складе
        }

        // Создаем новый заказ
        Order order = new Order();
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(Status.NEW);

        // Создаем элемент заказа и привязываем его к заказу
        OrderItem orderItem = new OrderItem();
        orderItem.setProduct(product);
        orderItem.setQuantity(quantity);
        orderItem.setPrice(product.getPrice()); // Устанавливаем цену товара

        order.addItem(orderItem); // Добавляем элемент заказа в заказ

        // Вычисляем общую стоимость заказа
        BigDecimal totalPrice = order.calculateTotalPrice();
        order.setTotalPrice(totalPrice);

        // Обновляем количество товара на складе
        product.setStockQuantity(product.getStockQuantity() - quantity);
        productService.updateProduct(product.getId(), productService.convertToDTO(product));

        Order savedOrder = orderRepository.save(order); // Сохраняем заказ в базе данных
        log.info("Заказ успешно создан с ID: {}", savedOrder.getId());

        return new OrderDTO(savedOrder); // Преобразуем сохраненный заказ в DTO и возвращаем
    }

    /**
     * Получает все заказы пользователя.
     *
     * @param userId Идентификатор пользователя, чьи заказы необходимо получить
     * @return Список всех заказов пользователя в формате DTO
     */
    public List<OrderDTO> getOrdersByUserId(Long userId) {
        log.debug("Запрос на получение всех заказов для пользователя ID: {}", userId);

        return orderRepository.findByUserId(userId).stream()
                .map(OrderDTO::new) // Преобразуем каждый заказ в DTO
                .collect(Collectors.toList());
    }

    /**
     * Получает заказ по его идентификатору.
     *
     * @param orderId Идентификатор заказа, который необходимо получить
     * @return Опциональный DTO объект заказа, если он найден
     */
    public Optional<OrderDTO> getOrderById(Long orderId) {
        log.debug("Запрос на получение заказа ID: {}", orderId);

        return orderRepository.findById(orderId)
                .map(OrderDTO::new); // Преобразуем найденный заказ в DTO
    }

    /**
     * Обновляет статус заказа.
     *
     * @param orderId Идентификатор заказа, который необходимо обновить
     * @param status  Новый статус заказа
     * @return DTO объект обновленного заказа
     * @throws RuntimeException если заказ не найден
     */
    @Transactional
    public OrderDTO updateOrderStatus(Long orderId, Status status) {
        log.debug("Запрос на обновление статуса заказа с помощью ID: {} to {}", orderId, status);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Заказ не найден")); // Проверяем наличие заказа

        order.setStatus(status); // Обновляем статус заказа
        Order updatedOrder = orderRepository.save(order); // Сохраняем изменения в базе данных

        log.info("Статус заказа успешно обновлен с помощью ID: {} to {}", orderId, status);
        return new OrderDTO(updatedOrder); // Преобразуем обновленный заказ в DTO и возвращаем
    }

    /**
     * Отменяет заказ.
     *
     * @param orderId Идентификатор заказа, который необходимо отменить
     * @return DTO объект отмененного заказа
     * @throws RuntimeException если заказ не найден или уже завершен
     */
    @Transactional
    public OrderDTO cancelOrder(Long orderId) {
        log.debug("Запрос на отмену заказа с помощью ID: {}", orderId);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Заказ не найден")); // Проверяем наличие заказа

        if (Status.COMPLETED.equals(order.getStatus())) {
            throw new RuntimeException("Невозможно отменить выполненный заказ"); // Проверяем возможность отмены заказа
        }

        // Возвращаем товары на склад для каждого элемента заказа
        for (OrderItem orderItem : order.getItems()) {
            Product product = orderItem.getProduct();
            product.setStockQuantity(product.getStockQuantity() + orderItem.getQuantity()); // Увеличиваем количество товара на складе
            productService.updateProduct(product.getId(), productService.convertToDTO(product)); // Обновляем информацию о продукте
        }

        order.setStatus(Status.CANCELLED); // Устанавливаем статус заказа как отмененный
        Order savedOrder = orderRepository.save(order); // Сохраняем обновленный заказ в базе данных

        log.info("Заказ успешно отменен с помощью ID: {}", orderId);
        return convertToDTO(savedOrder); // Преобразуем отмененный заказ в DTO и возвращаем
    }

    /**
     * Вспомогательный метод для преобразования объекта Order в DTO.
     *
     * @param order Объект заказа, который необходимо преобразовать
     * @return DTO объект заказа
     */
    private OrderDTO convertToDTO(Order order) {
        log.debug("Преобразование заказа с помощью ID: {} в DTO", order.getId());

        return new OrderDTO(order);
    }

    /**
     * Вспомогательный метод для преобразования списка объектов Order в список DTO.
     *
     * @param orders Список объектов заказов, которые необходимо преобразовать
     * @return Список DTO объектов заказов
     */
    private List<OrderDTO> convertToDTOList(List<Order> orders) {
        log.debug("Конвертирование списка заказаов в DTOs");

        return orders.stream()
                .map(this::convertToDTO) // Преобразуем каждый заказ в DTO
                .collect(Collectors.toList());
    }

    /**
     * Получает список всех купленных продуктов пользователем.
     *
     * @param userId Идентификатор пользователя, чьи купленные продукты необходимо получить
     * @return Список всех уникальных продуктов, которые были куплены пользователем
     * @throws IllegalArgumentException если идентификатор пользователя равен null
     */
    public List<Product> getPurchasedProducts(Long userId) {
        log.debug("Запрос на получение купленных продуктов для пользователя ID: {}", userId);

        if (userId == null) {
            throw new IllegalArgumentException("Идентификатор пользователя не должен быть null");
        }

        try {
            // Получаем все завершенные заказы пользователя
            List<Order> completedOrders = orderRepository.findCompletedOrdersByUserId(userId);
            if (completedOrders.isEmpty()) {
                log.warn("No completed orders found for user ID: {}", userId);
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
            log.error("Error while getting purchased products for user {}: {}", userId, e.getMessage());
            throw new RuntimeException("Failed to get purchased products", e);
        }
    }

    /**
     * Получает количество выполненных заказов для указанного продукта.
     *
     * @param productId Идентификатор продукта, для которого необходимо получить количество заказов
     * @return Количество выполненных заказов для данного продукта
     */
//    public long getProductOrderCount(Long productId) {
//        log.debug("Request to get order count for product ID: {}", productId);
//
//        return orderRepository.countByProductIdAndStatus(productId, Status.COMPLETED);
//    }

    /**
     * Получает пагинированный список всех заказов.
     *
     * @param pageable Параметры пагинации
     * @return Пагинированный список всех заказов в формате DTO
     */
    public Page<OrderDTO> getAllOrders(Pageable pageable) {
        log.debug("Запрос на получение всех заказов с пагинацией страниц");

        Page<Order> ordersPage = orderRepository.findAll(pageable); // Получаем страницу заказов
        return ordersPage.map(OrderDTO::new); // Преобразуем каждый заказ в OrderDTO
    }

    /**
     * Получает список заказов за определенный период времени.
     *
     * @param startDate Начальная дата диапазона
     * @param endDate   Конечная дата диапазона
     * @return Список заказов в указанном диапазоне в формате DTO
     */
    public List<OrderDTO> getOrdersByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        log.debug("Запрос на получение заказов между датами: {} and {}", startDate, endDate);

        return orderRepository.findByOrderDateBetween(startDate, endDate).stream()
                .map(OrderDTO::new) // Преобразуем заказы в OrderDTO
                .collect(Collectors.toList());
    }

    /**
     * Проверяет, покупал ли пользователь указанный продукт.
     *
     * @param userId    Идентификатор пользователя
     * @param productId Идентификатор продукта
     * @return true если пользователь покупал данный продукт, false в противном случае
     */
    public boolean hasUserPurchasedProduct(Long userId, Long productId) {
        log.debug("Request to check if user with ID: {} has purchased product with ID: {}", userId, productId);

        return orderRepository.existsByUser_IdAndItems_Product_IdAndStatus(userId, productId, Status.COMPLETED);
    }

    /**
     * Удаляет заказ по его идентификатору.
     *
     * @param orderId Идентификатор заказа, который необходимо удалить
     * @throws OrderNotFoundException если заказ с указанным идентификатором не найден
     */
    @Transactional
    public void deleteOrder(Long orderId)  {
        log.debug("Запрос на удаление заказа с ID: {}", orderId);

        if (!orderRepository.existsById(orderId)) {
            throw new OrderNotFoundException("Заказ не найден с ID: " + orderId);
        }

        if (orderRepository.findById(orderId).get().getStatus()!=Status.CANCELLED) {
            throw new OrderNotFoundException ("Только отмененные заказы  со статусом CANCELED можно удалить " + orderId);
        }
        orderRepository.deleteById(orderId); // Удаляем заказ из базы данных
        log.info("Заказ успешно удален с помощью ID: {}", orderId);
    }

    /**
     * Рассчитывает общую сумму всех выполненных заказов пользователя.
     *
     * @param userId Идентификатор пользователя, для которого необходимо рассчитать сумму
     * @return Общая сумма всех выполненных заказов пользователя
     */
    public BigDecimal calculateUserTotalSpent(Long userId) {
        log.debug("Request to calculate total spending for user ID: {}", userId);

        return orderRepository.findByUserIdAndStatus(userId, Status.COMPLETED).stream()
                .map(Order::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add); // Суммируем все стоимости заказов
    }

    /**
     * Рассчитывает среднюю сумму заказа пользователя.
     *
     * @param userId Идентификатор пользователя, для которого необходимо рассчитать среднее значение
     * @return Средняя сумма заказа пользователя
     */
    public BigDecimal calculateUserAverageOrderAmount(Long userId) {
        log.debug("Request to calculate average order amount for user ID: {}", userId);

        List<Order> completedOrders = orderRepository.findByUserIdAndStatus(userId, Status.COMPLETED);
        if (completedOrders.isEmpty()) {
            return BigDecimal.ZERO;
        }

        BigDecimal totalSpent = calculateUserTotalSpent(userId);
        return totalSpent.divide(BigDecimal.valueOf(completedOrders.size()), 2, RoundingMode.HALF_UP); // Вычисляем среднее значение
    }

    /**
     * Получает статистику заказов пользователя.
     *
     * @param userId Идентификатор пользователя, для которого необходимо получить статистику
     * @return Карта со статистическими данными по заказам пользователя
     */
    public Map<String, Object> getUserOrderStatistics(Long userId) {
        log.debug("Request to get order statistics for user ID: {}", userId);

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

        log.info("Retrieved order statistics for user ID: {}", userId);
        return statistics;
    }

    /**
     * Получает список заказов пользователя, сумма которых больше указанной.
     *
     * @param userId Идентификатор пользователя
     * @param amount Минимальная сумма заказа
     * @return Список заказов в формате DTO
     */
    public List<OrderDTO> getOrdersAboveAmount(Long userId, BigDecimal amount) {
        log.debug("Request to get orders above amount: {} for user ID: {}", amount, userId);

        return orderRepository.findByUserIdAndStatus(userId, Status.COMPLETED).stream()
                .filter(order -> order.getTotalPrice().compareTo(amount) > 0) // Фильтруем заказы по сумме
                .map(OrderDTO::new) // Преобразуем заказы в DTO
                .collect(Collectors.toList());
    }

    /**
     * Рассчитывает общую сумму заказов пользователя по месяцам.
     *
     * @param userId Идентификатор пользователя
     * @return Карта с ключами-месяцами и значениями-суммами заказов
     */
    public Map<YearMonth, BigDecimal> calculateMonthlySpending(Long userId) {
        log.debug("Request to calculate monthly spending for user ID: {}", userId);

        return orderRepository.findByUserIdAndStatus(userId, Status.COMPLETED).stream()
                .collect(Collectors.groupingBy(
                        order -> YearMonth.from(order.getOrderDate()), // Группируем заказы по месяцам
                        Collectors.reducing(BigDecimal.ZERO, Order::getTotalPrice, BigDecimal::add) // Суммируем стоимости заказов
                ));
    }

    /**
     * Получает заказ по его идентификатору или выбрасывает исключение, если заказ не найден.
     *
     * @param orderId Идентификатор заказа
     * @return DTO объект заказа
     * @throws RuntimeException если заказ не найден
     */
    public OrderDTO getOrderByIdOrThrow(Long orderId) {
        log.debug("Request to get order by ID: {}", orderId);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with ID: " + orderId)); // Проверяем наличие заказа

        log.info("Successfully retrieved order with ID: {}", orderId);
        return new OrderDTO(order); // Преобразуем Order в OrderDTO и возвращаем
    }

    /**
     * Создает новый заказ из всех товаров в корзине пользователя.
     *
     * @param userId Идентификатор пользователя, который создает заказ
     * @return DTO объект созданного заказа
     * @throws RuntimeException если корзина пуста, либо если один из продуктов в корзине не найден или недостаточно запасов на складе
     */
    @Transactional
    public OrderDTO createOrderFromCart(Long userId) {
        log.debug("Request to create an order from the cart for user ID: {}", userId);

        // Получаем все товары в корзине пользователя
        List<CartItemDTO> cartItems = cartService.getCartItemsForUser(userId);
        if (cartItems.isEmpty()) {
            throw new RuntimeException("Cart is empty, cannot create order");
        }

        // Создаем новый заказ
        Order order = new Order();
        order.setUser(userService.getUserEntityById(userId).orElseThrow(() -> new RuntimeException("User not found"))); // Привязываем пользователя к заказу
        order.setStatus(Status.NEW);
        order.setOrderDate(LocalDateTime.now());

        // Создаем и добавляем элементы заказа
        for (CartItemDTO cartItem : cartItems) {
            Product product = productService.getProductEntityById(cartItem.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found: " + cartItem.getProductId())); // Проверяем наличие продукта

            if (product.getStockQuantity() < cartItem.getQuantity()) {
                throw new RuntimeException("Insufficient stock for product: " + product.getName()); // Проверяем количество на складе
            }

            // Уменьшаем количество товара на складе
            product.setStockQuantity(product.getStockQuantity() - cartItem.getQuantity());
            productService.updateProduct(product.getId(), productService.convertToDTO(product)); // Обновляем информацию о продукте

            // Создаем OrderItem и привязываем его к заказу
            OrderItem orderItem = OrderItem.builder()
                    .product(product)
                    .quantity(cartItem.getQuantity())
                    .price(cartItem.getPrice())
                    .build();

            order.addItem(orderItem); // Добавляем элемент заказа в заказ
        }

        // Рассчитываем общую стоимость заказа
        order.setTotalPrice(order.calculateTotalPrice());

        // Сохраняем заказ в базе данных
        Order savedOrder = orderRepository.save(order);
        log.info("Successfully created order with ID: {} from user's cart", savedOrder.getId());

        // Очищаем корзину пользователя после успешного создания заказа
        cartService.clearCartForUser(userId);

        // Устанавливаем рейтинги товарам в заказе
        saveRatingsForOrder(savedOrder);

        // Преобразуем заказ в DTO и возвращаем
        return convertOrderToDTO(savedOrder);
    }

    /**
     * Вспомогательный метод для преобразования объекта Order в DTO.
     *
     * @param order Объект заказа, который необходимо преобразовать
     * @return DTO объект заказа
     */
    private OrderDTO convertOrderToDTO(Order order) {
        log.debug("Converting order with ID: {} to DTO", order.getId());

        List<OrderItemDTO> itemsDto = order.getItems().stream()
                .map(item -> new OrderItemDTO(
                        item.getId(),
                        order.getId(),
                        item.getProduct().getId(),
                        item.getProduct().getName(),
                        item.getQuantity(),
                        item.getPrice()

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

    /**
     * Устанавливает рейтинги товарам в заказе.
     *
     * @param order Объект заказа, для которого необходимо установить рейтинги
     */
    private void saveRatingsForOrder(Order order) {
        log.info("Saving ratings for order: {}", order.getId());

        for (OrderItem item : order.getItems()) {
            RatingDTO ratingDTO = RatingDTO.builder()
                    .userId(order.getUser().getId())
                    .itemId(item.getProduct().getId())
                    .rating(item.getProduct().getPopularity()) // Можно использовать другие метрики для рейтинга
                    .build();

            try {
                Rating savedRating = ratingService.saveRating(ratingDTO);
                log.info("Saved rating: {}", savedRating);
            } catch (Exception e) {
                log.error("Error saving rating for product {} in order {}: {}", item.getProduct().getId(), order.getId(), e.getMessage());
            }
        }
    }
}