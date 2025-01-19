package ru.alexds.ccoshop.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.alexds.ccoshop.dto.CreateClusterDTO;
import ru.alexds.ccoshop.entity.ARTClusterEntity;
import ru.alexds.ccoshop.service.ARTClusterService;

import java.util.List;

@RestController
@RequestMapping("/api/clusters")
@RequiredArgsConstructor
public class ARTClusterController {

    private final ARTClusterService clusterService;
    ARTClusterEntity cluster;
    // Создание нового кластера
    @PostMapping
    public ResponseEntity<ARTClusterEntity> createCluster(@RequestBody CreateClusterDTO dto) {


        return ResponseEntity.ok(cluster);
    }

    // Получение всех кластеров
    @GetMapping
    public ResponseEntity<List<ARTClusterEntity>> getAllClusters() {
        return ResponseEntity.ok(clusterService.getAllClusters());
    }
}
