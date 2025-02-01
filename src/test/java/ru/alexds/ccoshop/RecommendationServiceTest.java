package ru.alexds.ccoshop;

import org.antlr.v4.runtime.misc.LogManager;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import ru.alexds.ccoshop.dto.RecommendationDTO;
import ru.alexds.ccoshop.entity.Rating;
import ru.alexds.ccoshop.repository.RatingRepository;
import ru.alexds.ccoshop.service.RecommendationService;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class RecommendationServiceTest {

    @InjectMocks
    private RecommendationService recommendationService;
  @Autowired
    RatingRepository ratingRepository;
    @Mock
    private DataModel dataModel;

    @Test
    void testGetUserBasedRecommendationsSuccess() throws Exception {
        // Условия (Arrange)
        Long userId = 1L;
        int numRecommendations = 5;

        // Мок данных для Mahout
        UserSimilarity similarity = Mockito.mock(UserSimilarity.class);
        UserNeighborhood neighborhood = Mockito.mock(UserNeighborhood.class);
        GenericUserBasedRecommender mockedRecommender = Mockito.mock(GenericUserBasedRecommender.class);
        List<RecommendedItem> mockRecommendations = List.of(
                createMockRecommendedItem(1L, 4.5),
                createMockRecommendedItem(2L, 4.0)
        );

        // Создаем моки поведения Mahout
        Mockito.when(mockedRecommender.recommend(Mockito.eq(userId), Mockito.eq(numRecommendations)))
                .thenReturn(mockRecommendations);

        // Действие (Act)
        List<RecommendationDTO> result = recommendationService.getUserBasedRecommendations(userId, numRecommendations);

        // Проверка (Assert)
        Assertions.assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getItemId());
        assertEquals(4.5, result.get(0).getRating());
    }

    @Test
    void createUserVector_UserWithRatings_ReturnsCorrectVector() {
        // Подготовка тестовых данных в репозитории
        Rating rating1 = new Rating(1L, 1L, 5.0);
        Rating rating2 = new Rating(1L, 3L, 4.0);
        ratingRepository.saveAll(List.of(rating1, rating2));

        double[] vector = recommendationService.createUserVector(1L);
        assertArrayEquals(new double[]{1.0, 0.0, 1.0}, vector, 0.01);
    }
    @Test
    void testGetUserBasedRecommendationsFailure() {
        // Условия: выбрасываем исключение
        Long userId = 1L;
        int numRecommendations = 5;

        try {
            Mockito.doThrow(new RuntimeException("DataModel error"))
                    .when(dataModel)
                    .getNumUsers();
        } catch (TasteException e) {
            throw new RuntimeException(e);
        }

        // Проверка на исключение
        Assertions.assertThrows(RuntimeException.class,
                () -> recommendationService.getUserBasedRecommendations(userId, numRecommendations));
    }

    // Helper для создания RecommendedItem
    private RecommendedItem createMockRecommendedItem(long itemId, double value) {
        RecommendedItem item = Mockito.mock(RecommendedItem.class);
        Mockito.when(item.getItemID()).thenReturn(itemId);
        Mockito.when(item.getValue()).thenReturn((float) value);
        return item;
    }



    @Test
    public void testArtBasedRecommendations() {
        // Подготовка тестовых данных

        ratingRepository.save(new Rating(1L, 1L, 5.0));
        ratingRepository.save(new Rating(1L, 2L, 3.0));
        ratingRepository.save(new Rating(2L, 1L, 5.0));
        ratingRepository.save(new Rating(2L, 3L, 2.0));

        // Вызов метода
        List<RecommendationDTO> recommendations = recommendationService.getARTRecommendations(1L);

        // Проверка результатов
        assertThat(recommendations).isNotNull();
        assertThat(recommendations.get(0).getItemId()).isEqualTo(3L); // Предполагаем, что товар 3 будет рекомендован
    }
    @Test
    void normalizeRatings_AllSameScores_ReturnsMidValue() {
        List<RecommendationDTO> input = List.of(
                new RecommendationDTO(1L, "Item1", 5.0),
                new RecommendationDTO(2L, "Item2", 5.0)
        );
        List<RecommendationDTO> result = recommendationService.normalizeRatings(input);
        assertEquals(5.0, result.get(0).getRating(), 0.01);
    }
}

