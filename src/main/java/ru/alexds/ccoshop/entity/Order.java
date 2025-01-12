package ru.alexds.ccoshop.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

//@Entity
//@Table(name = "orders")
//@Data // Генерирует геттеры, сеттеры и другие методы
//@NoArgsConstructor // Генерирует пустой конструктор
//public class Order {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id; // Уникальный идентификатор заказа
//
//    @NotNull
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "user_id", nullable = false) // Внешний ключ, ссылающийся на пользователя
//    private User user; // Пользователь, который сделал заказ
//
////    @NotNull
////    @ManyToOne(fetch = FetchType.LAZY)
////    @JoinColumn(name = "product_id", nullable = false) // Внешний ключ, ссылающийся на продукт
////    private Product product; // Заказанный продукт
//
//    @NotNull
//    private Integer quantity; // Количество продукта в заказе
//
//    private LocalDateTime orderDate; // Дата и время создания заказа
//
//    @Enumerated(EnumType.STRING) // Сохранение статусов в виде строки
//    private Status status; // Статус заказа
//
//    private BigDecimal totalPrice;
//
//
//    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<OrderItem> items; // Список элементов в заказе
//
//    /**
//     * Удобный метод для добавления элемента в заказ
//     */
//    public void addItem(OrderItem item) {
//        this.items.add(item);
//        item.setOrder(this); // Устанавливаем связь с текущим заказом
//    }
//
//    /**
//     * Метод для вычисления итоговой стоимости заказа из всех OrderItem
//     */
//    public BigDecimal calculateTotalPrice() {
//        return items.stream()
//                .map(OrderItem::getTotalPrice)
//                .reduce(BigDecimal.ZERO, BigDecimal::add);
//    }
//
//    // Конструктор для создания заказа
////    public Order(User user, Product product, int quantity) {
////        this.user = user;
////        this.product = product;
////        this.quantity = quantity;
////        this.orderDate = LocalDateTime.now(); // Установить дату создания
////        this.status = Status.NEW; // Установить статус по умолчанию
////        this.totalPrice = product.getPrice().multiply(BigDecimal.valueOf(quantity)); // Установка общей стоимости
//    }


@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();

    private LocalDateTime orderDate;

    @Enumerated(EnumType.STRING)
    private Status status;

    private BigDecimal totalPrice;

    public void addItem(OrderItem item) {
        if (items == null) {
            items = new ArrayList<>();
        }
        items.add(item);
        item.setOrder(this); // Устанавливаем обратную связь
    }

    public BigDecimal calculateTotalPrice() {
        return items.stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}