/**
 * Пакет для DTO (Data Transfer Objects) приложения.
 */
package ru.alexds.ccoshop.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.alexds.ccoshop.entity.Currency;

/**
 * Класс PaymentRequestDTO представляет собой DTO (Data Transfer Object), используемый для передачи данных о запросе на оплату заказа между слоями приложения.
 * Этот класс используется для упрощения и защиты передачи данных, избегая передачи ненужных или чувствительных полей.
 * Для удобства использования Lombok генерирует геттеры, сеттеры, equals, hashCode и toString методы,
 * а также пустой, полностью параметризованный конструкторы.
 */
@Data // Генерирует геттеры, сеттеры, equals, hashCode и toString
@NoArgsConstructor // Генерирует пустой конструктор
@AllArgsConstructor // Генерирует конструктор с аргументами
public class PaymentRequestDTO {

    /**
     * Идентификатор заказа, который оплачивается.
     * Это уникальный идентификатор заказа, для которого необходимо выполнить операцию оплаты.
     */
    private Long orderId;        // ID заказа, который оплачивается

    /**
     * Сумма оплаты.
     * Это числовое значение, представляющее сумму денег, которая должна быть оплачена за заказ.
     */
    private Double amount;       // Сумма оплаты

    /**
     * Валюта оплаты.
     * Это перечисление, представляющее валюту, в которой будет выполнена оплата (например, "USD", "EUR").
     * Поле currency позволяет указать валюту, в которой производится оплата.
     */
    private Currency currency;   // Валюта (например, "USD", "EUR")
}