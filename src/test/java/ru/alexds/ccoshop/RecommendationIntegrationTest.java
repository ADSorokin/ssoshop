package ru.alexds.ccoshop;

import org.apache.mahout.cf.taste.common.TasteException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import ru.alexds.ccoshop.dto.RecommendationDTO;
import ru.alexds.ccoshop.entity.Rating;
import ru.alexds.ccoshop.repository.RatingRepository;
import ru.alexds.ccoshop.service.ARTClusterService;
import ru.alexds.ccoshop.service.ProductService;
import ru.alexds.ccoshop.service.RecommendationService;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@Import({RecommendationService.class, ProductService.class, ARTClusterService.class})
public class RecommendationIntegrationTest {

    @Autowired
    private RatingRepository ratingRepository;

    @Autowired
    private RecommendationService recommendationService;

    @Autowired
    private ProductService productService;

    @Autowired
    private ARTClusterService artClusterService;

    @Test
    public void testIntegrationWithDatabase() throws TasteException {
        // Подготовка тестовых данных
        ratingRepository.save(new Rating(1L, 101L, 5.0));
        ratingRepository.save(new Rating(1L, 102L, 3.0));
        ratingRepository.save(new Rating(2L, 101L, 4.0));
        ratingRepository.save(new Rating(2L, 103L, 2.0));

        // Вызов метода
        List<RecommendationDTO> recommendations = recommendationService.getUserBasedRecommendations(1L, 1);

        // Проверка результатов
        assertThat(recommendations).isNotNull();
        assertThat(recommendations.get(0).getItemId()).isEqualTo(103L); // Предполагаем, что товар 103 будет рекомендован
    }




}
