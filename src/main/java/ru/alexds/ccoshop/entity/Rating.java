/**
 * Пакет для сущностей приложения.
 */
package ru.alexds.ccoshop.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Класс Rating представляет собой сущность, описывающую оценку (рейтинг) товара пользователем в системе.
 * Этот класс аннотирован как JPA-сущность и отображается в таблицу "ratings" в базе данных.
 * Для удобства использования Lombok генерирует геттеры, сеттеры, equals, hashCode и toString методы,
 * а также пустой и полностью параметризованный конструкторы.
 */
@Data // Генерирует геттеры, сеттеры, equals, hashCode и toString
@NoArgsConstructor // Генерирует пустой конструктор
@AllArgsConstructor // Генерирует конструктор с аргументами
@Entity // Аннотация для обозначения класса как JPA-сущности
@Table(name = "ratings") // Аннотация для указания имени таблицы в базе данных
public class Rating {

    /**
     * Уникальный идентификатор записи рейтинга.
     * Аннотация @Id указывает на то, что это поле является первичным ключом.
     * Аннотация @GeneratedValue стратегией GenerationType.IDENTITY позволяет автоматически генерировать значения.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Идентификатор пользователя, который выставил рейтинг.
     * Поле не может быть пустым.
     */
    @Column(name = "user_id", nullable = false)
    private Long userId;

    /**
     * Идентификатор товара, которому выставлен рейтинг.
     * Поле не может быть пустым.
     */
    @Column(name = "item_id", nullable = false)
    private Long itemId;

    /**
     * Рейтинг, выставленный пользователем.
     * Поле не может быть пустым и должно содержать числовое значение.
     */
    @Column(name = "rating", nullable = false)
    private Double rating;

    /**
     * Временная метка (в формате Unix времени).
     * Хранит время, когда был выставлен рейтинг.
     * Может быть null, если временная метка не задана.
     */
    @Column(name = "timestamp")
    private Long timestamp;
}