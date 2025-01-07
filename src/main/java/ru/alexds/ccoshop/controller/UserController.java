package ru.alexds.ccoshop.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.alexds.ccoshop.entity.User;
import ru.alexds.ccoshop.service.UserService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users") // Базовый URL
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<User> registerNewUser(@RequestBody User user) {
        User registeredUser = userService.registerNewUser(user);
        return ResponseEntity.ok(registeredUser);
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}") // Получить пользователя по ID
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        Optional<User> user = userService.getUserById(id);
        return user.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}") // Обновление пользователя
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User user) {
        user.setId(id); // Установить ID из URL
        User updatedUser = userService.updateUser(user);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/{id}") // Удаление пользователя
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build(); // Отправить 204 No Content
    }

    @PutMapping("/{id}/status") // Изменение статуса
    public ResponseEntity<Void> changeUserStatus(@PathVariable Long id, @RequestParam boolean active) {
        userService.changeUserStatus(id, active);
        return ResponseEntity.noContent().build(); // Отправить 204 No Content
    }

    @PutMapping("/{id}/password") // Изменение пароля
    public ResponseEntity<Void> changePassword(@PathVariable Long id, @RequestBody String newPassword) {
        userService.changePassword(id, newPassword);
        return ResponseEntity.noContent().build(); // Отправить 204 No Content
    }
}