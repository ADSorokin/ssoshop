/**
 * Пакет для репозиториев приложения.
 */
package ru.alexds.ccoshop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.alexds.ccoshop.entity.Payment;
import java.util.List;
import java.util.Optional;

/**
 * Интерфейс PaymentRepository представляет собой репозиторий для работы с сущностью Payment в базе данных.
 * Этот интерфейс расширяет JpaRepository, который предоставляет основные CRUD операции и другие полезные методы.
 * Для удобства использования Spring Data JPA генерирует реализацию этого интерфейса автоматически.
 */
@Repository // Аннотация для обозначения класса как репозитория
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    /**
     * Метод для получения всех платежей, связанных с конкретным заказом.
     *
     * @param orderId Идентификатор заказа, для которого необходимо найти все платежи.
     * @return Список всех платежей, связанных с указанным заказом.
     */
    List<Payment> findAllByOrderId(Long orderId);

    /**
     * Метод для получения последнего платежа для конкретного заказа.
     * Платежи сортируются по дате создания в порядке убывания, и возвращается первый из них.
     *
     * @param orderId Идентификатор заказа, для которого необходимо найти последний платеж.
     * @return Объект Optional, содержащий последний платеж для указанного заказа, если он существует,
     *         или пустой Optional, если платежи отсутствуют.
     */
    Optional<Payment> findTopByOrderIdOrderByCreatedAtDesc(Long orderId);
}
