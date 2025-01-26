/**
 * Пакет для репозиториев приложения.
 */
package ru.alexds.ccoshop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.alexds.ccoshop.entity.OrderItem;
import java.util.List;

/**
 * Интерфейс OrderItemRepository представляет собой репозиторий для работы с сущностью OrderItem в базе данных.
 * Этот интерфейс расширяет JpaRepository, который предоставляет основные CRUD операции и другие полезные методы.
 * Для удобства использования Spring Data JPA генерирует реализацию этого интерфейса автоматически.
 */
@Repository // Аннотация для обозначения класса как репозитория
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    /**
     * Метод для поиска всех элементов заказа по идентификатору заказа.
     *
     * @param orderId Идентификатор заказа, для которого необходимо найти все элементы.
     * @return Список всех элементов заказа, связанных с указанным заказом.
     */
    List<OrderItem> findByOrderId(Long orderId);
}