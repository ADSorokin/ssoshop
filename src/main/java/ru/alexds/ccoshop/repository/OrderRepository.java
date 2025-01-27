/**
 * Пакет для репозиториев приложения.
 */
package ru.alexds.ccoshop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.alexds.ccoshop.entity.Order;
import ru.alexds.ccoshop.entity.Status;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Интерфейс OrderRepository представляет собой репозиторий для работы с сущностью Order в базе данных.
 * Этот интерфейс расширяет JpaRepository, который предоставляет основные CRUD операции и другие полезные методы.
 * Для удобства использования Spring Data JPA генерирует реализацию этого интерфейса автоматически.
 */
public interface OrderRepository extends JpaRepository<Order, Long> {

    /**
     * Метод для поиска всех заказов с определенным статусом.
     *
     * @param status Статус заказа, по которому необходимо найти все заказы.
     * @return Список всех заказов с указанным статусом.
     */
    List<Order> findByStatus(Status status);

    /**
     * Метод для поиска всех заказов с определенным статусом и общей стоимостью, превышающей заданное значение.
     *
     * @param status Статус заказа.
     * @param price  Минимальная стоимость заказа.
     * @return Список всех заказов с указанным статусом и общей стоимостью, превышающей заданное значение.
     */
    List<Order> findByStatusAndTotalPriceGreaterThan(Status status, BigDecimal price);

    /**
     * Метод для поиска всех заказов пользователя, отсортированных по дате создания в порядке убывания.
     *
     * @param userId Идентификатор пользователя.
     * @return Список всех заказов пользователя, отсортированных по дате создания в порядке убывания.
     */
    List<Order> findByUserIdOrderByOrderDateDesc(Long userId);

    /**
     * Метод для поиска первых пяти заказов пользователя.
     *
     * @param userId Идентификатор пользователя.
     * @return Список первых пяти заказов пользователя.
     */
    List<Order> findFirst5ByUserId(Long userId);

    /**
     * Метод для поиска всех заказов, созданных в заданном временном диапазоне.
     *
     * @param start Начальная дата временного диапазона.
     * @param end   Конечная дата временного диапазона.
     * @return Список всех заказов, созданных в заданном временном диапазоне.
     */
    List<Order> findByOrderDateBetween(LocalDateTime start, LocalDateTime end);

    /**
     * Метод для проверки существования заказа по идентификатору пользователя и статусу.
     *
     * @param userId Идентификатор пользователя.
     * @param status Статус заказа.
     * @return true, если существует хотя бы один заказ с указанным пользователем и статусом, иначе false.
     */
    boolean existsByUserIdAndStatus(Long userId, Status status);

    /**
     * Метод для подсчета количества заказов с определенным статусом.
     *
     * @param status Статус заказа.
     * @return Количество заказов с указанным статусом.
     */
    long countByStatus(Status status);

    /**
     * Метод для удаления всех заказов пользователя с определенным статусом.
     *
     * @param userId Идентификатор пользователя.
     * @param status Статус заказа.
     */
    void deleteByUserIdAndStatus(Long userId, Status status);

    /**
     * Метод для поиска всех заказов пользователя.
     *
     * @param userId Идентификатор пользователя.
     * @return Список всех заказов пользователя.
     */
    List<Order> findByUserId(Long userId);

    /**
     * Метод для поиска всех завершенных заказов пользователя.
     *
     * @param userId Идентификатор пользователя.
     * @return Список всех завершенных заказов пользователя.
     */
    List<Order> findCompletedOrdersByUserId(Long userId);

    /**
     * Метод для проверки существования заказа пользователя, содержащего определенный продукт и имеющего определенный статус.
     *
     * @param userId   Идентификатор пользователя.
     * @param productId Идентификатор продукта.
     * @param status    Статус заказа.
     * @return true, если существует хотя бы один заказ пользователя, содержащий указанный продукт и имеющий указанный статус, иначе false.
     */
    boolean existsByUser_IdAndItems_Product_IdAndStatus(Long userId, Long productId, Status status);

    /**
     * Метод для поиска всех заказов пользователя с определенным статусом.
     *
     * @param userId Идентификатор пользователя.
     * @param status Статус заказа.
     * @return Список всех заказов пользователя с указанным статусом.
     */
    List<Order> findByUserIdAndStatus(Long userId, Status status);

    long countByUserIdAndStatus(Long userId, Status completed);
}