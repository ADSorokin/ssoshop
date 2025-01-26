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

/**
 * Класс OrderItem представляет собой сущность, описывающую позицию в заказе (товар и его количество).
 * Этот класс аннотирован как JPA-сущность и отображается в таблицу "order_items" в базе данных.
 * Для удобства использования Lombok генерирует геттеры, сеттеры, equals, hashCode и toString методы,
 * а также пустой, полностью параметризованный конструкторы и builder-конструктор.
 */
@Data // Генерирует геттеры, сеттеры, equals, hashCode и toString
@NoArgsConstructor // Генерирует пустой конструктор
@AllArgsConstructor // Генерирует конструктор с аргументами
@Builder // Генерирует builder-конструктор для удобного создания объектов
@Entity // Аннотация для обозначения класса как JPA-сущности
@Table(name = "order_items") // Аннотация для указания имени таблицы в базе данных
public class OrderItem {

    /**
     * Уникальный идентификатор позиции в заказе.
     * Аннотация @Id указывает на то, что это поле является первичным ключом.
     * Аннотация @GeneratedValue стратегией GenerationType.IDENTITY позволяет автоматически генерировать значения.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Заказ, к которому относится данная позиция.
     * Отношение многие к одному с сущностью Order.
     * Аннотация @ManyToOne указывает на отношение многие к одному.
     * Аннотация @JoinColumn задает имя внешнего ключа.
     * Аннотация FetchType.LAZY загружает связь по запросу.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    /**
     * Товар, входящий в данную позицию заказа.
     * Отношение многие к одному с сущностью Product.
     * Аннотация @ManyToOne указывает на отношение многие к одному.
     * Аннотация @JoinColumn задает имя внешнего ключа.
     * Аннотация FetchType.LAZY загружает связь по запросу.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    /**
     * Количество товара в данной позиции заказа.
     * Поле не может быть пустым.
     */
    @Column(nullable = false)
    private Integer quantity;

    /**
     * Цена за единицу товара в данной позиции заказа.
     * Поле не может быть пустым.
     */
    @Column(nullable = false)
    private BigDecimal price;

    /**
     * Метод вызывается перед сохранением или обновлением записи в базе данных.
     * Выполняет проверку, чтобы убедиться, что поле product не равно null.
     * Если поле product равно null, выбрасывается исключение IllegalStateException.
     */
    @PrePersist
    @PreUpdate
    private void validateProduct() {
        if (product == null) {
            throw new IllegalStateException("Product cannot be null for OrderItem");
        }
    }
}