import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import org.apache.mahout.cf.taste.common.TasteException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.alexds.ccoshop.dto.ProductDTO;
import ru.alexds.ccoshop.dto.RecommendationDTO;
import ru.alexds.ccoshop.entity.Rating;
import ru.alexds.ccoshop.repository.RatingRepository;
import ru.alexds.ccoshop.service.RecommendationService;
import ru.alexds.ccoshop.service.ProductService;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class RecommendationServiceTest {

    @Mock
    private RatingRepository ratingRepository;

    @Mock
    private ProductService productService;

    @InjectMocks
    private RecommendationService recommendationService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetUserBasedRecommendations() throws TasteException {
        // Подготовка тестовых данных
        when(ratingRepository.findByUserId(anyLong())).thenReturn(Arrays.asList(
                new Rating(1L, 101L, 5.0),
                new Rating(1L, 102L, 3.0)
        ));
        when(productService.getProductById(anyLong())).thenReturn(Optional.of(new ProductDTO(103L, "Product C", 4.0)));

        // Вызов метода
        List<RecommendationDTO> recommendations = recommendationService.getUserBasedRecommendations(1L, 1);

        // Проверка результатов
        assertEquals(1, recommendations.size());
        assertEquals(103L, recommendations.get(0).getItemId());
    }

    @Test
    public void testGetItemBasedRecommendations() throws TasteException {
        // Подготовка тестовых данных
        when(ratingRepository.findByItemId(anyLong())).thenReturn(Arrays.asList(
                new Rating(1L, 101L, 5.0),
                new Rating(2L, 101L, 4.0),
                new Rating(2L, 102L, 3.0)
        ));
        when(productService.getProductById(anyLong())).thenReturn(Optional.of(new ProductDTO(102L, "Product B", 3.0)));

        // Вызов метода
        List<RecommendationDTO> recommendations = recommendationService.getItemBasedRecommendations(101L, 1);

        // Проверка результатов
        assertEquals(1, recommendations.size());
        assertEquals(102L, recommendations.get(0).getItemId());
    }

    @Test
    public void testNormalizeRatings() {
        // Подготовка тестовых данных
        List<RecommendationDTO> recommendations = Arrays.asList(
                new RecommendationDTO(101L, "Product A", 0.5),
                new RecommendationDTO(102L, "Product B", 0.8)
        );

        // Вызов метода
        List<RecommendationDTO> normalizedRecommendations = recommendationService.normalizeRatings(recommendations);

        // Проверка результатов
        assertEquals(101L, normalizedRecommendations.get(0).getItemId());
        assertEquals("Product A", normalizedRecommendations.get(0).getItemName());
        assertTrue(normalizedRecommendations.get(0).getRating() >= 0.1 && normalizedRecommendations.get(0).getRating() <= 9.9);

        assertEquals(102L, normalizedRecommendations.get(1).getItemId());
        assertEquals("Product B", normalizedRecommendations.get(1).getItemName());
        assertTrue(normalizedRecommendations.get(1).getRating() >= 0.1 && normalizedRecommendations.get(1).getRating() <= 9.9);
    }
}