package ru.alexds.ccoshop.entity;


import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "categories")
@Data // Генерирует геттеры, сеттеры и другие методы, такие как toString, equals и hashCode
@NoArgsConstructor // Генерирует пустой конструктор
@AllArgsConstructor
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Уникальный идентификатор категории

    @NotEmpty(message = "Category name cannot be empty") // Проверка, что название не пустое
    private String name; // Название категории
    @JsonBackReference
    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true) // Связь с продуктами
    private List<Product> products = new ArrayList<>(); // Список продуктов в этой категории

    // Конструктор для создания категории

    public Category(Long id) {
        this.id = id;
    }

    // Метод для добавления продукта в категорию
    public void addProduct(Product product) {
        products.add(product);
        product.setCategory(this); // Устанавливаем связь обратно
    }

    // Метод для удаления продукта из категории
    public void removeProduct(Product product) {
        products.remove(product);
        product.setCategory(null); // Убираем связь
    }
}
