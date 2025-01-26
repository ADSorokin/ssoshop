
/**
 * Пакет для DTO (Data Transfer Objects) приложения.
 */
package ru.alexds.ccoshop.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.alexds.ccoshop.entity.Role;
import ru.alexds.ccoshop.entity.User;

/**
 * Класс UserCreateDTO представляет собой DTO (Data Transfer Object), используемый для передачи данных о пользователе между слоями приложения.
 * Этот класс используется для упрощения и защиты передачи данных, избегая передачи ненужных или чувствительных полей.
 * Для удобства использования Lombok генерирует геттеры, сеттеры, equals, hashCode и toString методы,
 * а также пустой, полностью параметризованный конструкторы.
 */
@Data // Генерирует геттеры, сеттеры, equals, hashCode и toString
@NoArgsConstructor // Генерирует пустой конструктор
@AllArgsConstructor // Генерирует конструктор с аргументами
public class UserCreateDTO {

//    /**
//     * Уникальный идентификатор пользователя.
//     */
//    private Long id;

    /**
     * Электронная почта пользователя.
     */
    private String email;

    /**
     * Роль пользователя. Представляет уровень доступа и привилегии пользователя в системе.
     */
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
     * Конструктор для создания объекта UserDTO на основе существующего объекта User.
     *
     * @param user Объект User, из которого будут извлечены данные.
     */
    public UserCreateDTO(User user) {

        this.email = user.getEmail();
        this.role = user.getRole();
        this.active = user.isActive();

        // Пароль не копируется, так как он является чувствительной информацией
    }

    public UserCreateDTO(String email, Role role, boolean active) {
        this.email = email;
        this.role = role;
        this.active = active;
    }
}
