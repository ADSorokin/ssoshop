package ru.alexds.ccoshop.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ARTClusterDTO {
    private Long id;                 // Уникальный идентификатор кластера
    private List<Double> weights;    // Веса (координаты) кластера
    private List<Long> userIds;      // Список идентификаторов пользователей, входящих в кластер
}
