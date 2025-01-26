package ru.alexds.ccoshop.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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

/**
 * Контроллер для управления платежами.
 * Обеспечивает API для обработки платежей заказов.
 */
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Payment Controller", description = "API для работы с оплатой заказа")
public class PaymentController {
    private final PaymentService paymentService; // Сервис для управления платежами
    private final OrderRepository orderRepository; // Репозиторий для управления заказами
    private final OrderController orderService; // Сервис для управления статусом заказов

    /**
     * Обрабатывает процесс оплаты заказа.
     *
     * @param request DTO объект с информацией о платеже (идентификатор заказа, сумма, валюта)
     * @return HTTP-ответ с результатом обработки платежа в формате DTO
     * Если платеж не прошел проверку, возвращается статус 400 (Bad Request)
     * Если платеж успешен, возвращается статус 200 (OK)
     */
    @Operation(summary = "Оплатить заказ")
    @PostMapping("/process")
    public ResponseEntity<PaymentResponseDTO> processPayment(@RequestBody @Valid PaymentRequestDTO request) {
        log.debug("Request to process payment for order ID: {}", request.getOrderId());

        // Проверка суммы
        if (request.getAmount() == null || request.getAmount() <= 0) {
            log.error("Invalid amount for payment: {}", request.getAmount());
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
        Optional<Order> orderOptional = orderRepository.findById(request.getOrderId());
        if (!orderOptional.isPresent()) {
            log.error("Order not found with ID: {}", request.getOrderId());
            Payment payment = paymentService.savePayment(request.getOrderId(), request.getAmount(), request.getCurrency(),
                    "FAILED", "Order not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new PaymentResponseDTO(
                    request.getOrderId(),
                    payment.getStatus(),
                    payment.getMessage(),
                    payment.getAmount()
            ));
        }

        Order order = orderOptional.get();
        Status status = order.getStatus();
        if (!(status.equals(Status.NEW) || status.equals(Status.CANCELLED))) {
            log.error("Order with ID {} is in invalid status: {}", request.getOrderId(), status);
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
        String message = paymentSuccess ? "Payment was successful" : "Payment failed due to insufficient funds or other error";
        String statusResult = paymentSuccess ? "SUCCESS" : "FAILED";

        Payment payment = paymentService.savePayment(
                request.getOrderId(),
                request.getAmount(),
                request.getCurrency(),
                statusResult,
                message
        );

        if (paymentSuccess) {
            log.info("Order with ID {} successfully paid", request.getOrderId());
            orderService.updateOrderStatus(request.getOrderId(), Status.PAID);
        } else {
            log.error("Payment failed for order ID: {}", request.getOrderId());
        }

        return ResponseEntity.ok(new PaymentResponseDTO(
                request.getOrderId(),
                payment.getStatus(),
                payment.getMessage(),
                payment.getAmount()
        ));
    }

    /**
     * Эмулирует процесс обработки платежа (рандомный успех/ошибка для тестирования).
     *
     * @param amount Сумма платежа
     * @return true если платеж успешно обработан, false в случае ошибки
     */
    private boolean mockPaymentProcessor(Double amount) {
        log.debug("Mock processing payment of amount: {}", amount);
        // 80% вероятность успеха, 20% - неудачи
        return Math.random() > 0.2;
    }
}

