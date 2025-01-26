/**
 * Пакет для DTO (Data Transfer Objects) приложения.
 */
package ru.alexds.ccoshop.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

/**
 * Класс ProductCreateDTO представляет собой DTO (Data Transfer Object), используемый для передачи данных о создании нового продукта между слоями приложения.
 * Этот класс используется для упрощения и защиты передачи данных, избегая передачи ненужных или чувствительных полей.
 * Для удобства использования Lombok генерирует геттеры, сеттеры, equals, hashCode и toString методы,
 * а также пустой, полностью параметризованный конструкторы и builder-конструктор.
 */
@Data // Генерирует геттеры, сеттеры, equals, hashCode и toString
@NoArgsConstructor // Генерирует пустой конструктор
@AllArgsConstructor // Генерирует конструктор с аргументами
@Builder // Генерирует builder-конструктор для удобного создания объектов
public class ProductCreateDTO {

    /**
     * Название продукта.
     * Аннотация @NotBlank проверяет, чтобы строка была не пустой и не содержала только пробелы.
     * Сообщение об ошибке: "Название продукта не может быть пустым".
     */
    @NotBlank(message = "Название продукта не может быть пустым")
    private String name;

    /**
     * Описание продукта.
     * Аннотация @NotBlank проверяет, чтобы строка была не пустой и не содержала только пробелы.
     * Сообщение об ошибке: "Описание не может быть пустым".
     */
    @NotBlank(message = "Описание не может быть пустым")
    private String description;

    /**
     * Цена продукта.
     * Аннотация @NotNull проверяет, чтобы поле было заполнено.
     * Аннотация @DecimalMin проверяет, чтобы значение было больше 0.
     * Сообщение об ошибке: "Цена должна быть больше 0".
     */
    @NotNull(message = "Price cannot be null")
    @DecimalMin(value = "0.0", inclusive = false, message = "Цена должна быть больше 0")
    private BigDecimal price;

    /**
     * Количество продукта на складе.
     * Аннотация @Min проверяет, чтобы значение было не меньше 0.
     * Сообщение об ошибке: "Количество запасов не может быть отрицательным".
     */
    @Min(value = 0, message = "Количество запасов не может быть отрицательным")
    private Integer stockQuantity;

    /**
     * Идентификатор категории продукта.
     * Поле может быть null, если категория еще не назначена.
     */
    private Long categoryId;
}