package ru.alexds.ccoshop.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.alexds.ccoshop.dto.PaymentRequestDTO;
import ru.alexds.ccoshop.dto.PaymentResponseDTO;
import ru.alexds.ccoshop.entity.Order;
import ru.alexds.ccoshop.entity.Payment;
import ru.alexds.ccoshop.entity.Status;
import ru.alexds.ccoshop.repository.OrderRepository;
import ru.alexds.ccoshop.service.PaymentService;

import java.util.Optional;

@RestController

@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;
    private final OrderRepository orderRepository;
    private final OrderController orderService;

    /**
     * Метод для проверки оплаты
     */
    @Operation(summary = "Оплатить заказ")
    @PostMapping("/process")
    public ResponseEntity<PaymentResponseDTO> processPayment(@RequestBody @Valid PaymentRequestDTO request) {
        // Проверка суммы

        if (request.getAmount() == null || request.getAmount() <= 0) {
            Payment payment = paymentService.savePayment(request.getOrderId(), request.getAmount(), request.getCurrency(),
                    "FAILED", "Payment amount must be greater than zero");

            return ResponseEntity.badRequest().body(new PaymentResponseDTO(
                    request.getOrderId(),
                    payment.getStatus(),
                    payment.getMessage(),
                    payment.getAmount()
            ));
        }
        // Проверка ордера на возможность оплаты

        Optional<Order> order = orderRepository.findById(request.getOrderId());
        Status status = order.get().getStatus();
        if (!(status.equals(Status.NEW) || status.equals(Status.CANCELLED))) {
            Payment payment = paymentService.savePayment(request.getOrderId(), request.getAmount(), request.getCurrency(),
                    "FAILED", "Only new orders and canceled ones can be payed");

            return ResponseEntity.badRequest().body(new PaymentResponseDTO(
                    request.getOrderId(),
                    payment.getStatus(),
                    payment.getMessage(),
                    payment.getAmount()
            ));
        }


        // Логика оплаты
        boolean paymentSuccess = mockPaymentProcessor(request.getAmount());
        Payment payment = paymentService.savePayment(
                request.getOrderId(),
                request.getAmount(),
                request.getCurrency(),
                paymentSuccess ? "SUCCESS" : "FAILED",
                paymentSuccess ? "Payment was successful" : "Payment failed due to insufficient funds or other error"
        );
        if (paymentSuccess) {

            orderService.updateOrderStatus(request.getOrderId(), Status.PAID);
        }
        return ResponseEntity.ok(new PaymentResponseDTO(
                request.getOrderId(),
                payment.getStatus(),
                payment.getMessage(),
                payment.getAmount()
        ));
    }


    /**
     * Эмуляция процессора (рандомный успех/ошибка для тестов)
     */
    private boolean mockPaymentProcessor(Double amount) {
        // 80% вероятность успеха, 20% - неудачи
        return Math.random() > 0.2;
    }
}


