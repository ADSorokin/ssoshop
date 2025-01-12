package ru.alexds.ccoshop.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.alexds.ccoshop.entity.CartItem;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItemDTO {
    private Long id;          // ID товара в корзине
    private Long userId;      // ID пользователя
    private Long productId;   // ID продукта
    private Integer quantity; // Количество
    private BigDecimal price; // Цена




    public CartItemDTO(CartItem cartItem) {
        this.id = cartItem.getId();
        this.userId =cartItem.getUser().getId();
        this.productId = cartItem.getProduct().getId();
        this.quantity = cartItem.getQuantity();
        this.price = cartItem.getPrice();
    }



}
