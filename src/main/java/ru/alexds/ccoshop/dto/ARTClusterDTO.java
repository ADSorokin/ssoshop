/**
 * Пакет для DTO (Data Transfer Objects) приложения.
 */
package ru.alexds.ccoshop.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.alexds.ccoshop.entity.ARTClusterEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Класс ARTClusterDTO представляет собой DTO (Data Transfer Object), используемый для передачи данных о кластере в системе ART (Adaptive Resonance Theory) между слоями приложения.
 * Этот класс используется для упрощения и защиты передачи данных, избегая передачи ненужных или чувствительных полей.
 * Для удобства использования Lombok генерирует геттеры, сеттеры, equals, hashCode и toString методы,
 * а также пустой и полностью параметризованный конструкторы.
 */
@Data // Генерирует геттеры, сеттеры, equals, hashCode и toString
@NoArgsConstructor // Генерирует пустой конструктор
@AllArgsConstructor // Генерирует конструктор с аргументами
public class ARTClusterDTO {

    /**
     * Уникальный идентификатор кластера.
     * Это уникальный идентификатор записи, представляющей кластер.
     */
    private Long id;                 // Уникальный идентификатор кластера

    /**
     * Веса (координаты) кластера.
     * Это коллекция значений типа Double, представляющая веса или координаты кластера.
     * Веса используются для определения положения кластера в пространстве признаков.
     */
    private List<Double> weights;    // Веса (координаты) кластера

    /**
     * Список идентификаторов пользователей, входящих в кластер.
     * Это коллекция значений типа Long, представляющая идентификаторы пользователей, принадлежащих данному кластеру.
     * Используется для отслеживания пользователей, которые были классифицированы в данный кластер.
     */
    private List<Long> userIds;      // Список идентификаторов пользователей, входящих в кластер

    /**
     * Конструктор для создания объекта ARTClusterDTO на основе существующего объекта ARTClusterEntity.
     *
     * @param cluster Объект ARTClusterEntity, из которого будут извлечены данные.
     */
    public ARTClusterDTO(ARTClusterEntity cluster) {
        if (cluster != null) {
            this.id = cluster.getId();
            this.weights = cluster.getWeights();  // Предполагается, что у объекта ARTClusterEntity есть метод getWeights()
            this.userIds = cluster.getUserIds();  // Предполагается, что у объекта ARTClusterEntity есть метод getUserIds()
        }
    }

    /**
     * Метод для добавления пользователя в кластер.
     *
     * @param userId Идентификатор пользователя, который будет добавлен в кластер.
     */
    public void addUser(Long userId) {
        if (this.userIds == null) {
            this.userIds = new ArrayList<>();
        }
        this.userIds.add(userId);
    }

    /**
     * Метод для удаления пользователя из кластера.
     *
     * @param userId Идентификатор пользователя, который будет удален из кластера.
     */
    public void removeUser(Long userId) {
        if (this.userIds != null) {
            this.userIds.remove(userId);
        }
    }
}