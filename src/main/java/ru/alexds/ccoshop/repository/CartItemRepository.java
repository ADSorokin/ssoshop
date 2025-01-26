/**
 * Пакет для репозиториев приложения.
 */
package ru.alexds.ccoshop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.alexds.ccoshop.entity.CartItem;
import java.util.List;
import java.util.Optional;

/**
 * Интерфейс CartItemRepository представляет собой репозиторий для работы с сущностью CartItem в базе данных.
 * Этот интерфейс расширяет JpaRepository, который предоставляет основные CRUD операции и другие полезные методы.
 * Для удобства использования Spring Data JPA генерирует реализацию этого интерфейса автоматически.
 */
@Repository // Аннотация для обозначения класса как репозитория
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    /**
     * Метод для поиска всех элементов корзины, принадлежащих конкретному пользователю.
     *
     * @param userId Идентификатор пользователя, чьи элементы корзины необходимо найти.
     * @return Список всех элементов корзины, принадлежащих указанному пользователю.
     */
    List<CartItem> findByUserId(Long userId);

    /**
     * Метод для поиска элемента корзины по идентификатору пользователя и идентификатору продукта.
     *
     * @param userId   Идентификатор пользователя.
     * @param productId Идентификатор продукта.
     * @return Объект Optional, содержащий найденный элемент корзины, если он существует,
     *         или пустой Optional, если элемент не найден.
     */
    Optional<CartItem> findByUserIdAndProductId(Long userId, Long productId);

    /**
     * Метод для удаления всех элементов корзины, принадлежащих конкретному пользователю.
     *
     * @param userId Идентификатор пользователя, чьи элементы корзины необходимо удалить.
     */
    void deleteByUserId(Long userId);

    /**
     * Метод для проверки существования элемента корзины по идентификатору пользователя и идентификатору продукта.
     *
     * @param userId   Идентификатор пользователя.
     * @param productId Идентификатор продукта.
     * @return true, если элемент корзины с указанным пользователем и продуктом существует, иначе false.
     */
    boolean existsByUserIdAndProductId(Long userId, Long productId);

    /**
     * Метод для подсчета количества элементов корзины, принадлежащих конкретному пользователю.
     *
     * @param userId Идентификатор пользователя.
     * @return Количество элементов корзины, принадлежащих указанному пользователю.
     */
    int countByUserId(Long userId);
}