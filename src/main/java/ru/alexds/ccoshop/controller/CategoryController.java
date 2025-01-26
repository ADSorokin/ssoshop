package ru.alexds.ccoshop.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.alexds.ccoshop.dto.CategoryDTO;
import ru.alexds.ccoshop.service.CategoryService;
import java.util.List;

/**
 * Контроллер для управления категориями товаров.
 * Обеспечивает API для создания, получения, обновления и удаления категорий,
 * а также для добавления и удаления продуктов из категорий.
 */
@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@Validated
@Slf4j
@Tag(name = "Category Controller", description = "API для работы с категориями товаров")
public class CategoryController {
    private final CategoryService categoryService; // Сервис для управления категориями

    /**
     * Создает новую категорию товара.
     *
     * @param categoryDTO DTO объект с информацией о создаваемой категории (например, имя и описание)
     * @return HTTP-ответ с созданной категорией в формате DTO и статусом 201 (Created)
     */
    @Operation(summary = "Создать новую категорию товара")
    @PostMapping
    public ResponseEntity<CategoryDTO> createCategory(@Valid @RequestBody CategoryDTO categoryDTO) {
        log.debug("Request to create a new category: {}", categoryDTO);
        CategoryDTO createdCategory = categoryService.createCategory(categoryDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCategory);
    }

    /**
     * Получает категорию по ее идентификатору.
     *
     * @param id Идентификатор категории, которую нужно получить
     * @return HTTP-ответ с категорией в формате DTO и статусом 200 (OK)
     * @throws EntityNotFoundException если категория с указанным идентификатором не найдена
     */
    @Operation(summary = "Получение  категории по ID категории")
    @GetMapping("/{id}")
    public ResponseEntity<CategoryDTO> getCategoryById(@PathVariable Long id) {
        log.debug("Request to get category by ID: {}", id);
        CategoryDTO category = categoryService.getCategoryById(id);
        return ResponseEntity.ok(category);
    }

    /**
     * Получает все категории товаров.
     *
     * @return HTTP-ответ со списком всех категорий в формате DTO и статусом 200 (OK)
     */
    @Operation(summary = "Вывод всех категорий")
    @GetMapping
    public ResponseEntity<List<CategoryDTO>> getAllCategories() {
        log.debug("Request to get all categories");
        List<CategoryDTO> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(categories);
    }

    /**
     * Обновляет существующую категорию и добавляет продукты в нее.
     *
     * @param id         Идентификатор категории, которую нужно обновить
     * @param categoryDTO DTO объект с новыми данными о категории (например, имя, описание и список продуктов)
     * @return HTTP-ответ с обновленной категорией в формате DTO и статусом 200 (OK)
     * @throws EntityNotFoundException если категория с указанным идентификатором не найдена
     */
    @Operation(summary = "Обновление категории и добавление товаров")
    @PutMapping("/{id}")
    public ResponseEntity<CategoryDTO> updateCategory(@PathVariable Long id, @Valid @RequestBody CategoryDTO categoryDTO) {
        log.debug("Request to update category with ID: {} and data: {}", id, categoryDTO);
        CategoryDTO updatedCategory = categoryService.updateCategory(id, categoryDTO);
        return ResponseEntity.ok(updatedCategory);
    }

    /**
     * Удаляет категорию по ее идентификатору.
     *
     * @param id Идентификатор категории, которую нужно удалить
     * @return HTTP-ответ без содержимого и статусом 204 (No Content), подтверждающий успешное удаление
     * @throws EntityNotFoundException если категория с указанным идентификатором не найдена
     */
    @Operation(summary = "Удаление категории")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        log.debug("Request to delete category with ID: {}", id);
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Добавляет продукт в указанную категорию.
     *
     * @param categoryId Идентификатор категории, в которую нужно добавить продукт
     * @param productId  Идентификатор продукта, который нужно добавить в категорию
     * @return HTTP-ответ с обновленной категорией в формате DTO и статусом 200 (OK)
     * @throws EntityNotFoundException если категория или продукт с указанными идентификаторами не найдены
     */
    @Operation(summary = "Добавление продукта в категорию")
    @PostMapping("/{categoryId}/products/{productId}")
    public ResponseEntity<CategoryDTO> addProductToCategory(@PathVariable Long categoryId, @PathVariable Long productId) {
        log.debug("Request to add product with ID: {} to category with ID: {}", productId, categoryId);
        CategoryDTO updatedCategory = categoryService.addProductToCategory(categoryId, productId);
        return ResponseEntity.ok(updatedCategory);
    }

    /**
     * Удаляет продукт из указанной категории.
     *
     * @param categoryId Идентификатор категории, из которой нужно удалить продукт
     * @param productId  Идентификатор продукта, который нужно удалить из категории
     * @return HTTP-ответ с обновленной категорией в формате DTO и статусом 200 (OK)
     * @throws EntityNotFoundException если категория или продукт с указанными идентификаторами не найдены
     */
    @Operation(summary = "Удаление продукта из категории")
    @DeleteMapping("/{categoryId}/products/{productId}")
    public ResponseEntity<CategoryDTO> removeProductFromCategory(@PathVariable Long categoryId, @PathVariable Long productId) {
        log.debug("Request to remove product with ID: {} from category with ID: {}", productId, categoryId);
        CategoryDTO updatedCategory = categoryService.removeProductFromCategory(categoryId, productId);
        return ResponseEntity.ok(updatedCategory);
    }
}