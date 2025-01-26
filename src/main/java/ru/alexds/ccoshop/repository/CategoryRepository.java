/**
 * Пакет для репозиториев приложения.
 */
package ru.alexds.ccoshop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.alexds.ccoshop.entity.Category;

/**
 * Интерфейс CategoryRepository представляет собой репозиторий для работы с сущностью Category в базе данных.
 * Этот интерфейс расширяет JpaRepository, который предоставляет основные CRUD операции и другие полезные методы.
 * Для удобства использования Spring Data JPA генерирует реализацию этого интерфейса автоматически.
 */
@Repository // Аннотация для обозначения класса как репозитория
public interface CategoryRepository extends JpaRepository<Category, Long> {
    /**
     * Репозиторий предоставляет стандартные CRUD-операции для работы с категориями:
     * - сохранение (save)
     * - удаление (deleteById)
     * - поиск по идентификатору (findById)
     * - получение всех записей (findAll)
     *
     * Эти методы предоставляются интерфейсом JpaRepository и не требуют дополнительной реализации.
     */
}
