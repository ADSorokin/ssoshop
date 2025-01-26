
/**
 * Пакет для DTO (Data Transfer Objects) приложения.
 */
package ru.alexds.ccoshop.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.alexds.ccoshop.entity.Role;
import ru.alexds.ccoshop.entity.User;

import java.time.LocalDateTime;

/**
 * Класс UserDTO представляет собой DTO (Data Transfer Object), используемый для передачи данных о пользователе между слоями приложения.
 * Этот класс используется для упрощения и защиты передачи данных, избегая передачи ненужных или чувствительных полей.
 * Для удобства использования Lombok генерирует геттеры, сеттеры, equals, hashCode и toString методы,
 * а также пустой, полностью параметризованный конструкторы.
 */
@Data // Генерирует геттеры, сеттеры, equals, hashCode и toString
@NoArgsConstructor // Генерирует пустой конструктор
@AllArgsConstructor // Генерирует конструктор с аргументами
public class UserDTO {

    /**
     * Уникальный идентификатор пользователя.
     */
    private Long id;

    /**
     * Электронная почта пользователя.
     */
    private String email;

    /**
     * Роль пользователя. Представляет уровень доступа и привилегии пользователя в системе.
     */

    private String firstName;

    /**
     * Фамилия пользователя. Поле не может быть пустым.
     *
     */

    private String lastName;
    private Role role;

    /**
     * Состояние учетной записи пользователя (активирована или деактивирована).
     */
    private boolean active;

    /**
     * Пароль пользователя.  Обычно не передается через DTO для безопасности.
     * Оставлено здесь для примера.
     */
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
     * Конструктор для создания объекта UserDTO на основе существующего объекта User.
     *
     * @param user Объект User, из которого будут извлечены данные.
     */
    public UserDTO(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.firstName= user.getFirstName();
        this.lastName = user.getLastName();
        this.role = user.getRole();
        this.active = user.isActive();
        this.createdAt=user.getCreatedAt();
        this.updatedAt=user.getUpdatedAt();
        // Пароль не копируется, так как он является чувствительной информацией
    }



}
