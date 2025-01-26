/**
 * Пакет для репозиториев приложения.
 */
package ru.alexds.ccoshop.repository;

import aj.org.objectweb.asm.commons.Remapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.alexds.ccoshop.entity.Category;
import ru.alexds.ccoshop.entity.Product;
import java.math.BigDecimal;
import java.util.List;

/**
 * Интерфейс ProductRepository представляет собой репозиторий для работы с сущностью Product в базе данных.
 * Этот интерфейс расширяет JpaRepository, который предоставляет основные CRUD операции и другие полезные методы.
 * Для удобства использования Spring Data JPA генерирует реализацию этого интерфейса автоматически.
 */
@Repository // Аннотация для обозначения класса как репозитория
public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * Метод для поиска всех продуктов, принадлежащих определенной категории.
     *
     * @param categoryId Идентификатор категории, продукты которой необходимо найти.
     * @return Список продуктов, принадлежащих указанной категории.
     */
    List<Product> findByCategoryId(Long categoryId);

    /**
     * Метод для поиска всех продуктов, название которых содержит заданную строку (без учета регистра).
     *
     * @param name Строка, которую необходимо найти в названии продуктов.
     * @return Список продуктов, название которых содержит заданную строку.
     */
    List<Product> findByNameContainingIgnoreCase(String name);

    /**
     * Метод для поиска всех продуктов, цена которых находится в заданном диапазоне.
     *
     * @param minPrice Минимальная цена продукта.
     * @param maxPrice Максимальная цена продукта.
     * @return Список продуктов, цена которых находится в заданном диапазоне.
     */
    List<Product> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);

    /**
     * Метод для получения топ-10 самых популярных продуктов.
     *
     * @return Список из 10 самых популярных продуктов, отсортированных по убыванию популярности.
     */
    List<Product> findTop10ByOrderByPopularityDesc(); // Пример метода получения популярных продуктов

    /**
     * Метод для поиска всех продуктов, цена которых находится в заданном диапазоне, отсортированных по возрастанию цены.
     *
     * @param minPrice Минимальная цена продукта.
     * @param maxPrice Максимальная цена продукта.
     * @return Список продуктов, цена которых находится в заданном диапазоне, отсортированных по возрастанию цены.
     */
    List<Product> findByPriceBetweenOrderByPriceAsc(BigDecimal minPrice, BigDecimal maxPrice);

    /**
     * Метод для поиска всех продуктов, принадлежащих к определенной категории, отсортированных по дате создания в порядке убывания.
     *
     * @param category Категория, продукты которой необходимо найти.
     * @return Список продуктов, принадлежащих указанной категории, отсортированных по дате создания в порядке убывания.
     */
    List<Product> findByCategoryOrderByCreatedAtDesc(Category category);

    /**
     * Метод для поиска всех продуктов, название которых содержит заданную строку (без учета регистра), отсортированных по убыванию популярности.
     *
     * @param name Строка, которую необходимо найти в названии продуктов.
     * @return Список продуктов, название которых содержит заданную строку, отсортированных по убыванию популярности.
     */
    List<Product> findByNameContainingIgnoreCaseOrderByPopularityDesc(String name);

    /**
     * Метод для получения всех продуктов, отсортированных по убыванию популярности и дате создания в порядке убывания.
     *
     * @param of Объект PageRequest для пагинации результатов.
     * @return Список продуктов, отсортированных по убыванию популярности и дате создания в порядке убывания.
     */
    List<Product> findAllByOrderByPopularityDescCreatedAtDesc(PageRequest of);

    /**
     * Метод для поиска сущности по идентификатору (предположительно ошибка в типе возвращаемого значения).
     *
     * @param id Идентификатор сущности, которую необходимо найти.
     * @return Объект Remapper, представляющий найденную сущность (предположительно ошибка в типе возвращаемого значения).
     */
    Remapper findEntityById(Long id);
}