package ru.alexds.ccoshop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.alexds.ccoshop.entity.ARTClusterEntity;

import java.util.List;
import java.util.Optional;

public interface ARTClusterRepository extends JpaRepository<ARTClusterEntity, Long> {
    Optional<ARTClusterEntity> findByWeights(List<Double> weights); // Найти кластер по весам
}
