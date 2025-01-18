package ru.alexds.ccoshop;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.alexds.ccoshop.entity.ARTClusterEntity;
import ru.alexds.ccoshop.service.ARTClusterService;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class ARTClusterEntityTest {

    @Autowired
    private ARTClusterService artClusterService;

    @Test
    public void testCreateCluster() {
        List<Double> weights = Arrays.asList(0.1, 0.2, 0.3);
        List<Long> users = Arrays.asList(1L, 2L);

        ARTClusterEntity cluster = artClusterService.createCluster(weights, users);
        assertNotNull(cluster.getId()); // Проверяем, что id не null после сохранения
    }
}
