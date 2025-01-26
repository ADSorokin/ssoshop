/**
 * Пакет для репозиториев приложения.
 */
package ru.alexds.ccoshop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.alexds.ccoshop.entity.ARTClusterEntity;
import java.util.List;
import java.util.Optional;

/**
 * Интерфейс ARTClusterRepository представляет собой репозиторий для работы с сущностью ARTClusterEntity в базе данных.
 * Этот интерфейс расширяет JpaRepository, который предоставляет основные CRUD операции и другие полезные методы.
 * Для удобства использования Spring Data JPA генерирует реализацию этого интерфейса автоматически.
 */
public interface ARTClusterRepository extends JpaRepository<ARTClusterEntity, Long> {

    /**
     * Метод для поиска кластера по его весам.
     *
     * @param weights Список весов (координат) кластера.
     * @return Объект Optional, содержащий найденный кластер, если он существует,
     *         или пустой Optional, если кластер не найден.
     */
    Optional<ARTClusterEntity> findByWeights(List<Double> weights); // Найти кластер по весам
}