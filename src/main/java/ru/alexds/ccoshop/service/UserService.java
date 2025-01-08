package ru.alexds.ccoshop.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.alexds.ccoshop.dto.UserDTO;
import ru.alexds.ccoshop.entity.User;
import ru.alexds.ccoshop.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserDTO registerNewUser(UserDTO userDTO) {
        // Преобразование UserDTO в User
        User user = new User();
        user.setEmail(userDTO.getEmail());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword())); // Шифруем пароль
        user.setRole(userDTO.getRole());
        user.setActive(true); // Значение по умолчанию

        User savedUser = userRepository.save(user);
        return new UserDTO(savedUser); // Возвращаем DTO
    }

    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserDTO::new)
                .collect(Collectors.toList());
    }

    public Optional<UserDTO> getUserById(Long id) {
        return userRepository.findById(id)
                .map(UserDTO::new);
    }

    // Метод для получения сущности User
    public Optional<User> getUserEntityById(Long userId) {
        return userRepository.findById(userId);
    }

    public UserDTO updateUser(UserDTO userDTO) {
        // Предположим, что можно получить существующего пользователя
        User user = userRepository.findById(userDTO.getId())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Обновляем поля, которые разрешены
        user.setEmail(userDTO.getEmail());
        user.setRole(userDTO.getRole());
        user.setActive(userDTO.isActive());

        User updatedUser = userRepository.save(user);
        return new UserDTO(updatedUser);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public void changeUserStatus(Long userId, boolean active) {
        userRepository.findById(userId).ifPresent(user -> {
            user.setActive(active);
            userRepository.save(user);
        });
    }

    public void changePassword(Long userId, String newPassword) {
        userRepository.findById(userId).ifPresent(user -> {
            user.setPassword(passwordEncoder.encode(newPassword)); // Шифруем новый пароль
            userRepository.save(user);
        });
    }

    private UserDTO convertToDTO(User user) {
        return new UserDTO(user);
    }
}

