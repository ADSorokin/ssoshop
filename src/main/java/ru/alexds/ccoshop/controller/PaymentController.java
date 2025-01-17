package ru.alexds.ccoshop.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.alexds.ccoshop.dto.PaymentRequestDTO;
import ru.alexds.ccoshop.dto.PaymentResponseDTO;
import ru.alexds.ccoshop.entity.Payment;
import ru.alexds.ccoshop.service.PaymentService;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;

    /**
     * Метод для проверки оплаты
     */
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

        // Логика оплаты
        boolean paymentSuccess = mockPaymentProcessor(request.getAmount());
        Payment payment = paymentService.savePayment(
                request.getOrderId(),
                request.getAmount(),
                request.getCurrency(),
                paymentSuccess ? "SUCCESS" : "FAILED",
                paymentSuccess ? "Payment was successful" : "Payment failed due to insufficient funds or other error"
        );

        return ResponseEntity.ok(new PaymentResponseDTO(
                request.getOrderId(),
                payment.getStatus(),
                payment.getMessage(),
                payment.getAmount()
        ));
    }


//    @PostMapping("/process")
//    public ResponseEntity<PaymentResponseDTO> processPayment(@RequestBody @Valid PaymentRequestDTO request) {
//        // Проверка суммы (тестовая проверка - не входит в реальную логику)
//        if (request.getAmount() == null || request.getAmount() <= 0) {
//            return ResponseEntity.badRequest().body(new PaymentResponseDTO(
//                    request.getOrderId(),
//                    "FAILED",
//                    "Payment amount must be greater than zero",
//                    null
//            ));
//        }
//
//        // Логика оплаты
//        if (mockPaymentProcessor(request.getAmount())) {
//            // Успешная оплата
//            PaymentResponseDTO response = new PaymentResponseDTO(
//                    request.getOrderId(),
//                    "SUCCESS",
//                    "Payment was successful",
//                    request.getAmount()
//            );
//            return ResponseEntity.ok(response);
//        } else {
//            // Неудачная оплата
//            PaymentResponseDTO response = new PaymentResponseDTO(
//                    request.getOrderId(),
//                    "FAILED",
//                    "Payment failed due to insufficient funds or other error",
//                    request.getAmount()
//            );
//            return ResponseEntity.status(HttpStatus.PAYMENT_REQUIRED).body(response);
//        }
//    }

    /**
     * Эмуляция процессора (рандомный успех/ошибка для тестов)
     */
    private boolean mockPaymentProcessor(Double amount) {
        // 80% вероятность успеха, 20% - неудачи
        return Math.random() > 0.2;
    }
}


