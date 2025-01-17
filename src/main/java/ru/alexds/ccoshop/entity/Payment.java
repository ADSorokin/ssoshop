package ru.alexds.ccoshop.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long orderId; // ID заказа

    @Column(nullable = false)
    private Double amount; // Сумма оплаты

    @Column(nullable = false)
    private String currency; // Валюта

    @Column(nullable = false)
    private String status; // Статус оплаты (SUCCESS / FAILED)

    @Column(nullable = false)
    private String message; // Сообщение о результате оплаты

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt; // Время создания платежа

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
