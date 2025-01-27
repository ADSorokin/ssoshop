//package ru.alexds.ccoshop.service;
//
//import jakarta.persistence.EntityNotFoundException;
//import lombok.RequiredArgsConstructor;
//import org.apache.commons.lang.StringUtils;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//import ru.alexds.ccoshop.dto.ProductDTO;
//import ru.alexds.ccoshop.entity.Category;
//import ru.alexds.ccoshop.entity.Product;
//import ru.alexds.ccoshop.repository.CategoryRepository;
//import ru.alexds.ccoshop.repository.ProductRepository;
//
//import java.math.BigDecimal;
//import java.util.Collections;
//import java.util.List;
//import java.util.Optional;
//import java.util.stream.Collectors;
//
//@Service
//@Transactional
//@RequiredArgsConstructor
//public class ProductService {
//
//    private final ProductRepository productRepository;
//    private final CategoryRepository categoryRepository;
//
//    private static final int POPULAR_PRODUCTS_LIMIT = 5;
//
//
//    public List<ProductDTO> getAllProducts() {
//        return productRepository.findAll().stream().map(this::convertToDTO).collect(Collectors.toList());
//    }
//
//    public Optional<ProductDTO> getProductById(Long id) {
//        return productRepository.findById(id).map(this::convertToDTO);
//    }
//
//    public Optional<Product> getProductEntityById(Long id) {
//        return productRepository.findById(id);
//
//    }
//
//    public ProductDTO createProduct(ProductDTO productDTO) {
//
//        // Проверяем существование категории
//        Category category = categoryRepository.findById(productDTO.getCategoryId())
//                .orElseThrow(() -> new EntityNotFoundException("Category not found"));
//
//        // Создаем новый продукт
//        Product product = Product.builder()
//                .name(productDTO.getName())
//                .description(productDTO.getDescription())
//                .price(productDTO.getPrice())
//                .stockQuantity(productDTO.getStockQuantity())
//                .imagePath(productDTO.getImagePath())
//                .category(category)
//                .popularity(productDTO.getPopularity())
//                .build();
//
//        // Сохраняем продукт
//        Product savedProduct = productRepository.save(product);
//
//        // Возвращаем DTO
//        return convertToDTO(savedProduct);
//
//
//    }
//
//    public ProductDTO updateProduct(Long id, ProductDTO productDTO) {
//        Product product = productRepository.findById(id)
//                .orElseThrow(() -> new EntityNotFoundException("Product not found"));
//
//        // Обновляем только переданные поля
//        if (productDTO.getName() != null) {
//            product.setName(productDTO.getName());
//        }
//        if (productDTO.getDescription() != null) {
//            product.setDescription(productDTO.getDescription());
//        }
//        if (productDTO.getPrice() != null) {
//            product.setPrice(productDTO.getPrice());
//        }
//        if (productDTO.getStockQuantity() != null) {
//            product.setStockQuantity(productDTO.getStockQuantity());
//        }
//        if (productDTO.getImagePath() != null) {
//            product.setImagePath(productDTO.getImagePath());
//        }
//        if (productDTO.getCategoryId() != null) {
//            Category category = categoryRepository.findById(productDTO.getCategoryId())
//                    .orElseThrow(() -> new EntityNotFoundException("Category not found"));
//            product.setCategory(category);
//        }
//
//        Product updatedProduct = productRepository.save(product);
//        return convertToDTO(updatedProduct);
//    }
//
//    public ProductDTO convertToDTO(Product product) {
//        return ProductDTO.builder()
//                .id(product.getId())
//                .name(product.getName())
//                .description(product.getDescription())
//                .price(product.getPrice())
//                .stockQuantity(product.getStockQuantity())
//                .imagePath(product.getImagePath())
//                .categoryId(product.getCategory().getId())
//                .popularity(product.getPopularity())
//                .createAt(product.getCreatedAt())
//                .updatedAt(product.getUpdatedAt())
//                .build();
//    }
//
//
//
//    private Product convertToEntity(ProductDTO productDTO) {
//        return Product.builder().id(productDTO.getId()).name(productDTO.getName()).description(productDTO.getDescription()).price(productDTO.getPrice()).stockQuantity(productDTO.getStockQuantity()).category(new Category(productDTO.getCategoryId())) // если необходимо
//                .build();
//    }
//
//    /**
//     * Получение популярных продуктов
//     */
//    public List<ProductDTO> getPopularProducts() {
//        return productRepository.findAllByOrderByPopularityDescCreatedAtDesc(PageRequest.of(0, POPULAR_PRODUCTS_LIMIT)).stream().map(this::convertToDTO).collect(Collectors.toList());
//    }
//
//    /**
//     * Получение продуктов в заданном ценовом диапазоне
//     */
//    public List<ProductDTO> getProductsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
//        if (minPrice == null && maxPrice == null) {
//            return getAllProducts();
//        }
//
//        minPrice = minPrice != null ? minPrice : BigDecimal.ZERO;
//        maxPrice = maxPrice != null ? maxPrice : BigDecimal.valueOf(Double.MAX_VALUE);
//
//        return productRepository.findByPriceBetweenOrderByPriceAsc(minPrice, maxPrice).stream().map(this::convertToDTO).collect(Collectors.toList());
//    }
//
//    /**
//     * Получение продуктов по категории
//     */
//    public List<ProductDTO> getProductsByCategory(Long categoryId) {
//        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + categoryId));
//
//        return productRepository.findByCategoryOrderByCreatedAtDesc(category).stream().map(this::convertToDTO).collect(Collectors.toList());
//    }
//
//    /**
//     * Поиск продуктов по имени
//     */
//    public List<ProductDTO> searchProductsByName(String name) {
//        if (StringUtils.isBlank(name)) {
//            return Collections.emptyList();
//        }
//
//        return productRepository.findByNameContainingIgnoreCaseOrderByPopularityDesc(name).stream().map(this::convertToDTO).collect(Collectors.toList());
//    }
//
//    /**
//     * Удаление продукта
//     */
//    @Transactional
//    public void deleteProduct(Long id) {
//        Product product = productRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
//
//        // Проверка связанных заказов или других зависимостей
//        if (hasActiveOrders(product)) {
//            throw new BusinessException("Cannot delete product with active orders");
//        }
//
//        productRepository.delete(product);
//    }
//
//
//    private boolean hasActiveOrders(Product product) {
//        // Реализация проверки активных заказов
//        return false; // Заглушка, требуется реальная имплементация
//    }
//
//    public class ResourceNotFoundException extends RuntimeException {
//        public ResourceNotFoundException(String message) {
//            super(message);
//        }
//    }
//
//    public class BusinessException extends RuntimeException {
//        public BusinessException(String message) {
//            super(message);
//        }
//    }
//}

