package ru.alexds.ccoshop.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.alexds.ccoshop.entity.Currency;
import ru.alexds.ccoshop.entity.Payment;
import ru.alexds.ccoshop.entity.Status;
import ru.alexds.ccoshop.repository.OrderRepository;
import ru.alexds.ccoshop.repository.PaymentRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;



    /**
     * Запись транзакции в базу данных
     */
    public Payment savePayment(Long orderId, Double amount, Currency currency, String status, String message) {
        Payment payment = Payment.builder()
                .orderId(orderId)
                .amount(amount)
                .currency(currency)
                .status(status)
                .message(message)
                .createdAt(LocalDateTime.now())
                .build();

        return paymentRepository.save(payment);
    }

    /**
     * Получение всех платежей для определенного заказа
     */
    public List<Payment> getPaymentsByOrderId(Long orderId) {
        return paymentRepository.findAllByOrderId(orderId);
    }

    /**
     * Получение последнего платежа для заказа
     */
    public Optional<Payment> getLastPaymentByOrderId(Long orderId) {
        return paymentRepository.findTopByOrderIdOrderByCreatedAtDesc(orderId);
    }
}