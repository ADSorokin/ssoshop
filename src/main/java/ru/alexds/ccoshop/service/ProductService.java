package ru.alexds.ccoshop.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.alexds.ccoshop.dto.ProductDTO;
import ru.alexds.ccoshop.entity.Category;
import ru.alexds.ccoshop.entity.Product;
import ru.alexds.ccoshop.repository.CategoryRepository;
import ru.alexds.ccoshop.repository.ProductRepository;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    private static final int POPULAR_PRODUCTS_LIMIT = 5;


    public List<ProductDTO> getAllProducts() {
        return productRepository.findAll().stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public Optional<ProductDTO> getProductById(Long id) {
        return productRepository.findById(id).map(this::convertToDTO);
    }

    public Optional<Product> getProductEntityById(Long id) {
        return productRepository.findById(id);

    }

    public ProductDTO createProduct(ProductDTO productDTO) {
//        Product product = convertToEntity(productDTO);
//        Product createdProduct = productRepository.save(product);
//        return convertToDTO(createdProduct);
        // Проверяем существование категории
        Category category = categoryRepository.findById(productDTO.getCategoryId())
                .orElseThrow(() -> new EntityNotFoundException("Category not found"));

        // Создаем новый продукт
        Product product = Product.builder()
                .name(productDTO.getName())
                .description(productDTO.getDescription())
                .price(productDTO.getPrice())
                .stockQuantity(productDTO.getStockQuantity())
                .imagePath(productDTO.getImagePath())
                .category(category)
                .popularity(productDTO.getPopularity())
                .build();

        // Сохраняем продукт
        Product savedProduct = productRepository.save(product);

        // Возвращаем DTO
        return convertToDTO(savedProduct);


    }

    public ProductDTO updateProduct(Long id, ProductDTO productDTO) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));

        // Обновляем только переданные поля
        if (productDTO.getName() != null) {
            product.setName(productDTO.getName());
        }
        if (productDTO.getDescription() != null) {
            product.setDescription(productDTO.getDescription());
        }
        if (productDTO.getPrice() != null) {
            product.setPrice(productDTO.getPrice());
        }
        if (productDTO.getStockQuantity() != null) {
            product.setStockQuantity(productDTO.getStockQuantity());
        }
        if (productDTO.getImagePath() != null) {
            product.setImagePath(productDTO.getImagePath());
        }
        if (productDTO.getCategoryId() != null) {
            Category category = categoryRepository.findById(productDTO.getCategoryId())
                    .orElseThrow(() -> new EntityNotFoundException("Category not found"));
            product.setCategory(category);
        }

        Product updatedProduct = productRepository.save(product);
        return convertToDTO(updatedProduct);
    }

    public ProductDTO convertToDTO(Product product) {
        return ProductDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stockQuantity(product.getStockQuantity())
                .imagePath(product.getImagePath())
                .categoryId(product.getCategory().getId())
                .popularity(product.getPopularity())
                .build();
    }

//    public ProductDTO updateProduct(ProductDTO productDTO) {
//        Product product = convertToEntity(productDTO);
//        Product updatedProduct = productRepository.save(product);
//        return convertToDTO(updatedProduct);
//    }

    // Другие методы...

//    ProductDTO convertToDTO(Product product) {
//        return ProductDTO.builder()
//                .id(product.getId())
//                .name(product.getName())
//                .description(product.getDescription())
//                .price(product.getPrice())
//                .stockQuantity(product.getStockQuantity())
//                .categoryId(product.getCategory().getId()) // если необходимо
//                .build();
//    }

    private Product convertToEntity(ProductDTO productDTO) {
        return Product.builder().id(productDTO.getId()).name(productDTO.getName()).description(productDTO.getDescription()).price(productDTO.getPrice()).stockQuantity(productDTO.getStockQuantity()).category(new Category(productDTO.getCategoryId())) // если необходимо
                .build();
    }

    /**
     * Получение популярных продуктов
     */
    public List<ProductDTO> getPopularProducts() {
        return productRepository.findAllByOrderByPopularityDescCreatedAtDesc(PageRequest.of(0, POPULAR_PRODUCTS_LIMIT)).stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    /**
     * Получение продуктов в заданном ценовом диапазоне
     */
    public List<ProductDTO> getProductsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        if (minPrice == null && maxPrice == null) {
            return getAllProducts();
        }

        minPrice = minPrice != null ? minPrice : BigDecimal.ZERO;
        maxPrice = maxPrice != null ? maxPrice : BigDecimal.valueOf(Double.MAX_VALUE);

        return productRepository.findByPriceBetweenOrderByPriceAsc(minPrice, maxPrice).stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    /**
     * Получение продуктов по категории
     */
    public List<ProductDTO> getProductsByCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + categoryId));

        return productRepository.findByCategoryOrderByCreatedAtDesc(category).stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    /**
     * Поиск продуктов по имени
     */
    public List<ProductDTO> searchProductsByName(String name) {
        if (StringUtils.isBlank(name)) {
            return Collections.emptyList();
        }

        return productRepository.findByNameContainingIgnoreCaseOrderByPopularityDesc(name).stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    /**
     * Удаление продукта
     */
    @Transactional
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        // Проверка связанных заказов или других зависимостей
        if (hasActiveOrders(product)) {
            throw new BusinessException("Cannot delete product with active orders");
        }

        productRepository.delete(product);
    }


    private boolean hasActiveOrders(Product product) {
        // Реализация проверки активных заказов
        return false; // Заглушка, требуется реальная имплементация
    }

    public class ResourceNotFoundException extends RuntimeException {
        public ResourceNotFoundException(String message) {
            super(message);
        }
    }

    public class BusinessException extends RuntimeException {
        public BusinessException(String message) {
            super(message);
        }
    }
}