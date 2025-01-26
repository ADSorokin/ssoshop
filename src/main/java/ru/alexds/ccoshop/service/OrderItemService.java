package ru.alexds.ccoshop.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.alexds.ccoshop.dto.OrderItemDTO;
import ru.alexds.ccoshop.entity.OrderItem;
import ru.alexds.ccoshop.repository.OrderItemRepository;

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

    /**
     * Получает все элементы заказа по идентификатору заказа.
     *
     * @param orderId Идентификатор заказа, элементы которого необходимо получить
     * @return Список DTO элементов заказа
     */
    public List<OrderItemDTO> getOrderItemsByOrderId(Long orderId) {
        log.debug("Request to get order items by order ID: {}", orderId);

        // Получаем все элементы заказа из репозитория по идентификатору заказа
        List<OrderItem> items = orderItemRepository.findByOrderId(orderId);

        // Если нет элементов заказа, возвращаем пустой список
        if (items.isEmpty()) {
            log.warn("No order items found for order ID: {}", orderId);
            return List.of(); // Возвращаем пустой список, если нет элементов заказа
        }

        // Преобразуем каждый OrderItem в OrderItemDTO
        List<OrderItemDTO> orderItemDTOs = items.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        log.info("Successfully retrieved {} order items for order ID: {}", orderItemDTOs.size(), orderId);
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
                .productId(item.getProduct().getId())
                .productName(item.getProduct().getName())
                .quantity(item.getQuantity())
                .price(item.getPrice())
                .build();

        log.debug("Converted order item with ID: {} to DTO: {}", item.getId(), dto);
        return dto;
    }
}
