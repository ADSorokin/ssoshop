package ru.alexds.ccoshop.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.alexds.ccoshop.dto.ProductDTO;
import ru.alexds.ccoshop.entity.Product;
import ru.alexds.ccoshop.service.ProductService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    /**
     * Получение всех продуктов
     */
    @Tag(name = "Product", description = "Получение всех продуктов")
    @GetMapping
    public ResponseEntity<List<ProductDTO>> getAllProducts() {
        List<ProductDTO> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }

    /**
     * Получение продукта по ID
     */
    @Tag(name = "Product", description = "Получение продукта по ID")
    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable Long id) {
        return productService.getProductById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    /**
     * Создание нового продукта
     */
    @Tag(name = "Product", description = "Создание нового продукта")
    @PostMapping
    public ResponseEntity<ProductDTO> createProduct(@RequestBody ProductDTO productDTO) {
        ProductDTO createdProduct = productService.createProduct(productDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);
    }

    /**
     * Обновление существующего продукта
     */
    @Tag(name = "Product", description = "Обновление существующего продукта")
    @PutMapping("/{id}")
    public ResponseEntity<ProductDTO> updateProduct(@PathVariable Long id, @RequestBody ProductDTO productDTO) {
        productDTO.setId(id); // Устанавливаем ID для обновления
        ProductDTO updatedProduct = productService.updateProduct(productDTO);
        return ResponseEntity.ok(updatedProduct);
    }

    /**
     * Удаление продукта по ID
     */
    @Tag(name = "Product", description = "Удаление продукта по ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Получение популярных продуктов
     */
    @Tag(name = "Product", description = "Получение популярных продуктов")
    @GetMapping("/popular")
    public ResponseEntity<List<ProductDTO>> getPopularProducts() {
        List<ProductDTO> popularProducts = productService.getPopularProducts();
        return ResponseEntity.ok(popularProducts);
    }

    /**
     * Поиск продуктов по имени
     */
    @Tag(name = "Product", description = "Поиск продуктов по имени")
    @GetMapping("/search")
    public ResponseEntity<List<ProductDTO>> searchProducts(@RequestParam String name) {
        List<ProductDTO> products = productService.searchProductsByName(name);
        return ResponseEntity.ok(products);
    }

    /**
     * Получение продуктов по категории
     */
    @Tag(name = "Product", description = "Получение продуктов по категории")
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<ProductDTO>> getProductsByCategory(@PathVariable Long categoryId) {
        List<ProductDTO> products = productService.getProductsByCategory(categoryId);
        return ResponseEntity.ok(products);
    }

    /**
     * Получение продуктов по цене
     */
    @Tag(name = "Product", description = "Получение продуктов по цене min?max")
    @GetMapping("/price")
    public ResponseEntity<List<ProductDTO>> getProductsByPriceRange(
            @RequestParam("min") BigDecimal minPrice,
            @RequestParam("max") BigDecimal maxPrice) {
        List<ProductDTO> products = productService.getProductsByPriceRange(minPrice, maxPrice);
        return ResponseEntity.ok(products);
    }
}