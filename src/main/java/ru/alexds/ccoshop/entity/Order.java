package ru.alexds.ccoshop.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
@Data // Генерирует геттеры, сеттеры и другие методы
@NoArgsConstructor // Генерирует пустой конструктор
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Уникальный идентификатор заказа

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false) // Внешний ключ, ссылающийся на пользователя
    private User user; // Пользователь, который сделал заказ

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false) // Внешний ключ, ссылающийся на продукт
    private Product product; // Заказанный продукт

    @NotNull
    private Integer quantity; // Количество продукта в заказе

    private LocalDateTime orderDate; // Дата и время создания заказа

    @Enumerated(EnumType.STRING) // Сохранение статусов в виде строки
    private Status status; // Статус заказа

    private BigDecimal totalPrice;

    // Конструктор для создания заказа
    public Order(User user, Product product, int quantity) {
        this.user = user;
        this.product = product;
        this.quantity = quantity;
        this.orderDate = LocalDateTime.now(); // Установить дату создания
        this.status = Status.NEW; // Установить статус по умолчанию
        this.totalPrice = product.getPrice().multiply(BigDecimal.valueOf(quantity)); // Установка общей стоимости
    }
}
