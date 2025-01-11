package ru.alexds.ccoshop.service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.alexds.ccoshop.dto.OrderItemDTO;
import ru.alexds.ccoshop.entity.OrderItem;
import ru.alexds.ccoshop.repository.OrderItemRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderItemService {

    private final OrderItemRepository orderItemRepository;

    /**
     * Получить все элементы заказа по идентификатору заказа
     *
     * @param orderId Идентификатор заказа
     * @return Список DTO элементов заказа
     */
    public List<OrderItemDTO> getOrderItemsByOrderId(Long orderId) {
        List<OrderItem> items = orderItemRepository.findByOrderId(orderId);

        // Преобразуем каждый OrderItem в OrderItemDTO
        return items.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Преобразование `OrderItem` в `OrderItemDTO`
     */
    private OrderItemDTO convertToDTO(OrderItem item) {
        return OrderItemDTO.builder()
                .id(item.getId())
                .productId(item.getProduct().getId())
                .productName(item.getProduct().getName())
                .quantity(item.getQuantity())
                .price(item.getPrice())
                .totalPrice(item.getTotalPrice())
                .build();
    }
}