package ru.alexds.ccoshop.service;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

/**
 * Сервис для управления продуктами.
 * Обеспечивает API для получения, создания, обновления и удаления продуктов,
 * а также для поиска популярных продуктов, продуктов в заданном ценовом диапазоне,
 * продуктов по категории и поиск продуктов по имени.
 */
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository; // Репозиторий для работы с продуктами
    private final CategoryRepository categoryRepository; // Репозиторий для работы с категориями

    private static final int POPULAR_PRODUCTS_LIMIT = 5; // Лимит для популярных продуктов

    /**
     * Получает все продукты.
     *
     * @return Список всех продуктов в формате DTO
     */
    public List<ProductDTO> getAllProducts() {
        log.debug("Request to get all products");
        return productRepository.findAll().stream()
                .map(this::convertToDTO) // Преобразуем каждый продукт в DTO
                .collect(Collectors.toList());
    }

    /**
     * Получает продукт по его идентификатору.
     *
     * @param id Идентификатор продукта, который необходимо получить
     * @return Опциональный DTO объект продукта, если он найден
     */
    public Optional<ProductDTO> getProductById(Long id) {
        log.debug("Request to get product by ID: {}", id);
        return productRepository.findById(id)
                .map(this::convertToDTO); // Преобразуем найденный продукт в DTO
    }

    /**
     * Получает сущность продукта по его идентификатору.
     *
     * @param id Идентификатор продукта, который необходимо получить
     * @return Опциональный объект продукта, если он найден
     */
    public Optional<Product> getProductEntityById(Long id) {
        log.debug("Request to get product entity by ID: {}", id);
        return productRepository.findById(id);
    }

    /**
     * Создает новый продукт.
     *
     * @param productDTO DTO объект с информацией о новом продукте (например, имя, описание, цена, количество на складе, путь к изображению, категория)
     * @return DTO объект созданного продукта
     * @throws EntityNotFoundException если категория не найдена
     */
    public ProductDTO createProduct(ProductDTO productDTO) {
        log.debug("Request to create a new product: {}", productDTO);

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
                .characteristics(productDTO.getCharacteristic())
                .popularity(productDTO.getPopularity())
                .build();

        // Сохраняем продукт в базе данных
        Product savedProduct = productRepository.save(product);
        log.info("Successfully created product with ID: {}", savedProduct.getId());

        // Возвращаем DTO нового продукта
        return convertToDTO(savedProduct);
    }

    /**
     * Обновляет информацию о существующем продукте.
     *
     * @param id         Идентификатор продукта, который необходимо обновить
     * @param productDTO DTO объект с новыми данными о продукте (например, имя, описание, цена, количество на складе, путь к изображению, категория)
     * @return DTO объект обновленного продукта
     * @throws EntityNotFoundException если продукт или категория не найдены
     */
    public ProductDTO updateProduct(Long id, ProductDTO productDTO) {
        log.debug("Request to update product with ID: {} and data: {}", id, productDTO);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found")); // Проверяем наличие продукта

        // Обновляем только переданные поля
        if (productDTO.getName() != null && !productDTO.getName().isBlank()) {
            product.setName(productDTO.getName());
            log.debug("Updated name for product ID: {}", id);
        }
        if (productDTO.getDescription() != null && !productDTO.getDescription().isBlank()) {
            product.setDescription(productDTO.getDescription());
            log.debug("Updated description for product ID: {}", id);
        }
        if (productDTO.getPrice() != null) {
            product.setPrice(productDTO.getPrice());
            log.debug("Updated price for product ID: {}", id);
        }
        if (productDTO.getStockQuantity() != null) {
            product.setStockQuantity(productDTO.getStockQuantity());
            log.debug("Updated stock quantity for product ID: {}", id);
        }
        if (productDTO.getImagePath() != null && !productDTO.getImagePath().isBlank()) {
            product.setImagePath(productDTO.getImagePath());
            log.debug("Updated image path for product ID: {}", id);
        }
        if (productDTO.getCategoryId() != null) {
            Category category = categoryRepository.findById(productDTO.getCategoryId())
                    .orElseThrow(() -> new EntityNotFoundException("Category not found"));
            product.setCategory(category);
            log.debug("Updated category for product ID: {}", id);
        }

        Product updatedProduct = productRepository.save(product); // Сохраняем обновленный продукт в базе данных
        log.info("Successfully updated product with ID: {}", id);
        return convertToDTO(updatedProduct); // Преобразуем обновленный продукт в DTO и возвращаем
    }

    /**
     * Вспомогательный метод для преобразования объекта Product в DTO.
     *
     * @param product Объект продукта, который необходимо преобразовать
     * @return DTO объект продукта
     */
    ProductDTO convertToDTO(Product product) {
        log.debug("Converting product with ID: {} to DTO", product.getId());

        return ProductDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stockQuantity(product.getStockQuantity())
                .imagePath(product.getImagePath())
                .categoryId(product.getCategory().getId())
                .popularity(product.getPopularity())
                .characteristic(product.getCharacteristics())
                .createAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }

    /**
     * Вспомогательный метод для преобразования объекта ProductDTO в сущность.
     *
     * @param productDTO DTO объект продукта, который необходимо преобразовать
     * @return Объект продукта
     */
    private Product convertToEntity(ProductDTO productDTO) {
        log.debug("Converting ProductDTO to Product entity");

        return Product.builder()
                .id(productDTO.getId())
                .name(productDTO.getName())
                .description(productDTO.getDescription())
                .price(productDTO.getPrice())
                .stockQuantity(productDTO.getStockQuantity())
                .category(new Category(productDTO.getCategoryId())) // Если необходимо, создаем новую категорию
                .build();
    }

    /**
     * Получает список популярных продуктов.
     *
     * @return Список популярных продуктов в формате DTO
     */
    public List<ProductDTO> getPopularProducts() {
        log.debug("Request to get popular products");

        return productRepository.findAllByOrderByPopularityDescCreatedAtDesc(PageRequest.of(0, POPULAR_PRODUCTS_LIMIT)).stream()
                .map(this::convertToDTO) // Преобразуем каждый продукт в DTO
                .collect(Collectors.toList());
    }

    /**
     * Получает продукты в заданном ценовом диапазоне.
     *
     * @param minPrice Минимальная цена продукта
     * @param maxPrice Максимальная цена продукта
     * @return Список продуктов в заданном ценовом диапазоне в формате DTO
     */
    public List<ProductDTO> getProductsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        log.debug("Request to get products in price range from {} to {}", minPrice, maxPrice);

        if (minPrice == null && maxPrice == null) {
            log.warn("No price range provided, returning all products");
            return getAllProducts(); // Возвращаем все продукты, если диапазон не указан
        }

        minPrice = minPrice != null ? minPrice : BigDecimal.ZERO; // Устанавливаем минимальную цену, если она не указана
        maxPrice = maxPrice != null ? maxPrice : BigDecimal.valueOf(Double.MAX_VALUE); // Устанавливаем максимальную цену, если она не указана

        return productRepository.findByPriceBetweenOrderByPriceAsc(minPrice, maxPrice).stream()
                .map(this::convertToDTO) // Преобразуем каждый продукт в DTO
                .collect(Collectors.toList());
    }

    /**
     * Получает продукты по категории.
     *
     * @param categoryId Идентификатор категории, продукты которой необходимо получить
     * @return Список продуктов в указанной категории в формате DTO
     * @throws ResourceNotFoundException если категория с указанным идентификатором не найдена
     */
    public List<ProductDTO> getProductsByCategory(Long categoryId) {
        log.debug("Request to get products by category ID: {}", categoryId);

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + categoryId)); // Проверяем наличие категории

        return productRepository.findByCategoryOrderByCreatedAtDesc(category).stream()
                .map(this::convertToDTO) // Преобразуем каждый продукт в DTO
                .collect(Collectors.toList());
    }

    /**
     * Поиск продуктов по имени.
     *
     * @param name Имя продукта для поиска
     * @return Список найденных продуктов в формате DTO
     */
    public List<ProductDTO> searchProductsByName(String name) {
        log.debug("Request to search products by name: {}", name);

        if (StringUtils.isBlank(name)) {
            log.warn("Name is blank, returning empty list");
            return Collections.emptyList(); // Возвращаем пустой список, если имя пустое
        }

        return productRepository.findByNameContainingIgnoreCaseOrderByPopularityDesc(name).stream()
                .map(this::convertToDTO) // Преобразуем каждый продукт в DTO
                .collect(Collectors.toList());
    }

    /**
     * Удаляет продукт по его идентификатору.
     *
     * @param id Идентификатор продукта, который необходимо удалить
     * @throws BusinessException если продукт имеет активные заказы
     * @throws ResourceNotFoundException если продукт не найден
     */
    @Transactional
    public void deleteProduct(Long id) {
        log.debug("Request to delete product with ID: {}", id);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        // Проверка связанных заказов или других зависимостей
        if (hasActiveOrders(product)) {
            throw new BusinessException("Cannot delete product with active orders");
        }

        productRepository.delete(product); // Удаляем продукт из базы данных
        log.info("Successfully deleted product with ID: {}", id);
    }

    /**
     * Проверяет наличие активных заказов для продукта.
     *
     * @param product Продукт, для которого необходимо проверить наличие активных заказов
     * @return true если продукт имеет активные заказы, false в противном случае
     */
    public boolean hasActiveOrders(Product product) {
        // Реализация проверки активных заказов
        // Например, можно проверить, есть ли заказы с данным продуктом и статусом, отличным от COMPLETED
        return false; // Заглушка, требуется реальная имплементация
    }

    /**
     * Исключение при отсутствии ресурса.
     */
    public static class ResourceNotFoundException extends RuntimeException {
        public ResourceNotFoundException(String message) {
            super(message);
        }
    }

    /**
     * Исключение для бизнес-логики.
     */
    public static class BusinessException extends RuntimeException {
        public BusinessException(String message) {
            super(message);
        }
    }
}