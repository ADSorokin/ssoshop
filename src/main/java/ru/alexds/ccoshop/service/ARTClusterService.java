package ru.alexds.ccoshop.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.alexds.ccoshop.entity.ARTClusterEntity;
import ru.alexds.ccoshop.repository.ARTClusterRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ARTClusterService {

    private final ARTClusterRepository artClusterRepository;

    // Сохранение или обновление кластера
    public ARTClusterEntity saveCluster(ARTClusterEntity cluster) {
        return artClusterRepository.save(cluster);
    }

    // Получить все кластеры
    public List<ARTClusterEntity> getAllClusters() {
        return artClusterRepository.findAll();
    }

    // Найти кластер по весам
    public Optional<ARTClusterEntity> findClusterByWeights(List<Double> weights) {
        return artClusterRepository.findByWeights(weights);
    }
}

