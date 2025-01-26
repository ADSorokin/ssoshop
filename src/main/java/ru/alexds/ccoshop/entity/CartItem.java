package ru.alexds.ccoshop.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;




@Entity
@Table(name = "cart_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /**
     * Пользователь, которому принадлежит корзина.
     * Отношение многие к одному с сущностью User.
     * Аннотация @ManyToOne указывает на отношение многие к одному.
     * Аннотация @JoinColumn задает имя внешнего ключа.
     * Аннотация FetchType.LAZY загружает связь по запросу.
     * Аннотация @NotNull проверяет, чтобы поле не было null.
     */
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // Пользователь, которому принадлежит корзина
    /**
     * Продукт в корзине.
     * Отношение многие к одному с сущностью Product.
     * Аннотация @ManyToOne указывает на отношение многие к одному.
     * Аннотация @JoinColumn задает имя внешнего ключа.
     * Аннотация FetchType.LAZY загружает связь по запросу.
     * Аннотация @NotNull проверяет, чтобы поле не было null.
     */
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product; // Продукт в корзине
    /**
     * Количество выбранного продукта.
     * Поле не может быть null и должно содержать положительное целое число.
     * Аннотация @NotNull проверяет, чтобы поле не было null.
     */
    @NotNull
    private Integer quantity; // Количество выбранного продукта
    /**
     * Цена продукта на момент добавления в корзину.
     * Поле может быть null, если цена не была установлена.
     * Это поле используется для фиксации цены продукта во времени, чтобы избежать изменений в цене товара после добавления его в корзину.
     */
    private BigDecimal price; // Цена продукта на момент добавления в корзину

//    /**
//     * Метод устанавливает обратную связь между продуктом и позицией в корзине.
//     *
//     * @param product Продукт, который нужно добавить в корзину.
//     */
//    public void setProduct(Product product) {
//        this.product = product;
//        if (product != null && product.getCartItems() != null) {
//            product.getCartItems().add(this);
//        }
//    }
//
//    /**
//     * Метод устанавливает обратную связь между пользователем и позицией в корзине.
//     *
//     * @param user Пользователь, которому принадлежит корзина.
//     */
//    public void setUser(User user) {
//        this.user = user;
//        if (user != null && user.getCartItems() != null) {
//            user.getCartItems().add(this);
//        }
//    }

}
