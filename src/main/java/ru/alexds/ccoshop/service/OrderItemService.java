package ru.alexds.ccoshop.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.alexds.ccoshop.dto.OrderItemDTO;
import ru.alexds.ccoshop.entity.OrderItem;
import ru.alexds.ccoshop.repository.OrderItemRepository;
import ru.alexds.ccoshop.utilites.PriceUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Сервис для управления элементами заказа.
 * Обеспечивает API для получения элементов заказа по идентификатору заказа.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderItemService {
    private final OrderItemRepository orderItemRepository; // Репозиторий для работы с элементами заказа
private final PriceUtils priceUtils;
    /**
     * Получает все элементы заказа по идентификатору заказа.
     *
     * @param orderId Идентификатор заказа, элементы которого необходимо получить
     * @return Список DTO элементов заказа
     */
    public List<OrderItemDTO> getOrderItemsByOrderId(Long orderId) {
        log.debug("Запрос на получение позиций заказа по заказу ID: {}", orderId);

        // Получаем все элементы заказа из репозитория по идентификатору заказа
        List<OrderItem> items = orderItemRepository.findByOrderId(orderId);

        // Если нет элементов заказа, возвращаем пустой список
        if (items.isEmpty()) {
            log.warn("Товары для заказа не найдены ID: {}", orderId);
            return List.of(); // Возвращаем пустой список, если нет элементов заказа
        }

        // Преобразуем каждый OrderItem в OrderItemDTO
        List<OrderItemDTO> orderItemDTOs = items.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        log.info("Успешно получено {} позиций заказа по ID: {}", orderItemDTOs.size(), orderId);
        return orderItemDTOs;
    }

    /**
     * Вспомогательный метод для преобразования объекта `OrderItem` в DTO.
     *
     * @param item Элемент заказа, который необходимо преобразовать
     * @return DTO объект элемента заказа
     */
    private OrderItemDTO convertToDTO(OrderItem item) {
        log.debug("Converting order item with ID: {} to DTO", item.getId());

        // Создаем DTO объект на основе данных элемента заказа
        OrderItemDTO dto = OrderItemDTO.builder()
                .id(item.getId())
                .orderId(item.getOrder().getId())
                .productId(item.getProduct().getId())
                .productName(item.getProduct().getName())
                .quantity(item.getQuantity())
                .price(item.getPrice())
                .totalPrice(calculateTotalPrice(item))
                .build();

        log.debug("Преобразованный элемент заказа с ID: {} в DTO: {}", item.getId(), dto);
        return dto;



    }

    /**
     * Метод для вычисления общей стоимости позиции в заказе.
     *
     * @return Общая стоимость позиции в заказе, рассчитанная как произведение количества и цены за единицу товара.
     */
    private BigDecimal calculateTotalPrice(OrderItem orderItem) {
        if (orderItem.getPrice() != null && orderItem.getQuantity() != null) {
            return orderItem.getPrice().multiply(BigDecimal.valueOf(orderItem.getQuantity()));
        }
        return BigDecimal.ZERO;
    }
}
