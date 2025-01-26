/**
 * Пакет для репозиториев приложения.
 */
package ru.alexds.ccoshop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.alexds.ccoshop.entity.User;
import java.util.Optional;

/**
 * Интерфейс UserRepository представляет собой репозиторий для работы с сущностью User в базе данных.
 * Этот интерфейс расширяет JpaRepository, который предоставляет основные CRUD операции и другие полезные методы.
 * Для удобства использования Spring Data JPA генерирует реализацию этого интерфейса автоматически.
 */
@Repository // Аннотация для обозначения класса как репозитория
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Метод для поиска пользователя по его электронной почте.
     *
     * @param email Электронная почта пользователя.
     * @return Объект Optional, содержащий найденного пользователя, если он существует, или пустой Optional, если пользователь не найден.
     */
    Optional<User> findByEmail(String email);

    /**
     * Метод для проверки существования пользователя с указанной электронной почтой.
     *
     * @param email Электронная почта пользователя.
     * @return true, если пользователь с указанной электронной почтой существует, false в противном случае.
     */
    boolean existsByEmail(String email);
}