package ru.alexds.ccoshop.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequestDTO {
    private Long orderId;        // ID заказа, который оплачивается
    private Double amount;       // Сумма оплаты
    private String currency;     // Валюта (например, "USD", "EUR")
}
