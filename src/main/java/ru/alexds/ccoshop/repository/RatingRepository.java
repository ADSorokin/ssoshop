/**
 * Пакет для репозиториев приложения.
 */
package ru.alexds.ccoshop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.alexds.ccoshop.entity.Rating;
import java.util.List;

/**
 * Интерфейс RatingRepository представляет собой репозиторий для работы с сущностью Rating в базе данных.
 * Этот интерфейс расширяет JpaRepository, который предоставляет основные CRUD операции и другие полезные методы.
 * Для удобства использования Spring Data JPA генерирует реализацию этого интерфейса автоматически.
 */
public interface RatingRepository extends JpaRepository<Rating, Long> {

    /**
     * Метод для поиска всех рейтингов, выставленных определенным пользователем.
     *
     * @param userId Идентификатор пользователя, чьи рейтинги необходимо найти.
     * @return Список рейтингов, выставленных указанным пользователем.
     */
    List<Rating> findByUserId(Long userId); // Найти все рейтинги, выставленные пользователем

    /**
     * Метод для поиска всех рейтингов для определенного товара.
     *
     * @param itemId Идентификатор товара, для которого необходимо найти рейтинги.
     * @return Список рейтингов для указанного товара.
     */
    List<Rating> findByItemId(Long itemId); // Найти все рейтинги для определенного товара

    /**
     * Метод для получения максимального значения itemId из таблицы рейтингов.
     *
     * @return Максимальное значение itemId или null, если таблица пустая.
     */
    @Query("SELECT MAX(r.itemId) FROM Rating r")
    Long findMaxItemId(); // Метод для получения максимального itemId
}
