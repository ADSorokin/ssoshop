/**
 * Пакет для сущностей приложения.
 */
package ru.alexds.ccoshop.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Класс Order представляет собой сущность, описывающую заказ в системе.
 * Этот класс аннотирован как JPA-сущность и отображается в таблицу "orders" в базе данных.
 * Для удобства использования Lombok генерирует геттеры, сеттеры, equals, hashCode и toString методы,
 * а также пустой, полностью параметризованный конструкторы и builder-конструктор.
 */
@Data // Генерирует геттеры, сеттеры, equals, hashCode и toString
@NoArgsConstructor // Генерирует пустой конструктор
@AllArgsConstructor // Генерирует конструктор с аргументами
@Builder // Генерирует builder-конструктор для удобного создания объектов
@Entity // Аннотация для обозначения класса как JPA-сущности
@Table(name = "orders") // Аннотация для указания имени таблицы в базе данных
public class Order {

    /**
     * Уникальный идентификатор заказа.
     * Аннотация @Id указывает на то, что это поле является первичным ключом.
     * Аннотация @GeneratedValue стратегией GenerationType.IDENTITY позволяет автоматически генерировать значения.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Пользователь, который сделал данный заказ.
     * Отношение многие к одному с сущностью User.
     * Аннотация @ManyToOne указывает на отношение многие к одному.
     * Аннотация @JoinColumn задает имя внешнего ключа.
     * Аннотация FetchType.LAZY загружает связь по запросу.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * Список позиций в данном заказе.
     * Отношение один ко многим с сущностью OrderItem.
     * Аннотация @OneToMany указывает на отношение один ко многим.
     * Аннотация mappedBy указывает на обратную связь в сущности OrderItem.
     * Аннотация CascadeType.ALL указывает на каскадное выполнение всех операций (сохранение, удаление и т.д.).
     * Аннотация orphanRemoval=true указывает на удаление "сиротских" записей.
     */
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();

    /**
     * Дата и время оформления заказа.
     */
    private LocalDateTime orderDate;

    /**
     * Статус заказа.
     * Хранится в виде строки (например, "NEW", "PAID", "SHIPPED").
     * Аннотация @Enumerated(EnumType.STRING) указывает на хранение перечисления в виде строки.
     */
    @Enumerated(EnumType.STRING)
    private Status status;

    /**
     * Общая сумма заказа.
     */
    private BigDecimal totalPrice;

    /**
     * Метод добавляет новую позицию в список позиций заказа и устанавливает обратную связь.
     *
     * @param item Новая позиция заказа.
     */
    public void addItem(OrderItem item) {
        if (items == null) {
            items = new ArrayList<>();
        }
        items.add(item);
        item.setOrder(this); // Устанавливаем обратную связь
    }

    /**
     * Метод вычисляет общую сумму заказа на основе количества и цены каждой позиции.
     *
     * @return Общая сумма заказа.
     */
    public BigDecimal calculateTotalPrice() {
        return items.stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}