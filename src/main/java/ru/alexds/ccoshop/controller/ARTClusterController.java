package ru.alexds.ccoshop.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.alexds.ccoshop.dto.CreateClusterDTO;
import ru.alexds.ccoshop.entity.ARTClusterEntity;
import ru.alexds.ccoshop.service.ARTClusterService;

import java.util.List;

@RestController
@RequestMapping("/api/clusters")
@RequiredArgsConstructor
@Tag(name = "ART Cluster Controller", description = "API работы с ART кластерами ")
public class ARTClusterController {

    private final ARTClusterService clusterService;
    ARTClusterEntity cluster;

    // Создание нового кластера
    /**
     * Создание нового ART кластера.
     *
     * @param weights и userIds данные для создания кластера
     * @return ResponseEntity с созданным ART кластером
     */
    @Operation(
            summary = "Создание нового ART кластера",
            description = "Создает новый ART кластер на основе предоставленных данных"
    )
    @ApiResponse(responseCode = "200", description = "Кластер успешно создан")
    @ApiResponse(responseCode = "400", description = "Ошибка валидации данных")
    @PostMapping
    public ResponseEntity<ARTClusterEntity> createCluster(@RequestBody @Valid List<Double> weights, List<Long> userIds) {
        ARTClusterEntity cluster = clusterService.createCluster( weights,  userIds);
        return ResponseEntity.status(HttpStatus.CREATED).body(cluster); // Возвращаем статус 201 Created
    }

    // Получение всех кластеров
    @Operation(summary = "Посмотр всех кластеров")
    @GetMapping
    public ResponseEntity<List<ARTClusterEntity>> getAllClusters() {
        return ResponseEntity.ok(clusterService.getAllClusters());
    }
}
