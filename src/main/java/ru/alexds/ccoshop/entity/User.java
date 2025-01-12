package ru.alexds.ccoshop.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Data // Генерирует геттеры, сеттеры, equals, hashCode и toString
@NoArgsConstructor // Генерирует пустой конструктор
@AllArgsConstructor // Генерирует конструктор с аргументами
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Уникальный идентификатор пользователя

    @NotEmpty(message = "First name cannot be empty")
    private String firstName; // Имя пользователя

    @NotEmpty(message = "Last name cannot be empty")
    private String lastName; // Фамилия пользователя

    @NotEmpty(message = "Email cannot be empty")
    @Email(message = "Email should be valid")
    @Column(unique = true)
    private String email; // Электронная почта пользователя

    @NotEmpty(message = "Password cannot be empty")
    private String password; // Пароль пользователя

    private LocalDateTime createdAt; // Дата создания пользователя
    private LocalDateTime updatedAt; // Дата последнего обновления профиля

    @Enumerated(EnumType.STRING) // Хранение роли в виде строки (например, "USER" или "ADMIN")
    @Column(nullable = false)
    private Role role; // Роль пользователя (enum)

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Order> orders = new ArrayList<>(); // Список заказов пользователя

    private boolean active = true; // Состояние учетной записи (активирована или деактивирована)

    public User(Long id) {
        this.id = id;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Конструктор с основными параметрами
    public User(String firstName, String lastName, String email, String password, Role role) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.role = role;
        this.createdAt = LocalDateTime.now(); // Установка даты создания
    }



    public void addOrder(Order order) {
        orders.add(order);
        order.setUser(this);
    }

}
