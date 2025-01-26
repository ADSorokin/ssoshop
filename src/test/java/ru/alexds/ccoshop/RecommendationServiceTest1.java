//package ru.alexds.ccoshop;
//
//
//
//import org.apache.mahout.cf.taste.common.TasteException;
//import org.apache.mahout.cf.taste.impl.model.jdbc.MySQLJDBCDataModel;
//import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
//import org.apache.mahout.cf.taste.impl.recommender.GenericItemBasedRecommender;
//import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
//import org.apache.mahout.cf.taste.impl.similarity.EuclideanDistanceSimilarity;
//
//import org.apache.mahout.cf.taste.similarity.ItemSimilarity;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import ru.alexds.ccoshop.dto.RecommendationDTO;
//import ru.alexds.ccoshop.entity.ARTClusterEntity;
//import ru.alexds.ccoshop.entity.Product;
//import ru.alexds.ccoshop.entity.Rating;
//import ru.alexds.ccoshop.repository.RatingRepository;
//import ru.alexds.ccoshop.service.ARTClusterService;
//import ru.alexds.ccoshop.service.ProductService;
//import ru.alexds.ccoshop.service.RecommendationService;
//
//import javax.sql.DataSource;
//import java.util.*;
//import java.util.stream.Collectors;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//public class RecommendationServiceTest1 {
//
//    @Mock
//    private DataSource dataSource;
//
//    @Mock
//    private RatingRepository ratingRepository;
//
//    @Mock
//    private ARTClusterService artClusterService;
//
//    @Mock
//    private ProductService productService;
//
//    @InjectMocks
//    private RecommendationService recommendationService;
//
//    private List<Rating> testRatings;
//    private List<ARTClusterEntity> testArtClusters;
//    private List<Product> testProducts;
//
//    @BeforeEach
//    public void setUp() {
//        // Инициализация тестовых данных
//        testRatings = Arrays.asList(
//                createRating(1L, 1L, 4.0),
//                createRating(1L, 2L, 3.5),
//                createRating(2L, 1L, 4.5)
//        );
//
//        testArtClusters = Arrays.asList(
//                createARTCluster(1L, new double[]{0.8, 0.6}, Collections.singletonList(1L)),
//                createARTCluster(2L, new double[]{0.7, 0.9}, Collections.singletonList(2L))
//        );
//
//        testProducts = Arrays.asList(
//                createProduct(1L, "Product 1"),
//                createProduct(2L, "Product 2")
//        );
//    }
//
//    private Rating createRating(Long userId, Long itemId, Double rating) {
//        Rating r = new Rating();
//        r.setUserId(userId);
//        r.setItemId(itemId);
//        r.setRating(rating);
//        return r;
//    }
//
//    private ARTClusterEntity createARTCluster(Long id, double[] weights, List<Long> userIds) {
//        ARTClusterEntity cluster = new ARTClusterEntity();
//        cluster.setId(id);
//        cluster.setWeights(Arrays.stream(weights).boxed().collect(Collectors.toList()));
//        cluster.setUserIds(userIds);
//        return cluster;
//    }
//
//    private Product createProduct(Long id, String name) {
//        Product p = new Product();
//        p.setId(id);
//        p.setName(name);
//        return p;
//    }
//
//    @Test
//    public void testGetUserBasedRecommendations() throws TasteException {
//        when(recommendationService.ratingRepository.findByUserId(anyLong())).thenReturn(testRatings);
//        when(recommendationService.productService.getProductById(anyLong()))
//                .thenReturn(Optional.of(testProducts.get(0)))
//                .thenReturn(Optional.of(testProducts.get(1)));
//
//        MySQLJDBCDataModel dataModel = mock(MySQLJDBCDataModel.class);
//        EuclideanDistanceSimilarity similarity = mock(EuclideanDistanceSimilarity.class);
//        NearestNUserNeighborhood neighborhood = mock(NearestNUserNeighborhood.class);
//
//        GenericUserBasedRecommender recommender = mock(GenericUserBasedRecommender.class);
//        when(recommender.recommend(anyLong(), anyInt())).thenReturn(Collections.singletonList(mock(org.apache.mahout.cf.taste.recommender.RecommendedItem.class)));
//
//        recommendationService.dataModel = dataModel;
//        recommendationService.getUserBasedRecommendations(1L, 5);
//
//        verify(recommender, times(1)).recommend(anyLong(), anyInt());
//    }
//
//    @Test
//    public void testGetItemBasedRecommendations() throws TasteException {
//        ItemSimilarity itemSimilarity = mock(ItemSimilarity.class);
//        GenericItemBasedRecommender recommender = mock(GenericItemBasedRecommender.class);
//        when(recommender.mostSimilarItems(anyLong(), anyInt())).thenReturn(Collections.singletonList(mock(org.apache.mahout.cf.taste.recommender.RecommendedItem.class)));
//
//        recommendationService.getItemBasedRecommendations(1L, 5);
//
//        verify(recommender, times(1)).mostSimilarItems(anyLong(), anyInt());
//    }
//
//    @Test
//    public void testGetARTRecommendations_NoSuitableCluster() {
//        when(artClusterService.getAllClusters()).thenReturn(testArtClusters);
//        when(ratingRepository.findByUserId(anyLong())).thenReturn(testRatings);
//        when(productService.getProductById(anyLong()))
//                .thenReturn(Optional.of(testProducts.get(0)))
//                .thenReturn(Optional.of(testProducts.get(1)));
//
//        List<RecommendationDTO> recommendations = recommendationService.getARTRecommendations(1L);
//
//        assertEquals(1, recommendations.size());
//        assertTrue(recommendations.get(0).getRating() >= 0 && recommendations.get(0).getRating() <= 1);
//    }
//
//    @Test
//    public void testGetARTRecommendations_ExistingCluster() {
//        ARTClusterEntity suitableCluster = createARTCluster(1L, new double[]{0.8, 0.6}, Collections.singletonList(1L));
//        when(artClusterService.getAllClusters()).thenReturn(Collections.singletonList(suitableCluster));
//        when(ratingRepository.findByUserId(anyLong())).thenReturn(testRatings);
//        when(productService.getProductById(anyLong()))
//                .thenReturn(Optional.of(testProducts.get(0)))
//                .thenReturn(Optional.of(testProducts.get(1)));
//
//        List<RecommendationDTO> recommendations = recommendationService.getARTRecommendations(1L);
//
//        assertEquals(1, recommendations.size());
//        assertTrue(recommendations.get(0).getRating() >= 0 && recommendations.get(0).getRating() <= 1);
//    }
//
//    @Test
//    public void testCalculateSimilarityScore() {
//        when(ratingRepository.findByItemId(anyLong())).thenReturn(testRatings);
//        when(ratingRepository.findByUserId(anyLong())).thenReturn(testRatings);
//
//        double similarityScore = recommendationService.calculateSimilarityScore(1L, Collections.singletonList(1L));
//
//        assertNotNull(similarityScore);
//        assertTrue(similarityScore >= 0 && similarityScore <= 1);
//    }
//
//    @Test
//    public void testCreateUserVector() {
//        when(ratingRepository.findByUserId(anyLong())).thenReturn(testRatings);
//        when(ratingRepository.findMaxItemId()).thenReturn(2L);
//
//        double[] userVector = recommendationService.createUserVector(1L);
//
//        assertNotNull(userVector);
//        assertEquals(2, userVector.length);
//        assertEquals(1.0, userVector[0]);
//        assertEquals(1.0, userVector[1]);
//    }
//
//    @Test
//    public void testMergeRecommendations() {
//        List<RecommendationDTO> userBasedRecs = Arrays.asList(
//                new RecommendationDTO(1L, "Product 1", 0.8),
//                new RecommendationDTO(2L, "Product 2", 0.6)
//        );
//
//        List<RecommendationDTO> artBasedRecs = Arrays.asList(
//                new RecommendationDTO(1L, "Product 1", 0.9),
//                new RecommendationDTO(3L, "Product 3", 0.7)
//        );
//
//        List<RecommendationDTO> mergedRecommendations = recommendationService.mergeRecommendations(userBasedRecs, artBasedRecs, 3);
//
//        assertNotNull(mergedRecommendations);
//        assertEquals(3, mergedRecommendations.size());
//        assertEquals("Product 1", mergedRecommendations.get(0).getItemName());
//        assertEquals("Product 3", mergedRecommendations.get(1).getItemName());
//        assertEquals("Product 2", mergedRecommendations.get(2).getItemName());
//    }
//
//    @Test
//    public void testNormalizeRatings() {
//        List<RecommendationDTO> recommendations = Arrays.asList(
//                new RecommendationDTO(1L, "Product 1", 0.2),
//                new RecommendationDTO(2L, "Product 2", 0.8)
//        );
//
//        List<RecommendationDTO> normalizedRecommendations = recommendationService.normalizeRatings(recommendations);
//
//        assertNotNull(normalizedRecommendations);
//        assertEquals(2, normalizedRecommendations.size());
//        assertTrue(normalizedRecommendations.get(0).getRating() >= 0.1 && normalizedRecommendations.get(0).getRating() <= 9.9);
//        assertTrue(normalizedRecommendations.get(1).getRating() >= 0.1 && normalizedRecommendations.get(1).getRating() <= 9.9);
//    }
//
//    @Test
//    public void testMatchCluster() {
//        ARTClusterEntity cluster = createARTCluster(1L, new double[]{0.8, 0.6}, Collections.singletonList(1L));
//        double[] userVector = {0.7, 0.5};
//
//        double matchValue = recommendationService.matchCluster(cluster, userVector);
//
//        assertNotNull(matchValue);
//        assertTrue(matchValue >= 0 && matchValue <= 1);
//    }
//
//    @Test
//    public void testAdaptClusterWeights() {
//        ARTClusterEntity cluster = createARTCluster(1L, new double[]{0.8, 0.6}, Collections.singletonList(1L));
//        double[] userVector = {0.7, 0.5};
//
//        recommendationService.adaptClusterWeights(cluster, userVector);
//
//        assertEquals(2, cluster.getWeights().size());
//        assertTrue(cluster.getWeights().get(0) > 0.7); // Проверяем обновление весов
//        assertTrue(cluster.getWeights().get(1) > 0.5);
//    }
//}
