package ru.alexds.ccoshop;

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
import ru.alexds.ccoshop.dto.RecommendationDTO;
import ru.alexds.ccoshop.repository.ProductRepository;
import ru.alexds.ccoshop.service.OrderService;
import ru.alexds.ccoshop.service.RecommendationService;
import ru.alexds.ccoshop.service.UserService;

import java.util.List;

@ExtendWith(MockitoExtension.class)



class RecommendationServiceTest {

    @InjectMocks
    private RecommendationService recommendationService;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserService userService;

    @Mock
    private OrderService orderService;

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
        Assertions.assertEquals(2, result.size());
        Assertions.assertEquals(1L, result.get(0).getItemId());
        Assertions.assertEquals(4.5, result.get(0).getRating());
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
}
