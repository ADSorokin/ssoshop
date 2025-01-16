package ru.alexds.ccoshop.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.alexds.ccoshop.dto.CategoryDTO;
import ru.alexds.ccoshop.entity.Category;
import ru.alexds.ccoshop.entity.Product;
import ru.alexds.ccoshop.repository.CategoryRepository;
import ru.alexds.ccoshop.repository.ProductRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    /**
     * Создание новой категории
     */
    public CategoryDTO createCategory(CategoryDTO categoryDTO) {
        Category category = Category.builder()
                .name(categoryDTO.getName())
                .products(new ArrayList<>())
                .build();

        Category savedCategory = categoryRepository.save(category);
        return convertToDTO(savedCategory);
    }

    /**
     * Получение категории по ID
     */
    public CategoryDTO getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category not found with id: " + id));
        return convertToDTO(category);
    }

    /**
     * Получение всех категорий
     */
    public List<CategoryDTO> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Обновление категории и добавление продуктов
     */
    public CategoryDTO updateCategory(Long id, CategoryDTO categoryDTO) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category not found with id: " + id));

        // Обновление имени категории
        category.setName(categoryDTO.getName());

        // Добавление товаров в категорию
        if (categoryDTO.getProductIds() != null) {
            for (Long productId : categoryDTO.getProductIds()) {
                Product product = productRepository.findById(productId)
                        .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + productId));
                category.getProducts().add(product);
            }
        }

        Category updatedCategory = categoryRepository.save(category);

        return convertToDTO(updatedCategory);
    }

    /**
     * Удаление категории
     */
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category not found with id: " + id));
        categoryRepository.delete(category);
    }
    /**
     * Добавление продукта в категорию
     */
    public CategoryDTO addProductToCategory(Long categoryId, Long productId) {
        // Поиск категории
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new EntityNotFoundException("Category not found with id: " + categoryId));

        // Поиск продукта
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + productId));

        // Проверяем, что продукт еще не привязан к категории
        if (category.getProducts().contains(product)) {
            throw new IllegalArgumentException("Product already exists in the category");
        }

        // Привязываем продукт к категории
        category.getProducts().add(product);
        product.setCategory(category); // Двусторонняя связь

        // Сохраняем изменения
        categoryRepository.save(category);

        // Возвращаем обновленную категорию в виде DTO
        return convertToDTO(category);
    }

    /**
     * Удаление продукта из категории
     */
    public CategoryDTO removeProductFromCategory(Long categoryId, Long productId) {
        // Поиск категории
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new EntityNotFoundException("Category not found with id: " + categoryId));

        // Поиск продукта
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + productId));

        // Проверяем, что продукт принадлежит категории
        if (!category.getProducts().contains(product)) {
            throw new IllegalArgumentException("The product does not belong to this category");
        }

        // Удаляем продукт из категории
        category.getProducts().remove(product);
        product.setCategory(null); // Убрать связь продукта с категорией

        // Сохраняем изменения
        categoryRepository.save(category);

        // Возвращаем обновленную категорию
        return convertToDTO(category);
    }


    /**
     * Конвертация Category в CategoryDTO
     */
    private CategoryDTO convertToDTO(Category category) {
        return CategoryDTO.builder()
                .id(category.getId())
                .name(category.getName())
                .productIds(category.getProducts().stream()
                        .map(Product::getId)
                        .collect(Collectors.toList()))
                .build();
    }
}
