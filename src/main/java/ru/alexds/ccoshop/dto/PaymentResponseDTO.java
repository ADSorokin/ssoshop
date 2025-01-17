package ru.alexds.ccoshop.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponseDTO {
    private Long orderId;        // ID заказа
    private String status;       // Статус оплаты ("SUCCESS" или "FAILED")
    private String message;      // Сообщение о результате оплаты
    private Double amount;       // Сумма оплаты
}

