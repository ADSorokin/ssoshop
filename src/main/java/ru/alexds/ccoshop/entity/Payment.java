/**
 * Пакет для сущностей приложения.
 */
package ru.alexds.ccoshop.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Класс Payment представляет собой сущность, описывающую платеж в системе.
 * Этот класс аннотирован как JPA-сущность и отображается в таблицу "payments" в базе данных.
 * Для удобства использования Lombok генерирует геттеры, сеттеры, equals, hashCode и toString методы,
 * а также пустой, полностью параметризованный конструкторы и builder-конструктор.
 */
@Data // Генерирует геттеры, сеттеры, equals, hashCode и toString
@NoArgsConstructor // Генерирует пустой конструктор
@AllArgsConstructor // Генерирует конструктор с аргументами
@Builder // Генерирует builder-конструктор для удобного создания объектов
@Entity // Аннотация для обозначения класса как JPA-сущности
@Table(name = "payments") // Аннотация для указания имени таблицы в базе данных
public class Payment {

    /**
     * Уникальный идентификатор платежа.
     * Аннотация @Id указывает на то, что это поле является первичным ключом.
     * Аннотация @GeneratedValue стратегией GenerationType.IDENTITY позволяет автоматически генерировать значения.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Идентификатор заказа, к которому относится данный платеж.
     * Поле не может быть пустым.
     */
    @Column(nullable = false)
    private Long orderId;

    /**
     * Сумма оплаты. Поле не может быть пустым.
     */
    @Column(nullable = false)
    private Double amount;

    /**
     * Валюта оплаты. Поле не может быть пустым.
     * Хранится в виде строки (например, "USD", "EUR").
     * Аннотация @Enumerated(EnumType.STRING) указывает на хранение перечисления в виде строки.
     */
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Currency currency;

    /**
     * Статус оплаты. Поле не может быть пустым.
     * Возможные значения: SUCCESS, FAILED.
     */
    @Column(nullable = false)
    private String status;

    /**
     * Сообщение о результате оплаты. Поле не может быть пустым.
     * Например, может содержать описание ошибки или подтверждение успешной оплаты.
     */
    @Column(nullable = false)
    private String message;

    /**
     * Время создания платежа. Поле не может быть пустым и не должно обновляться после создания.
     * Аннотация @Column(updatable = false) запрещает изменение этого поля после его создания.
     */
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Устанавливает значение идентификатора платежа.
     *
     * @param id Новый идентификатор платежа.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Возвращает текущий идентификатор платежа.
     *
     * @return Текущий идентификатор платежа.
     */
    public Long getId() {
        return id;
    }
}
