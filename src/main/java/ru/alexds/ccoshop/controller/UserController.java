package ru.alexds.ccoshop.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.alexds.ccoshop.dto.UserDTO;
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
    public ResponseEntity<UserDTO> registerNewUser(@RequestBody UserDTO userDTO) {
        UserDTO registeredUser = userService.registerNewUser(userDTO);
        return ResponseEntity.ok(registeredUser);
    }

    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserDTO> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}") // Получить пользователя по ID
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        Optional<UserDTO> user = userService.getUserById(id);
        return user.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}") // Обновление пользователя
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id, @RequestBody UserDTO userDTO) {
        userDTO.setId(id); // Установить ID из URL
        UserDTO updatedUser = userService.updateUser(userDTO);
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