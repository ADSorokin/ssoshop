package ru.alexds.ccoshop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.alexds.ccoshop.entity.Order;
import ru.alexds.ccoshop.entity.Status;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    // Поиск по равенству
    List<Order> findByStatus(Status status);

    // Поиск по нескольким условиям
    List<Order> findByStatusAndTotalPriceGreaterThan(Status status, BigDecimal price);

    // Сортировка
    List<Order> findByUserIdOrderByOrderDateDesc(Long userId);

    // Лимит результатов
    List<Order> findFirst5ByUserId(Long userId);

    // Поиск по диапазону
    List<Order> findByOrderDateBetween(LocalDateTime start, LocalDateTime end);

//    // Игнорирование регистра
//    List<Order> findByStatusIgnoreCase(Status status);

    // Проверка существования
    boolean existsByUserIdAndStatus(Long userId, Status status);

    // Подсчёт
    long countByStatus(Status status);

    // Удаление
    void deleteByUserIdAndStatus(Long userId, Status status);

    List<Order> findByUserId(Long userId);

    List<Order> findCompletedOrdersByUserId(Long userId);

    //   long countByProductIdAndStatus(Long productId, Status status);

    //  boolean existsByUserIdAndProductIdAndStatus(Long userId, Long productId, Status status);
    boolean existsByUser_IdAndItems_Product_IdAndStatus(Long userId, Long productId, Status status);


    // Находит все завершенные заказы пользователя
    List<Order> findByUserIdAndStatus(Long userId, Status status);


    // Подсчитывает количество заказов пользователя в определенном статусе
    long countByUserIdAndStatus(Long userId, Status status);


}