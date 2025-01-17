package ru.alexds.ccoshop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.alexds.ccoshop.entity.Payment;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    // Получить все платежи для конкретного заказа
    List<Payment> findAllByOrderId(Long orderId);

    // Получить последний платеж для конкретного заказа
    Optional<Payment> findTopByOrderIdOrderByCreatedAtDesc(Long orderId);
}
