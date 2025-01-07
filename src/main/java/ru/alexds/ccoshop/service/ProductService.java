package ru.alexds.ccoshop.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.alexds.ccoshop.entity.Product;
import ru.alexds.ccoshop.repository.ProductRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    /**
     * Получение всех продуктов
     */
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    /**
     * Получение продукта по ID
     */
    public Optional<Product> getProductById(Long productId) {
        return productRepository.findById(productId);
    }

    /**
     * Создание нового продукта
     */
    @Transactional
    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    /**
     * Обновление существующего продукта
     */
    @Transactional
    public Product updateProduct(Product product) {
        // Проверяем, существует ли продукт
        if (!productRepository.existsById(product.getId())) {
            throw new RuntimeException("Product not found");
        }
        return productRepository.save(product);
    }

    /**
     * Удаление продукта по ID
     */
    @Transactional
    public void deleteProduct(Long productId) {
        if (!productRepository.existsById(productId)) {
            throw new RuntimeException("Product not found");
        }
        productRepository.deleteById(productId);
    }

    /**
     * Получение популярных продуктов
     */
    public List<Product> getPopularProducts() {
        // Логика для получения популярных продуктов может зависеть от ваших требований,
        // например, на основе количества заказов или средней оценки.
        return productRepository.findTop10ByOrderByPopularityDesc(); // Пример, зависит от вашей реализации
    }

    /**
     * Получение продуктов по категории
     */
    public List<Product> getProductsByCategory(Long categoryId) {
        return productRepository.findByCategoryId(categoryId);
    }

    /**
     * Получение всех продуктов с пагинацией
     */
    public Page<Product> getPaginatedProducts(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    /**
     * Поиск продуктов по имени
     */
    public List<Product> searchProductsByName(String name) {
        return productRepository.findByNameContainingIgnoreCase(name);
    }

    /**
     * Получение продуктов по цене
     */
    public List<Product> getProductsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        return productRepository.findByPriceBetween(minPrice, maxPrice);
    }
}
