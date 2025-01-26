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
import ru.alexds.ccoshop.dto.ProductDTO;
import ru.alexds.ccoshop.entity.ErrorResponse;
import ru.alexds.ccoshop.service.ProductService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Контроллер для управления продуктами.
 * Обеспечивает API для создания, получения, обновления и удаления продуктов,
 * а также для поиска и фильтрации продуктов.
 */
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Validated
@Slf4j
@Tag(name = "Product Controller", description = "API для работы с продуктами")
public class ProductController {
    private final ProductService productService; // Сервис для управления продуктами

    /**
     * Получает все продукты.
     *
     * @return HTTP-ответ со списком всех продуктов в формате DTO и статусом 200 (OK)
     */
    @Operation(summary = "Получение всех продуктов")
    @GetMapping
    public ResponseEntity<List<ProductDTO>> getAllProducts() {
        log.debug("Request to get all products");
        List<ProductDTO> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }

    /**
     * Получает продукт по его идентификатору.
     *
     * @param id Идентификатор продукта, который необходимо получить
     * @return HTTP-ответ с продуктом в формате DTO и статусом 200 (OK), если продукт найден,
     *         или статусом 404 (Not Found), если продукт не найден
     */
    @Operation(summary = "Получение продукта по ID")
    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(@PathVariable Long id) {
        log.debug("Request to get product by ID: {}", id);
        Optional<ProductDTO> optionalProduct = productService.getProductById(id);
        if (optionalProduct.isPresent()) {
            return ResponseEntity.ok(optionalProduct.get());
        } else {
            ErrorResponse errorResponse = new ErrorResponse(
                    HttpStatus.NOT_FOUND.value(),
                    "PRODUCT_NOT_FOUND",
                    "Product with ID " + id + " not found"
            );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }

    /**
     * Создает новый продукт.
     *
     * @param productDTO DTO объект с информацией о создаваемом продукте (например, имя, описание, цена и категория)
     * @return HTTP-ответ с созданным продуктом в формате DTO и статусом 201 (Created)
     */
    @Operation(summary = "Создание нового продукта")
    @PostMapping
    public ResponseEntity<ProductDTO> createProduct(@RequestBody @Valid ProductDTO productDTO) {
        log.debug("Request to create a new product: {}", productDTO);
        ProductDTO createdProduct = productService.createProduct(productDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);
    }

    /**
     * Обновляет существующий продукт.
     *
     * @param id         Идентификатор продукта, который необходимо обновить
     * @param updateDTO  DTO объект с новыми данными о продукте (например, имя, описание, цена и категория)
     * @return HTTP-ответ с обновленным продуктом в формате DTO и статусом 200 (OK),
     *         или статусом 400 (Bad Request) при некорректных данных,
     *         или статусом 404 (Not Found) если продукт не найден
     */
    @Operation(summary = "Обновление существующего продукта")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable Long id, @Valid @RequestBody ProductDTO updateDTO) {
        log.debug("Request to update product with ID: {} and data: {}", id, updateDTO);
        try {
            ProductDTO updatedProduct = productService.updateProduct(id, updateDTO);
            return ResponseEntity.ok(updatedProduct);
        } catch (IllegalArgumentException e) {
            log.error("Invalid data for product update with ID {}: {}", id, e.getMessage());
            ErrorResponse errorResponse = new ErrorResponse(
                    HttpStatus.BAD_REQUEST.value(),
                    "INVALID_DATA",
                    "Invalid data provided for product update: " + e.getMessage()
            );
            return ResponseEntity.badRequest().body(errorResponse);
        } catch (EntityNotFoundException e) {
            log.error("Product not found with ID {}: {}", id, e.getMessage());
            ErrorResponse errorResponse = new ErrorResponse(
                    HttpStatus.NOT_FOUND.value(),
                    "PRODUCT_NOT_FOUND",
                    "Product with ID " + id + " not found"
            );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }

    /**
     * Удаляет продукт по его идентификатору.
     *
     * @param id Идентификатор продукта, который необходимо удалить
     * @return HTTP-ответ без содержимого и статусом 204 (No Content), подтверждающий успешное удаление
     * или HTTP-STATUS NOT_FOUND
     */
    @Operation(summary = "Удаление продукта по ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        log.debug("Request to delete product with ID: {}", id);
        try {
            productService.deleteProduct(id);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            log.error("Product not found with ID {}: {}", id, e.getMessage());
            ErrorResponse errorResponse = new ErrorResponse(
                    HttpStatus.NOT_FOUND.value(),
                    "PRODUCT_NOT_FOUND",
                    "Product with ID " + id + " not found"
            );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    /**
     * Получает популярные продукты.
     *
     * @return HTTP-ответ со списком всех популярных продуктов в формате DTO и статусом 200 (OK)
     */
    @Operation(summary = "Получение популярных продуктов")
    @GetMapping("/popular")
    public ResponseEntity<List<ProductDTO>> getPopularProducts() {
        log.debug("Request to get popular products");
        List<ProductDTO> popularProducts = productService.getPopularProducts();
        return ResponseEntity.ok(popularProducts);
    }

    /**
     * Поиск продуктов по имени.
     *
     * @param name Имя продукта для поиска
     * @return HTTP-ответ со списком продуктов в формате DTO и статусом 200 (OK)
     */
    @Operation(summary = "Поиск продуктов по имени")
    @GetMapping("/search")
    public ResponseEntity<List<ProductDTO>> searchProducts(@RequestParam String name) {
        log.debug("Request to search products by name: {}", name);
        List<ProductDTO> products = productService.searchProductsByName(name);
        return ResponseEntity.ok(products);
    }

    /**
     * Получает продукты по категории.
     *
     * @param categoryId Идентификатор категории, продукты которой необходимо получить
     * @return HTTP-ответ со списком всех продуктов в указанной категории в формате DTO и статусом 200 (OK)
     * @throws EntityNotFoundException если категория с указанным идентификатором не найдена
     */
    @Operation(summary = "Получение продуктов по категории")
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<ProductDTO>> getProductsByCategory(@PathVariable Long categoryId) {
        log.debug("Request to get products by category ID: {}", categoryId);
        List<ProductDTO> products = productService.getProductsByCategory(categoryId);
        return ResponseEntity.ok(products);
    }

    /**
     * Получает продукты в заданном диапазоне цен.
     *
     * @param minPrice Минимальная цена продукта
     * @param maxPrice Максимальная цена продукта
     * @return HTTP-ответ со списком всех продуктов в заданном диапазоне цен в формате DTO и статусом 200 (OK)
     */
    @Operation(summary = "Получение продуктов по цене min?max")
    @GetMapping("/price")
    public ResponseEntity<List<ProductDTO>> getProductsByPriceRange(
            @RequestParam("min") BigDecimal minPrice,
            @RequestParam("max") BigDecimal maxPrice) {
        log.debug("Request to get products in price range from {} to {}", minPrice, maxPrice);
        List<ProductDTO> products = productService.getProductsByPriceRange(minPrice, maxPrice);
        return ResponseEntity.ok(products);
    }
}