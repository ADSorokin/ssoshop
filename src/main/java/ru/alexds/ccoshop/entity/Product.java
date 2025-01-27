/**
 * Пакет для сущностей приложения.
 */
package ru.alexds.ccoshop.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Класс Product представляет собой сущность, описывающую товар в системе.
 * Этот класс аннотирован как JPA-сущность и отображается в таблицу "products" в базе данных.
 * Для удобства использования Lombok генерирует геттеры, сеттеры, equals, hashCode и toString методы,
 * а также пустой, полностью параметризованный конструкторы и builder-конструктор.
 */
@Data // Генерирует геттеры, сеттеры, equals, hashCode и toString
@NoArgsConstructor // Генерирует пустой конструктор
@AllArgsConstructor // Генерирует конструктор с аргументами
@Builder // Генерирует builder-конструктор для удобного создания объектов
@Entity // Аннотация для обозначения класса как JPA-сущности
@Table(name = "products") // Аннотация для указания имени таблицы в базе данных
public class Product {

    /**
     * Уникальный идентификатор товара.
     * Аннотация @Id указывает на то, что это поле является первичным ключом.
     * Аннотация @GeneratedValue стратегией GenerationType.IDENTITY позволяет автоматически генерировать значения.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Название товара. Поле не может быть пустым.
     * Аннотация @NotEmpty проверяет, чтобы строка была не пустой.
     */
    @NotEmpty(message = "Product name cannot be empty")
    private String name;

    /**
     * Описание товара. Поле не может быть пустым.
     * Аннотация @NotEmpty проверяет, чтобы строка была не пустой.
     */
    @NotEmpty(message = "Description cannot be empty")
    private String description;

    /**
     * Цена товара. Поле не может быть пустым.
     */
    @Column(nullable = false)
    private BigDecimal price;

    /**
     * Количество товара на складе. Поле не может быть пустым.
     */
    @Column(name = "stock_quantity", nullable = false)
    private int stockQuantity;

    /**
     * Популярность товара. Поле не может быть пустым.
     */
    @Column(nullable = false)
    private Double popularity;

    /**
     * Характеристики товара.
     */
    @ElementCollection
    @CollectionTable(name = "product_characteristics", joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "characteristic")
    private List<String> characteristics;
    /**
     * Категория товара. Отношение многие к одному с сущностью Category.
     * Аннотация @ManyToOne указывает на отношение многие к одному.
     * Аннотация @JoinColumn задает имя внешнего ключа.
     * Аннотация @JsonBackReference предотвращает рекурсивное сериализацию JSON.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    @JsonBackReference
    private Category category;

    /**
     * Путь к изображению товара.
     */
    @Column(name = "image_path")
    private String imagePath;

    /**
     * Дата создания записи товара. Устанавливается автоматически при создании записи.
     * Аннотация @CreationTimestamp автоматически устанавливает текущее время при создании записи.
     */
    @Column(name = "created_at")
    @CreationTimestamp
    private LocalDateTime createdAt;

    /**
     * Дата последнего обновления записи товара. Устанавливается автоматически при обновлении записи.
     * Аннотация @UpdateTimestamp автоматически устанавливает текущее время при обновлении записи.
     */
    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    /**
     * Метод вызывается перед сохранением новой записи в базе данных.
     * Устанавливает текущее время в поля createdAt и updatedAt.
     */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    /**
     * Метод вызывается перед обновлением существующей записи в базе данных.
     * Устанавливает текущее время в поле updatedAt.
     */
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /**
     * Конструктор с основными параметрами для создания нового товара.
     *
     * @param name           Название товара.
     * @param description    Описание товара.
     * @param price          Цена товара.
     * @param stockQuantity  Количество товара на складе.
     * @param category       Категория товара.
     * @param imagePath      Путь к изображению товара.
     */
    public Product(String name, String description, BigDecimal price, int stockQuantity, Category category, List<String> characteristic ,String imagePath) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.category = category;
        this.characteristics = characteristic;
        this.imagePath = imagePath;
    }



}