package ru.alexds.ccoshop.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

/**
 * Сервис для управления категориями товаров.
 * Обеспечивает API для создания, получения, обновления и удаления категорий,
 * а также для добавления и удаления продуктов из категорий.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CategoryService {
    private final CategoryRepository categoryRepository; // Репозиторий для работы с категориями
    private final ProductRepository productRepository; // Репозиторий для работы с продуктами

    /**
     * Создает новую категорию.
     *
     * @param categoryDTO DTO объект с информацией о создаваемой категории (имя)
     * @return DTO объект созданной категории
     */
    public CategoryDTO createCategory(CategoryDTO categoryDTO) {
        log.debug("Request to create a new category: {}", categoryDTO);
        Category category = Category.builder()
                .name(categoryDTO.getName())
                .products(new ArrayList<>()) // Новая категория не содержит продуктов по умолчанию
                .build();

        Category savedCategory = categoryRepository.save(category); // Сохраняем новую категорию в базе данных
        log.info("Successfully created category with ID: {}", savedCategory.getId());
        return convertToDTO(savedCategory); // Преобразуем сохраненную категорию в DTO и возвращаем
    }

    /**
     * Получает категорию по ее идентификатору.
     *
     * @param id Идентификатор категории, которую необходимо получить
     * @return DTO объект найденной категории
     * @throws EntityNotFoundException если категория с указанным идентификатором не найдена
     */
    public CategoryDTO getCategoryById(Long id) {
        log.debug("Request to get category by ID: {}", id);
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category not found with id: " + id)); // Проверка на наличие категории в БД
        log.info("Successfully retrieved category with ID: {}", id);
        return convertToDTO(category); // Преобразуем найденную категорию в DTO и возвращаем
    }

    /**
     * Получает все категории.
     *
     * @return Список всех категорий в формате DTO
     */
    public List<CategoryDTO> getAllCategories() {
        log.debug("Request to get all categories");
        return categoryRepository.findAll().stream()
                .map(this::convertToDTO) // Преобразуем каждую категорию в DTO
                .collect(Collectors.toList()); // Возвращаем список DTO
    }

    /**
     * Обновляет существующую категорию и добавляет продукты в нее.
     *
     * @param id         Идентификатор категории, которую необходимо обновить
     * @param categoryDTO DTO объект с новыми данными о категории (например, имя, список идентификаторов продуктов)
     * @return DTO объект обновленной категории
     * @throws EntityNotFoundException если категория или один из продуктов не найден
     * @throws IllegalArgumentException если продукт уже присутствует в категории
     */
    public CategoryDTO updateCategory(Long id, CategoryDTO categoryDTO) {
        log.debug("Request to update category with ID: {} and data: {}", id, categoryDTO);

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category not found with id: " + id)); // Проверка наличия категории

        // Обновляем имя категории
        category.setName(categoryDTO.getName());

        // Добавление продуктов в категорию
        if (categoryDTO.getProductIds() != null) {
            for (Long productId : categoryDTO.getProductIds()) {
                Product product = productRepository.findById(productId)
                        .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + productId)); // Проверка наличия продукта
                category.getProducts().add(product); // Привязываем продукт к категории
            }
        }

        Category updatedCategory = categoryRepository.save(category); // Сохраняем изменения в категории
        log.info("Successfully updated category with ID: {}", id);
        return convertToDTO(updatedCategory); // Преобразуем обновленную категорию в DTO и возвращаем
    }

    /**
     * Удаляет категорию по ее идентификатору.
     *
     * @param id Идентификатор категории, которую необходимо удалить
     * @throws EntityNotFoundException если категория с указанным идентификатором не найдена
     */
    public void deleteCategory(Long id) {
        log.debug("Request to delete category with ID: {}", id);
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category not found with id: " + id)); // Проверка наличия категории

        categoryRepository.delete(category); // Удаляем категорию из базы данных
        log.info("Successfully deleted category with ID: {}", id);
    }

    /**
     * Добавляет продукт в указанную категорию.
     *
     * @param categoryId Идентификатор категории, в которую необходимо добавить продукт
     * @param productId  Идентификатор продукта, который необходимо добавить в категорию
     * @return DTO объект обновленной категории
     * @throws EntityNotFoundException если категория или продукт не найдены
     * @throws IllegalArgumentException если продукт уже присутствует в категории
     */
    public CategoryDTO addProductToCategory(Long categoryId, Long productId) {
        log.debug("Request to add product with ID: {} to category with ID: {}", productId, categoryId);

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
        log.info("Successfully added product with ID: {} to category with ID: {}", productId, categoryId);

        // Возвращаем обновленную категорию в виде DTO
        return convertToDTO(category);
    }

    /**
     * Удаляет продукт из указанной категории.
     *
     * @param categoryId Идентификатор категории, из которой необходимо удалить продукт
     * @param productId  Идентификатор продукта, который необходимо удалить из категории
     * @return DTO объект обновленной категории
     * @throws EntityNotFoundException если категория или продукт не найдены
     * @throws IllegalArgumentException если продукт не принадлежит категории
     */
    public CategoryDTO removeProductFromCategory(Long categoryId, Long productId) {
        log.debug("Request to remove product with ID: {} from category with ID: {}", productId, categoryId);

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
        product.setCategory(null); // Убираем связь продукта с категорией

        // Сохраняем изменения
        categoryRepository.save(category);
        log.info("Successfully removed product with ID: {} from category with ID: {}", productId, categoryId);

        // Возвращаем обновленную категорию в виде DTO
        return convertToDTO(category);
    }

    /**
     * Вспомогательный метод для преобразования объекта Category в DTO.
     *
     * @param category Объект категории, который необходимо преобразовать
     * @return DTO объект категории
     */
    private CategoryDTO convertToDTO(Category category) {
        log.debug("Converting category with ID: {} to DTO", category.getId());
        return CategoryDTO.builder()
                .id(category.getId())
                .name(category.getName())
                .productIds(category.getProducts().stream()
                        .map(Product::getId)
                        .collect(Collectors.toList())) // Преобразуем список продуктов в список идентификаторов
                .build();
    }
}
