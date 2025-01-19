package ru.alexds.ccoshop.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.alexds.ccoshop.entity.Currency;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequestDTO {
    private Long orderId;        // ID заказа, который оплачивается
    private Double amount;       // Сумма оплаты
    private Currency currency;     // Валюта (например, "USD", "EUR")
}
