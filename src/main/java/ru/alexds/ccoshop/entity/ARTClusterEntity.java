/**
 * Пакет для сущностей приложения.
 */
package ru.alexds.ccoshop.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

/**
 * Класс ARTClusterEntity представляет собой сущность, описывающую кластер в системе ART (Adaptive Resonance Theory).
 * Этот класс аннотирован как JPA-сущность и отображается в таблицу "art_clusters" в базе данных.
 * Для удобства использования Lombok генерирует геттеры, сеттеры, equals, hashCode и toString методы,
 * а также пустой, полностью параметризованный конструкторы и builder-конструктор.
 */
@Data // Генерирует геттеры, сеттеры, equals, hashCode и toString
@Builder // Генерирует builder-конструктор для удобного создания объектов
@NoArgsConstructor // Генерирует пустой конструктор
@AllArgsConstructor // Генерирует конструктор с аргументами
@Entity // Аннотация для обозначения класса как JPA-сущности
@Table(name = "art_clusters") // Аннотация для указания имени таблицы в базе данных
public class ARTClusterEntity {

    /**
     * Уникальный идентификатор кластера.
     * Аннотация @Id указывает на то, что это поле является первичным ключом.
     * Аннотация @GeneratedValue стратегией GenerationType.IDENTITY позволяет автоматически генерировать значения.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Уникальный идентификатор кластера

    /**
     * Веса (координаты) кластера.
     * Это коллекция значений типа Double, представляющая веса или координаты кластера.
     * Аннотация @ElementCollection указывает на коллекцию элементов, которые не являются сущностями.
     * Аннотация FetchType.EAGER загружает связь сразу при загрузке основной сущности.
     * Аннотация @CollectionTable задает имя таблицы и внешний ключ для хранения этой коллекции.
     * Аннотация @Column задает имя столбца для хранения значений весов.
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "cluster_weights",
            joinColumns = @JoinColumn(name = "cluster_id")
    )
    @Column(name = "weight")
    private List<Double> weights; // Веса (координаты) кластера

    /**
     * Список идентификаторов пользователей в кластере.
     * Это коллекция значений типа Long, представляющая идентификаторы пользователей, принадлежащих к данному кластеру.
     * Аннотация @ElementCollection указывает на коллекцию элементов, которые не являются сущностями.
     * Аннотация FetchType.EAGER загружает связь сразу при загрузке основной сущности.
     * Аннотация @CollectionTable задает имя таблицы и внешний ключ для хранения этой коллекции.
     * Аннотация @Column задает имя столбца для хранения идентификаторов пользователей.
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "cluster_users",
            joinColumns = @JoinColumn(name = "cluster_id")
    )
    @Column(name = "user_id")
    private List<Long> userIds; // Список идентификаторов пользователей в кластере
}

