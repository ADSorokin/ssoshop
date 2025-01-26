/**
 * Пакет для DTO (Data Transfer Objects) приложения.
 */
package ru.alexds.ccoshop.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.alexds.ccoshop.entity.Category;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Класс CategoryDTO представляет собой DTO (Data Transfer Object), используемый для передачи данных о категории между слоями приложения.
 * Этот класс используется для упрощения и защиты передачи данных, избегая передачи ненужных или чувствительных полей.
 * Для удобства использования Lombok генерирует геттеры, сеттеры, equals, hashCode и toString методы,
 * а также пустой, полностью параметризованный конструкторы и builder-конструктор.
 * Аннотации валидации используются для проверки входных данных.
 */
@Data // Генерирует геттеры, сеттеры, equals, hashCode и toString
@NoArgsConstructor // Генерирует пустой конструктор
@AllArgsConstructor // Генерирует конструктор с аргументами
@Builder // Генерирует builder-конструктор для удобного создания объектов
public class CategoryDTO {

    /**
     * Уникальный идентификатор категории.
     * Поле может быть null, если категория еще не существует в базе данных.
     */
    private Long id;

    /**
     * Название категории.
     * Поле не может быть пустым или содержать только пробелы.
     * Аннотация @NotEmpty проверяет, чтобы строка была не пустой и не содержала только пробелы.
     * Сообщение об ошибке: "Category name cannot be empty".
     */
    @NotEmpty(message = "Category name cannot be empty")
    private String name;

    /**
     * Список идентификаторов продуктов, которые будут добавлены в данную категорию.
     * Это список значений типа Long, представляющий идентификаторы продуктов.
     * Поле может быть null или пустым, если продукты еще не назначены категории.
     */
    private List<Long> productIds;  // IDs продуктов для добавления в категорию

    /**
     * Конструктор для создания объекта CategoryDTO на основе существующего объекта Category.
     *
     * @param category Объект Category, из которого будут извлечены данные.
     */
    public CategoryDTO(Category category) {
        if (category != null) {
            this.id = category.getId();
            this.name = category.getName();
            // Предполагается, что у категории есть доступ к списку идентификаторов продуктов через метод getProductIds()
            if (category.getProducts() != null) {
                this.productIds = category.getProducts().stream()
                        .map(product -> product.getId())
                        .collect(Collectors.toList());
            }
        }
    }
}