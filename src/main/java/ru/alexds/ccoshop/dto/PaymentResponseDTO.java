/**
 * Пакет для DTO (Data Transfer Objects) приложения.
 */
package ru.alexds.ccoshop.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Класс PaymentResponseDTO представляет собой DTO (Data Transfer Object), используемый для передачи данных о результате оплаты заказа между слоями приложения.
 * Этот класс используется для упрощения и защиты передачи данных, избегая передачи ненужных или чувствительных полей.
 * Для удобства использования Lombok генерирует геттеры, сеттеры, equals, hashCode и toString методы,
 * а также пустой, полностью параметризованный конструкторы.
 */
@Data // Генерирует геттеры, сеттеры, equals, hashCode и toString
@NoArgsConstructor // Генерирует пустой конструктор
@AllArgsConstructor // Генерирует конструктор с аргументами
public class PaymentResponseDTO {

    /**
     * Идентификатор заказа.
     * Это уникальный идентификатор заказа, который был оплачен.
     */
    private Long orderId;        // ID заказа

    /**
     * Статус оплаты.
     * Возможные значения: "SUCCESS" или "FAILED".
     * Это строковое значение, представляющее результат выполнения операции оплаты.
     */
    private String status;       // Статус оплаты ("SUCCESS" или "FAILED")

    /**
     * Сообщение о результате оплаты.
     * Это строковое значение, содержащее дополнительную информацию о результате выполнения операции оплаты.
     * Например, может содержать описание ошибки в случае неудачной оплаты или подтверждение успешной оплаты.
     */
    private String message;      // Сообщение о результате оплаты

    /**
     * Сумма оплаты.
     * Это числовое значение, представляющее сумму денег, которая была оплачена за заказ.
     */
    private Double amount;       // Сумма оплаты
}