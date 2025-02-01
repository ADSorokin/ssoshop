
/**
 * Пакет для DTO (Data Transfer Objects) приложения.
 */
package ru.alexds.ccoshop.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.alexds.ccoshop.entity.Product;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Класс ProductDTO представляет собой DTO (Data Transfer Object), используемый для передачи данных о продукте между слоями приложения.
 * Этот класс используется для упрощения и защиты передачи данных, избегая передачи ненужных или чувствительных полей.
 * Для удобства использования Lombok генерирует геттеры, сеттеры, equals, hashCode и toString методы,
 * а также пустой, полностью параметризованный конструкторы и builder-конструктор.
 */
@Data // Генерирует геттеры, сеттеры, equals, hashCode и toString
@NoArgsConstructor // Генерирует пустой конструктор
@AllArgsConstructor // Генерирует конструктор с аргументами
@Builder // Генерирует builder-конструктор для удобного создания объектов
public class ProductDTO {

    /**
     * Уникальный идентификатор продукта.
     */
    private Long id;

    /**
     * Название продукта.
     */
    private String name;

    /**
     * Описание продукта.
     */
    private String description;

    /**
     * Цена продукта.
     */
    private BigDecimal price;

    /**
     * Количество продукта на складе.
     */
    private Integer stockQuantity;

    /**
     * Популярность продукта.
     */
    private double popularity;

    private List<String> characteristic;
    /**
     * Идентификатор категории продукта.
     * Если нужно передать информацию о категории.
     */
    private Long categoryId; // если нужно передать информацию о категории

    /**
     * Путь к изображению продукта.
     */
    private String imagePath;

    /**
     * Дата создания продукта.
     */
    private LocalDateTime createAt;

    /**
     * Дата последнего обновления продукта.
     */
    private LocalDateTime updatedAt;

    /**
     * Конструктор для создания объекта ProductDTO на основе существующего объекта Product.
     *
     * @param product Объект Product, из которого будут извлечены данные.
     */
    public ProductDTO(Product product) {
        this.id = product.getId();
        this.name = product.getName();
        this.description = product.getDescription();
        this.price = product.getPrice();
        this.stockQuantity = product.getStockQuantity();
        this.popularity = product.getPopularity();
        this.characteristic = product.getCharacteristics();
        this.categoryId = product.getCategory() != null ? product.getCategory().getId() : null;
    }



    public ProductDTO(Long id, String name, double popularity) {
        this.id = id;
        this.name = name;
        this.popularity = popularity;
    }
}