package ru.alexds.ccoshop.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.alexds.ccoshop.dto.UserCreateDTO;
import ru.alexds.ccoshop.dto.UserDTO;
import ru.alexds.ccoshop.dto.UserUpdateDTO;
import ru.alexds.ccoshop.entity.User;
import ru.alexds.ccoshop.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Сервис для управления пользователями.
 * Обеспечивает API для регистрации, получения, обновления и удаления пользователей,
 * а также для изменения статуса и пароля пользователя.
 */
@Slf4j
@Service
public class UserService {
    @Autowired
    private UserRepository userRepository; // Репозиторий для работы с пользователями

    private final PasswordEncoder passwordEncoder; // Шифрование паролей

    /**
     * Конструктор класса UserService.
     *
     * @param userRepository   Репозиторий для работы с пользователями
     * @param passwordEncoder  Кодировщик паролей
     */
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Регистрирует нового пользователя.
     *
     * @param userCreateDTO DTO объект с информацией о новом пользователе (email, password, role)
     * @return DTO объект зарегистрированного пользователя
     */
    public UserCreateDTO registerNewUser(UserCreateDTO userCreateDTO) {
        log.debug("Запрос на регистрацию нового пользователя: {}", userCreateDTO);

        // Преобразование UserDTO в User
        User user = new User();
        user.setEmail(userCreateDTO.getEmail());
        user.setPassword(passwordEncoder.encode(userCreateDTO.getPassword())); // Шифруем пароль перед сохранением
        user.setRole(userCreateDTO.getRole());
        user.setActive(true); // Значение по умолчанию - активный пользователь

        User savedUser = userRepository.save(user); // Сохраняем нового пользователя в базе данных
        log.info("Успешно зарегистрирован пользователь с ID: {}", savedUser.getId());

        return new UserCreateDTO(savedUser); // Возвращаем DTO нового пользователя
    }

    /**
     * Получает всех пользователей.
     *
     * @return Список всех пользователей в формате DTO
     */
    public List<UserDTO> getAllUsers() {
        log.debug("Запросить получение всех пользователей");

        // Преобразуем все пользователи из репозитория в DTO
        return userRepository.findAll().stream()
                .map(UserDTO::new)
                .collect(Collectors.toList());
    }

    /**
     * Получает пользователя по его идентификатору.
     *
     * @param id Идентификатор пользователя, который необходимо получить
     * @return Опциональный DTO объект пользователя, если он найден
     */
    public Optional<UserDTO> getUserById(Long id) {
        log.debug("Запрос получить пользователя по ID: {}", id);

        // Преобразуем найденного пользователя в DTO
        return userRepository.findById(id)
                .map(UserDTO::new);
    }

    /**
     * Метод для получения сущности User.
     *
     * @param userId Идентификатор пользователя, которого необходимо найти
     * @return Опциональный объект пользователя, если он найден
     */
    public Optional<User> getUserEntityById(Long userId) {
        log.debug("Request to get user entity by ID: {}", userId);
        return userRepository.findById(userId);
    }

    /**
     * Обновляет информацию о существующем пользователе.
     *
     * @param userUpdateDTO DTO объект с новыми данными о пользователе (например, email,имя, фамилия )
     * @return DTO объект обновленного пользователя
     * @throws UsernameNotFoundException если пользователь не найден
     */
    public UserUpdateDTO updateUser(UserUpdateDTO userUpdateDTO) {
        log.debug("Запрос на обновление пользователя с помощью ID: {} и данные: {}", userUpdateDTO.getId(), userUpdateDTO);

        // Предположим, что можно получить существующего пользователя
        User user = userRepository.findById(userUpdateDTO.getId())
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));

        // Обновляем поля, которые разрешены

        user.setEmail(userUpdateDTO.getEmail());
        user.setFirstName(userUpdateDTO.getFirstName());
        user.setLastName(userUpdateDTO.getLastName());

        User updatedUser = userRepository.save(user); // Сохраняем обновленного пользователя в базе данных
        log.info("Успешно обновлен пользователя с ID: {}", updatedUser.getId());

        return new UserUpdateDTO(updatedUser); // Возвращаем DTO обновленного пользователя
    }

    /**
     * Удаляет пользователя по его идентификатору.
     *
     * @param id Идентификатор пользователя, которого необходимо удалить
     */
    public void deleteUser(Long id) {
        log.debug("Запрос на удаление пользователя с ID: {}", id);
        userRepository.deleteById(id); // Удаляем пользователя из базы данных
        log.info("Успешно удалил пользователя с ID: {}", id);
    }

    /**
     * Изменяет статус активности пользователя.
     *
     * @param userId Идентификатор пользователя, чей статус необходимо изменить
     * @param active Новый статус активности пользователя (true или false)
     */
    public void changeUserStatus(Long userId, boolean active) {
        log.debug("Запрос изменить статус пользователя с ID: {} to {}", userId, active);

        userRepository.findById(userId).ifPresent(user -> {
            user.setActive(active); // Обновляем статус активности пользователя
            userRepository.save(user); // Сохраняем изменения в базе данных
            log.info("Статус пользователя успешно изменен с ID: {} to {}", userId, active);
        });
    }

    /**
     * Изменяет пароль пользователя.
     *
     * @param userId      Идентификатор пользователя, чей пароль необходимо изменить
     * @param newPassword Новый пароль пользователя
     */
    public void changePassword(Long userId, String newPassword) {
        log.debug("Request to change password for user with ID: {}", userId);

        userRepository.findById(userId).ifPresent(user -> {
            user.setPassword(passwordEncoder.encode(newPassword)); // Шифруем новый пароль перед сохранением
            userRepository.save(user); // Сохраняем изменения в базе данных
            log.info("Successfully changed password for user with ID: {}", userId);
        });
    }

    /**
     * Вспомогательный метод для преобразования объекта User в DTO.
     *
     * @param user Объект пользователя, который необходимо преобразовать
     * @return DTO объект пользователя
     */
    private UserDTO convertToDTO(User user) {
        log.debug("Converting user with ID: {} to DTO", user.getId());
        return new UserDTO(user); // Преобразуем объект пользователя в DTO
    }
}