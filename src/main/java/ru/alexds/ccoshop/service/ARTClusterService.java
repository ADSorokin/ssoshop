package ru.alexds.ccoshop.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.alexds.ccoshop.entity.ARTClusterEntity;
import ru.alexds.ccoshop.repository.ARTClusterRepository;

import java.util.List;
import java.util.Optional;

/**
 * Сервис для управления ART-кластерами.
 * Обеспечивает API для сохранения, получения и создания кластеров на основе весов и пользователей.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ARTClusterService {
    private final ARTClusterRepository artClusterRepository; // Репозиторий для работы с ART-кластерами

    /**
     * Сохраняет или обновляет существующий ART-кластер.
     *
     * @param cluster Кластер, который необходимо сохранить или обновить
     * @return Сохраненный или обновленный кластер
     */
    public ARTClusterEntity saveCluster(ARTClusterEntity cluster) {
        log.debug("Request to save or update ART cluster: {}", cluster);
        return artClusterRepository.save(cluster); // Сохраняем кластер в БД
    }

    /**
     * Получает все ART-кластеры из базы данных.
     *
     * @return Список всех кластеров
     */
    public List<ARTClusterEntity> getAllClusters() {
        log.debug("Request to get all ART clusters");
        return artClusterRepository.findAll(); // Получаем все кластеры из БД
    }

    /**
     * Находит кластер по его весам.
     *
     * @param weights Веса кластера, которые необходимо найти
     * @return Опциональный объект кластера, если он найден, или пустой Optional, если не найден
     */
    public Optional<ARTClusterEntity> findClusterByWeights(List<Double> weights) {
        log.debug("Request to find ART cluster by weights: {}", weights);
        return artClusterRepository.findByWeights(weights); // Ищем кластер по весам в БД
    }

    /**
     * Создает новый ART-кластер.
     *
     * @param weights Веса нового кластера
     * @param userIds Идентификаторы пользователей, принадлежащих новому кластеру
     * @return Созданный кластер
     */
    @Transactional
    public ARTClusterEntity createCluster(List<Double> weights, List<Long> userIds) {
        log.debug("Request to create new ART cluster with weights: {} and user IDs: {}", weights, userIds);
        ARTClusterEntity cluster = new ARTClusterEntity();
        cluster.setWeights(weights); // Устанавливаем веса для нового кластера
        cluster.setUserIds(userIds); // Устанавливаем идентификаторы пользователей для нового кластера
        return artClusterRepository.save(cluster); // Сохраняем новый кластер в БД
    }
}

