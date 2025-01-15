package ru.alexds.ccoshop;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.alexds.ccoshop.controller.RecommendationController;
import ru.alexds.ccoshop.dto.RecommendationDTO;
import ru.alexds.ccoshop.service.RecommendationService;

import java.math.BigDecimal;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class RecomendationControllerTest {

    @InjectMocks
    private RecommendationController recommendationController;

    @Mock
    private RecommendationService recommendationService;

    @Test
    void testGetUserBasedRecommendationsSuccess() throws Exception {
        // Условия (Arrange)
        Long userId = 1L;
        int numRecommendations = 5;
        List<RecommendationDTO> mockRecommendations = List.of(
                createMockRecommendation(1L, "Product A", 4.5),
                createMockRecommendation(2L, "Product B", 4.0)
        );
        Mockito.when(recommendationService.getUserBasedRecommendations(userId, numRecommendations))
                .thenReturn(mockRecommendations);

        // Действие (Act)
        ResponseEntity<List<RecommendationDTO>> response = recommendationController.getUserBasedRecommendations(userId, numRecommendations);

        // Проверка (Assert)
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals(2, response.getBody().size());
        Assertions.assertEquals("Product A", response.getBody().get(0).getItemName());
    }

    @Test
    void testGetUserBasedRecommendationsFailure() throws Exception {
        // Условия: бросаем исключение
        Long userId = 1L;
        Mockito.when(recommendationService.getUserBasedRecommendations(Mockito.anyLong(), Mockito.anyInt()))
                .thenThrow(new RuntimeException("Service error"));

        // Действие
        ResponseEntity<List<RecommendationDTO>> response = recommendationController.getUserBasedRecommendations(userId, 5);

        // Проверка
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Assertions.assertNull(response.getBody());
    }

    // Mock RecommendationDTO creation helper
    private RecommendationDTO createMockRecommendation(Long id, String name, Double score) {
        return RecommendationDTO.builder()
                .itemId(id)
                .itemName(name)
                .rating(score)
                .build();
    }
}

