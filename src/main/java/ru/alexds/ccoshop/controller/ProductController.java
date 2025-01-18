package ru.alexds.ccoshop.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.alexds.ccoshop.dto.ProductDTO;
import ru.alexds.ccoshop.service.ProductService;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    /**
     * Получение всех продуктов
     */

    @Operation(summary = "Получение всех продуктов")
    @GetMapping
    public ResponseEntity<List<ProductDTO>> getAllProducts() {
        List<ProductDTO> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }

    /**
     * Получение продукта по ID
     */

    @Operation(summary = "Получение продукта по ID")
    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable Long id) {
        return productService.getProductById(id).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    /**
     * Создание нового продукта
     */

    @Operation(summary = "Создание нового продукта")
    @PostMapping
    public ResponseEntity<ProductDTO> createProduct(@RequestBody ProductDTO productDTO) {
        ProductDTO createdProduct = productService.createProduct(productDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);
    }

    /**
     * Обновление существующего продукта
     */

    @Operation(summary = "Обновление существующего продукта")
    @PutMapping("/{id}")

    public ResponseEntity<ProductDTO> updateProduct(@PathVariable Long id, @Valid @RequestBody ProductDTO updateDTO) {
        try {
            ProductDTO updatedProduct = productService.updateProduct(id, updateDTO);
            return ResponseEntity.ok(updatedProduct);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null); // или создайте ErrorDTO с сообщением об ошибке
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }


    /**
     * Удаление продукта по ID
     */
    @Operation(summary = "Удаление продукта по ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Получение популярных продуктов
     */

    @Operation(summary = "Получение популярных продуктов")
    @GetMapping("/popular")
    public ResponseEntity<List<ProductDTO>> getPopularProducts() {
        List<ProductDTO> popularProducts = productService.getPopularProducts();
        return ResponseEntity.ok(popularProducts);
    }

    /**
     * Поиск продуктов по имени
     */
    @Operation(summary = "Поиск продуктов по имени")
    @GetMapping("/search")
    public ResponseEntity<List<ProductDTO>> searchProducts(@RequestParam String name) {
        List<ProductDTO> products = productService.searchProductsByName(name);
        return ResponseEntity.ok(products);
    }

    /**
     * Получение продуктов по категории
     */
    @Operation(summary = "Получение продуктов по категории")
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<ProductDTO>> getProductsByCategory(@PathVariable Long categoryId) {
        List<ProductDTO> products = productService.getProductsByCategory(categoryId);
        return ResponseEntity.ok(products);
    }

    /**
     * Получение продуктов по цене
     */

    @Operation(summary = "Получение продуктов по цене min?max")
    @GetMapping("/price")
    public ResponseEntity<List<ProductDTO>> getProductsByPriceRange(@RequestParam("min") BigDecimal minPrice, @RequestParam("max") BigDecimal maxPrice) {
        List<ProductDTO> products = productService.getProductsByPriceRange(minPrice, maxPrice);
        return ResponseEntity.ok(products);
    }


}