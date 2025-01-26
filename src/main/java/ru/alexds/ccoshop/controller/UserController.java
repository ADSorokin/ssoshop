package ru.alexds.ccoshop.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.alexds.ccoshop.dto.UserCreateDTO;
import ru.alexds.ccoshop.dto.UserDTO;
import ru.alexds.ccoshop.dto.UserUpdateDTO;
import ru.alexds.ccoshop.exeption.UserAlreadyExistsException;
import ru.alexds.ccoshop.service.UserService;

import java.util.List;
import java.util.Optional;

/**
 * Контроллер для управления пользователями.
 * Обеспечивает API для регистрации, получения, обновления и удаления пользователей,
 * а также для изменения статуса и пароля пользователя.
 */
@Slf4j
@RestController
@RequestMapping("/api/users") // Базовый URL
@RequiredArgsConstructor
@Validated
@Tag(name = "User Controller", description = "API для работы с пользователями системы")
public class UserController {
    private final UserService userService; // Сервис для управления пользователями

    /**
     * Регистрация нового пользователя.
     *
     * @param userCreateDTO DTO объект с информацией о новом пользователе (например, имя, email, пароль).
     * @return HTTP-ответ с зарегистрированным пользователем в формате DTO и статусом 201 (Created)
     *         если регистрация прошла успешно.
     * @throws IllegalArgumentException если переданы недействительные данные пользователя.
     * @throws UserAlreadyExistsException если пользователь с данным email уже существует.
     */
    @Operation(summary = "Регистрация нового пользователя")
    @PostMapping("/register")
    public ResponseEntity<UserCreateDTO> registerNewUser(@RequestBody @Valid UserCreateDTO userCreateDTO) {
        log.debug("Запрос на регистрацию нового пользователя: {}", userCreateDTO);
        try {
            UserCreateDTO registeredUser = userService.registerNewUser(userCreateDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(registeredUser);
        } catch (IllegalArgumentException e) {
            log.error("Неверный ввод для регистрации пользователя: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (UserAlreadyExistsException e) {
            log.error("Пользователь с адресом электронной почты уже существует: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).build(); // Возвращаем 409 Conflict если пользователь уже существует
        } catch (Exception e) {
            log.error("Ошибка регистрации пользователя: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // Возвращаем 500 Internal Server Error для других ошибок
        }
    }

    /**
     * Получает всех пользователей.
     *
     * @return HTTP-ответ со списком всех пользователей в формате DTO и статусом 200 (OK)
     */
    @Operation(summary = "Вывести всех пользователей")
    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        log.debug("Запросить получение всех пользователей");
        List<UserDTO> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    /**
     * Получает пользователя по его идентификатору.
     *
     * @param id Идентификатор пользователя, который необходимо получить
     * @return HTTP-ответ с пользователем в формате DTO и статусом 200 (OK), если пользователь найден,
     *         или статусом 404 (Not Found) если пользователь не найден
     */
    @Operation(summary = "Получить пользователя по ID")
    @GetMapping("/{id}") // Получить пользователя по ID
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        log.debug("Запрос получить пользователя по ID: {}", id);
        Optional<UserDTO> user = userService.getUserById(id);
        return user.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Обновляет информацию о существующем пользователе.
     *
     * @param id      Идентификатор пользователя, которого необходимо обновить
     * @param userUpdateDTO DTO объект с новыми данными о пользователе (например, имя, email, пароль)
     * @return HTTP-ответ с обновленным пользователем в формате DTO и статусом 200 (OK),
     *         или статусом 400 (Bad Request) при некорректных данных,
     *         или статусом 404 (Not Found) если пользователь не найден
     */
    @Operation(summary = "Обновление информации о пользователе")
    @PutMapping("/{id}") // Обновление пользователя
    public ResponseEntity<UserUpdateDTO> updateUser(@PathVariable Long id, @RequestBody @Valid UserUpdateDTO userUpdateDTO) {
        userUpdateDTO.setId(id); // Установить ID из URL
        log.debug("Запрос на обновление пользователя с помощью ID: {} and data: {}", id, userUpdateDTO);

        try {
            UserUpdateDTO updatedUser = userService.updateUser(userUpdateDTO);
            return ResponseEntity.ok(updatedUser);
        } catch (IllegalArgumentException e) {
            log.error("Invalid data for user update with ID {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (EntityNotFoundException e) {
            log.error("User not found with ID {}: {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Удаляет пользователя по его идентификатору.
     *
     * @param id Идентификатор пользователя, которого необходимо удалить
     * @return HTTP-ответ без содержимого и статусом 204 (No Content), подтверждающий успешное удаление
     * @throws EntityNotFoundException если пользователь с указанным идентификатором не найден
     */
    @Operation(summary = "Удаление пользователя")
    @DeleteMapping("/{id}") // Удаление пользователя
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        log.debug("Request to delete user with ID: {}", id);
        try {
            userService.deleteUser(id);
            return ResponseEntity.noContent().build(); // Отправить 204 No Content
        } catch (EntityNotFoundException e) {
            log.error("User not found with ID {}: {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Изменяет статус активности пользователя.
     *
     * @param id     Идентификатор пользователя, чей статус необходимо изменить
     * @param active Новый статус активности пользователя
     * @return HTTP-ответ без содержимого и статусом 204 (No Content), подтверждающий успешное изменение статуса
     * @throws EntityNotFoundException если пользователь с указанным идентификатором не найден
     */
    @Operation(summary = "Изменение статуса пользователя")
    @PutMapping("/{id}/status") // Изменение статуса
    public ResponseEntity<Void> changeUserStatus(@PathVariable Long id, @RequestParam boolean active) {
        log.debug("Request to change status of user with ID: {} to {}", id, active);
        try {
            userService.changeUserStatus(id, active);
            return ResponseEntity.noContent().build(); // Отправить 204 No Content
        } catch (EntityNotFoundException e) {
            log.error("User not found with ID {}: {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Изменяет пароль пользователя.
     *
     * @param id          Идентификатор пользователя, чей пароль необходимо изменить
     * @param newPassword Новый пароль пользователя
     * @return HTTP-ответ без содержимого и статусом 204 (No Content), подтверждающий успешное изменение пароля
     * @throws EntityNotFoundException если пользователь с указанным идентификатором не найден
     */
    @Operation(summary = "Изменение пароля пользователя")
    @PutMapping("/{id}/password") // Изменение пароля
    public ResponseEntity<Void> changePassword(@PathVariable Long id, @RequestBody String newPassword) {
        log.debug("Request to change password for user with ID: {}", id);
        try {
            userService.changePassword(id, newPassword);
            return ResponseEntity.noContent().build(); // Отправить 204 No Content
        } catch (EntityNotFoundException e) {
            log.error("User not found with ID {}: {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            log.error("Invalid password provided for user with ID {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
}