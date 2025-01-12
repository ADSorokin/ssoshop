package ru.alexds.ccoshop.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;


//@Entity
//@Table(name = "cart_items")
//@Data // Генерирует геттеры, сеттеры и другие методы
//@NoArgsConstructor // Генерирует пустой конструктор
//@AllArgsConstructor
//@Builder
//public class CartItem {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id; // Уникальный идентификатор элемента корзины
//
//    @NotNull
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "user_id", nullable = false) // Внешний ключ, ссылающийся на пользователя
//    private User user; // Пользователь, которому принадлежит корзина
//
//    @NotNull
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "product_id", nullable = false) // Внешний ключ, ссылающийся на продукт
//    private Product product; // Продукт, который добавлен в корзину
//
//    @NotNull
//    private Integer quantity; // Количество данного продукта в корзине
//
//    @Column(precision = 10, scale = 2) // Формат для хранения суммы
//    private BigDecimal totalPrice; // Общая цена за данное количество продукта
//
//    // Конструктор для создания элемента корзины
//    public CartItem(User user, Product product, int quantity) {
//        this.user = user;
//        this.product = product;
//        this.quantity = quantity;
//        this.totalPrice = product.getPrice().multiply(BigDecimal.valueOf(quantity)); // Вычислить общую цену
//    }
//
//
//    // Метод для обновления количества и пересчета общей цены
//    public void updateQuantity(int newQuantity) {
//        this.quantity = newQuantity;
//        this.totalPrice = product.getPrice().multiply(BigDecimal.valueOf(newQuantity)); // Обновить общую цену
//    }
//}


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

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // Пользователь, которому принадлежит корзина

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product; // Продукт в корзине

    @NotNull
    private Integer quantity; // Количество выбранного продукта

    private BigDecimal price; // Цена продукта на момент добавления в корзину
}
