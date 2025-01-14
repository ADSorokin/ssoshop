package ru.alexds.ccoshop.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "art_clusters")
public class ARTClusterEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Уникальный идентификатор кластера

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "cluster_weights", joinColumns = @JoinColumn(name = "cluster_id"))
    @Column(name = "weight")
    private List<Double> weights; // Веса (координаты) кластера

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "cluster_users", joinColumns = @JoinColumn(name = "cluster_id"))
    @Column(name = "user_id")
    private List<Long> userIds; // Список идентификаторов пользователей в кластере
}

