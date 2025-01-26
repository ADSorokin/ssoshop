package ru.alexds.ccoshop.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "categories")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Уникальный идентификатор категории

    @NotEmpty(message = "Category name cannot be empty") // Проверка, что название не пустое
    private String name; // Название категории

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true) // Связь с продуктами
    private List<Product> products = new ArrayList<>(); // Список продуктов в этой категории

    // Конструктор для создания категории

    public Category(Long id) {
        this.id = id;
    }


}
