/**
 * Пакет для DTO (Data Transfer Objects) приложения.
 */
package ru.alexds.ccoshop.dto;


import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.alexds.ccoshop.entity.Role;
import ru.alexds.ccoshop.entity.User;


/**
 * Класс UserDTO представляет собой DTO (Data Transfer Object), используемый для передачи данных о пользователе между слоями приложения.
 * Этот класс используется для упрощения и защиты передачи данных, избегая передачи ненужных или чувствительных полей.
 * Для удобства использования Lombok генерирует геттеры, сеттеры, equals, hashCode и toString методы,
 * а также пустой, полностью параметризованный конструкторы.
 */
@Data // Генерирует геттеры, сеттеры, equals, hashCode и toString
@NoArgsConstructor // Генерирует пустой конструктор
@AllArgsConstructor // Генерирует конструктор с аргументами

public class UserUpdateDTO {
    /**
     * Уникальный идентификатор пользователя.
     */
    private Long id;

    /**
     * Электронная почта пользователя.
     */
    private String email;

    /**
     * Имя пользователя. Поле не может быть пустым.
     * Аннотация @NotEmpty проверяет, чтобы строка была не пустой.
     */
    @NotEmpty(message = "First name cannot be empty")
    private String firstName;
    /**
     * Фамилия пользователя. Поле не может быть пустым.
     * Аннотация @NotEmpty проверяет, чтобы строка была не пустой.
     */
    @NotEmpty(message = "Last name cannot be empty")
    private String lastName;

    /**
     * Роль пользователя. Представляет уровень доступа и привилегии пользователя в системе.
     */


    /**
     * Состояние учетной записи пользователя (активирована или деактивирована).


    /**
     * Пароль пользователя.  Обычно не передается через DTO для безопасности.
     * Оставлено здесь для примера.
     */
 //   private String password;

    /**
     * Конструктор для создания объекта UserDTO на основе существующего объекта User.
     *
     * @param user Объект User, из которого будут извлечены данные.
     */

    public UserUpdateDTO(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();

        // Пароль не копируется, так как он является чувствительной информацией
    }
}