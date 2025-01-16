package ru.alexds.ccoshop.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.alexds.ccoshop.dto.CategoryDTO;
import ru.alexds.ccoshop.service.CategoryService;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@Validated
public class CategoryController {

    private final CategoryService categoryService;

    /**
     * Создание категории
     */
    @PostMapping
    public ResponseEntity<CategoryDTO> createCategory(@Valid @RequestBody CategoryDTO categoryDTO) {
        CategoryDTO createdCategory = categoryService.createCategory(categoryDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCategory);
    }

    /**
     * Получение категории по ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<CategoryDTO> getCategoryById(@PathVariable Long id) {
        CategoryDTO category = categoryService.getCategoryById(id);
        return ResponseEntity.ok(category);
    }

    /**
     * Получение всех категорий
     */
    @GetMapping
    public ResponseEntity<List<CategoryDTO>> getAllCategories() {
        List<CategoryDTO> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(categories);
    }

    /**
     * Обновление категории и добавление товаров
     */
    @PutMapping("/{id}")
    public ResponseEntity<CategoryDTO> updateCategory(@PathVariable Long id,
                                                      @Valid @RequestBody CategoryDTO categoryDTO) {
        CategoryDTO updatedCategory = categoryService.updateCategory(id, categoryDTO);
        return ResponseEntity.ok(updatedCategory);
    }

    /**
     * Удаление категории
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Добавление продукта в категорию
     */
    @PostMapping("/{categoryId}/products/{productId}")
    public ResponseEntity<CategoryDTO> addProductToCategory(@PathVariable Long categoryId,
                                                            @PathVariable Long productId) {
        CategoryDTO updatedCategory = categoryService.addProductToCategory(categoryId, productId);
        return ResponseEntity.ok(updatedCategory);
    }
    /**
     * Удаление продукта из категории
     */
    @DeleteMapping("/{categoryId}/products/{productId}")
    public ResponseEntity<CategoryDTO> removeProductFromCategory(@PathVariable Long categoryId,
                                                                 @PathVariable Long productId) {
        CategoryDTO updatedCategory = categoryService.removeProductFromCategory(categoryId, productId);
        return ResponseEntity.ok(updatedCategory);
    }
}
