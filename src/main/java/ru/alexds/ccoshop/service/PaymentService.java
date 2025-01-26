package ru.alexds.ccoshop.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.alexds.ccoshop.entity.Currency;
import ru.alexds.ccoshop.entity.Payment;
import ru.alexds.ccoshop.repository.PaymentRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Сервис для управления платежами.
 * Обеспечивает API для сохранения, получения всех платежей и последнего платежа для заказа.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository; // Репозиторий для работы с платежами

    /**
     * Сохраняет новую транзакцию в базу данных.
     *
     * @param orderId  Идентификатор заказа, к которому относится платеж
     * @param amount   Сумма платежа
     * @param currency Валюта платежа
     * @param status   Статус платежа (например, "SUCCESS", "FAILED")
     * @param message  Сообщение о статусе платежа
     * @return Сохраненный объект платежа
     */
    public Payment savePayment(Long orderId, Double amount, Currency currency, String status, String message) {
        log.debug("Request to save a new payment for order ID: {}, amount: {}, currency: {}, status: {}, message: {}", orderId, amount, currency, status, message);

        // Создаем новый объект платежа с указанными параметрами
        Payment payment = Payment.builder()
                .orderId(orderId)
                .amount(amount)
                .currency(currency)
                .status(status)
                .message(message)
                .createdAt(LocalDateTime.now()) // Устанавливаем текущее время создания платежа
                .build();

        // Сохраняем платеж в базе данных
        Payment savedPayment = paymentRepository.save(payment);
        log.info("Successfully saved payment with ID: {} for order ID: {}", savedPayment.getId(), orderId);

        return savedPayment;
    }

    /**
     * Получает все платежи для указанного заказа.
     *
     * @param orderId Идентификатор заказа, для которого необходимо получить платежи
     * @return Список всех платежей, связанных с данным заказом
     */
    public List<Payment> getPaymentsByOrderId(Long orderId) {
        log.debug("Request to get all payments for order ID: {}", orderId);

        // Получаем все платежи по указанному заказу из репозитория
        List<Payment> payments = paymentRepository.findAllByOrderId(orderId);

        log.info("Retrieved {} payments for order ID: {}", payments.size(), orderId);
        return payments;
    }

    /**
     * Получает последний платеж для указанного заказа.
     *
     * @param orderId Идентификатор заказа, для которого необходимо получить последний платеж
     * @return Опциональный объект последнего платежа, если он найден
     */
    public Optional<Payment> getLastPaymentByOrderId(Long orderId) {
        log.debug("Request to get the last payment for order ID: {}", orderId);

        // Получаем последний платеж по указанному заказу из репозитория
        Optional<Payment> lastPayment = paymentRepository.findTopByOrderIdOrderByCreatedAtDesc(orderId);

        if (lastPayment.isPresent()) {
            log.info("Retrieved last payment with ID: {} for order ID: {}", lastPayment.get().getId(), orderId);
        } else {
            log.warn("No payments found for order ID: {}", orderId);
        }

        return lastPayment;
    }
}