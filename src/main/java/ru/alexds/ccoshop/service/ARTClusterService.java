package ru.alexds.ccoshop.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

    @Transactional
    public ARTClusterEntity createCluster(List<Double> weights, List<Long> userIds) {
        ARTClusterEntity cluster = new ARTClusterEntity();
        cluster.setWeights(weights);
        cluster.setUserIds(userIds);
        return artClusterRepository.save(cluster); // Сохранение в БД
    }
}

