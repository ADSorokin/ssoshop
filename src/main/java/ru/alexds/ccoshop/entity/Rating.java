package ru.alexds.ccoshop.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ratings")
public class Rating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;            // Уникальный идентификатор записи

    @Column(name = "user_id", nullable = false)
    private Long userId;        // Идентификатор пользователя

    @Column(name = "item_id", nullable = false)
    private Long itemId;        // Идентификатор товара

    @Column(name = "rating", nullable = false)
    private Double rating;      // Рейтинг, выставленный пользователем

    @Column(name = "timestamp")
    private Long timestamp;     // Временная метка (в формате Unix времени)
}
