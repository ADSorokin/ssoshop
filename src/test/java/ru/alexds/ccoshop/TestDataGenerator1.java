package ru.alexds.ccoshop;

import org.springframework.beans.factory.annotation.Autowired;
import ru.alexds.ccoshop.entity.ARTClusterEntity;
import ru.alexds.ccoshop.entity.Rating;
import ru.alexds.ccoshop.repository.RatingRepository;
import ru.alexds.ccoshop.service.ARTClusterService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TestDataGenerator1 {
    @Autowired
    private RatingRepository ratingRepository;
    @Autowired
    private ARTClusterService artClusterService;

    public void generateTestData() {
        // Создаем тестовые рейтинги
        List<Rating> testRatings = new ArrayList<>();
        testRatings.add(new Rating(1L, 101L, 5.0));
        testRatings.add(new Rating(1L, 102L, 3.0));
        testRatings.add(new Rating(2L, 101L, 4.0));
        testRatings.add(new Rating(2L, 103L, 2.0));
        ratingRepository.saveAll(testRatings);

        // Создаем тестовые ART-кластеры
        ARTClusterEntity cluster = new ARTClusterEntity();
        cluster.setWeights(Arrays.asList(1.0, 0.5, 0.0)); // Пример весов
        cluster.setUserIds(Arrays.asList(1L, 2L)); // Пример пользователей
        artClusterService.saveCluster(cluster);
    }
}