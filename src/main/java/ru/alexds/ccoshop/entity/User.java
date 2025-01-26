package ru.alexds.ccoshop.entity;
/**
 * Пакет для сущностей приложения.
 */


import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Класс User представляет собой сущность пользователя в системе.
 * Этот класс аннотирован как JPA-сущность и отображается в таблицу "users" в базе данных.
 * Для удобства использования Lombok генерирует геттеры, сеттеры, equals, hashCode и toString методы,
 * а также пустой и полностью параметризованный конструкторы.
 */
@Entity
@Table(name = "users")
@Data // Генерирует геттеры, сеттеры, equals, hashCode и toString
@NoArgsConstructor // Генерирует пустой конструктор
@AllArgsConstructor // Генерирует конструктор с аргументами
public class User {

    /**
     * Уникальный идентификатор пользователя.
     * Аннотация @Id указывает на то, что это поле является первичным ключом.
     * Аннотация @GeneratedValue стратегией GenerationType.IDENTITY позволяет автоматически генерировать значения.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Имя пользователя. Поле не может быть пустым.
     *
     */

    private String firstName;

    /**
     * Фамилия пользователя. Поле не может быть пустым.
     *
     */

    private String lastName;

    /**
     * Электронная почта пользователя. Поле не может быть пустым и должно быть валидным адресом электронной почты.
     * Аннотация @Email проверяет корректность формата электронной почты.
     * Аннотация @Column(unique = true) гарантирует уникальность значений в базе данных.
     */
    @NotEmpty(message = "Email cannot be empty")
    @Email(message = "Email should be valid")
    @Column(unique = true)
    private String email;

    /**
     * Пароль пользователя. Поле не может быть пустым.
     * Аннотация @NotEmpty проверяет, чтобы строка была не пустой.
     */
    @NotEmpty(message = "Password cannot be empty")
    private String password;

    /**
     * Дата создания пользователя. Устанавливается автоматически при создании записи в базе данных.
     */
    private LocalDateTime createdAt;

    /**
     * Дата последнего обновления профиля пользователя. Устанавливается автоматически при обновлении записи в базе данных.
     */
    private LocalDateTime updatedAt;

    /**
     * Роль пользователя. Хранится в виде строки (например, "USER" или "ADMIN").
     * Аннотация @Enumerated(EnumType.STRING) указывает на хранение перечисления в виде строки.
     * Аннотация @Column(nullable = false) указывает на обязательность этого поля.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    /**
     * Список заказов пользователя. Отношение один ко многим с сущностью Order.
     * Аннотация @OneToMany указывает на отношение один ко многим.
     * Аннотация @JoinColumn(mappedBy = "user") указывает на обратную связь.
     * Аннотация @Cascade(CascadeType.ALL) указывает на каскадное удаление всех зависимых объектов.
     * Аннотация @OrphanRemoval(true) указывает на удаление "сиротских" записей.
     */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Order> orders = new ArrayList<>();

    /**
     * Состояние учетной записи пользователя (активирована или деактивирована).
     * По умолчанию учетная запись активна.
     */
    private boolean active = true;

    /**
     * Конструктор, принимающий только идентификатор пользователя.
     *
     * @param id Идентификатор пользователя.
     */
    public User(Long id) {
        this.id = id;
    }

    /**
     * Метод вызывается перед сохранением новой записи в базе данных.
     * Устанавливает текущее время в поле createdAt.
     */
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    /**
     * Метод вызывается перед обновлением существующей записи в базе данных.
     * Устанавливает текущее время в поле updatedAt.
     */
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Конструктор с основными параметрами для создания нового пользователя.
     *
     * @param firstName Имя пользователя.
     * @param lastName  Фамилия пользователя.
     * @param email     Электронная почта пользователя.
     * @param password  Пароль пользователя.
     * @param role      Роль пользователя.
     */
    public User(String firstName, String lastName, String email, String password, Role role) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.role = role;
        this.createdAt = LocalDateTime.now(); // Установка даты создания
    }

    /**
     * Метод добавляет новый заказ в список заказов пользователя и устанавливает обратную связь.
     *
     * @param order Заказ, который нужно добавить.
     */
    public void addOrder(Order order) {
        orders.add(order);
        order.setUser(this);
    }
}