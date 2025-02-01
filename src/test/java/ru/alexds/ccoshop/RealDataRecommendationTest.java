package ru.alexds.ccoshop;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.mahout.cf.taste.common.TasteException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.alexds.ccoshop.dto.RecommendationDTO;
import ru.alexds.ccoshop.service.RecommendationService;

import java.util.List;

@SpringBootTest
public class RealDataRecommendationTest {

    @Autowired
    private RecommendationService recommendationService;

    @Test
    public void testRealUserBasedRecommendations() throws TasteException {
        // Вызов метода на реальных данных
        List<RecommendationDTO> recommendations = recommendationService.getUserBasedRecommendations(1L, 5);

        // Проверка результатов
        assertThat(recommendations).hasSizeGreaterThan(0);
        for (RecommendationDTO recommendation : recommendations) {
            assertThat(recommendation.getItemId()).isNotNull();
            assertThat(recommendation.getItemName()).isNotNull();
            assertThat(recommendation.getRating()).isBetween(1.0,10.0);
        }
    }

    @Test
    public void testRealItemBasedRecommendations() throws TasteException {
        // Вызов метода на реальных данных
        List<RecommendationDTO> recommendations = recommendationService.getItemBasedRecommendations(1L, 3);

        // Проверка результатов
        assertThat(recommendations).hasSizeGreaterThan(0);
        for (RecommendationDTO recommendation : recommendations) {
            assertThat(recommendation.getItemId()).isNotNull();
            assertThat(recommendation.getItemName()).isNotNull();
            assertThat(recommendation.getRating()).isBetween(1.0, 10.0);
        }
    }

    @Test
    public void testRealARTBasedRecommendations() {
        // Вызов метода на реальных данных
        List<RecommendationDTO> recommendations = recommendationService.getARTRecommendations(21L);

        // Проверка результатов
        assertThat(recommendations).hasSizeGreaterThan(0);
        for (RecommendationDTO recommendation : recommendations) {
            assertThat(recommendation.getItemId()).isNotNull();
            assertThat(recommendation.getItemName()).isNotNull();
            assertThat(recommendation.getRating()).isBetween(1.0, 10.0);
        }
    }
}