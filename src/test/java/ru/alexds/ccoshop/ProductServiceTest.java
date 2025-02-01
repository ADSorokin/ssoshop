package ru.alexds.ccoshop;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import ru.alexds.ccoshop.dto.ProductDTO;
import ru.alexds.ccoshop.entity.Category;
import ru.alexds.ccoshop.entity.Product;
import ru.alexds.ccoshop.repository.CategoryRepository;
import ru.alexds.ccoshop.repository.ProductRepository;
import ru.alexds.ccoshop.service.ProductService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private ProductService productService;

    private Product testProduct;
    private Category testCategory;
    private ProductDTO testProductDTO;

    @BeforeEach
    public void setUp() {
        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setName("TestProduct");
        testProduct.setDescription("Description of TestProduct");
        testProduct.setPrice(new BigDecimal("100.00"));
        testProduct.setStockQuantity(10);
        testProduct.setPopularity(4.5);

        testCategory = new Category();
        testCategory.setId(1L);
        testCategory.setName("TestCategory");

        testProduct.setCategory(testCategory);

        testProductDTO = new ProductDTO();
        testProductDTO.setId(1L);
        testProductDTO.setName("TestProduct");
        testProductDTO.setDescription("Description of TestProduct");
        testProductDTO.setPrice(new BigDecimal("100.00"));
        testProductDTO.setStockQuantity(10);
        testProductDTO.setCategoryId(1L);
        testProductDTO.setPopularity(4.5);
    }

    @Test
    public void testGetAllProducts() {
        when(productRepository.findAll()).thenReturn(Collections.singletonList(testProduct));

        List<ProductDTO> products = productService.getAllProducts();

        assertNotNull(products);
        assertEquals(1, products.size());
        verify(productRepository, times(1)).findAll();
    }

    @Test
    public void testGetProductById_Found() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));

        Optional<ProductDTO> productOptional = productService.getProductById(1L);

        assertTrue(productOptional.isPresent());
        assertEquals(testProduct.getId(), productOptional.get().getId());
        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    public void testGetProductById_NotFound() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<ProductDTO> productOptional = productService.getProductById(1L);

        assertFalse(productOptional.isPresent());
        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    public void testCreateProduct() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> {
            Product product = invocation.getArgument(0);
            product.setId(1L); // Имитация сохранения продукта с присвоением ID
            return product;
        });

        ProductDTO createdProduct = productService.createProduct(testProductDTO);

        assertNotNull(createdProduct);
        assertEquals(1L, createdProduct.getId());
        verify(productRepository, times(1)).save(any(Product.class));
        verify(categoryRepository, times(1)).findById(1L);
    }

    @Test
    public void testCreateProduct_CategoryNotFound() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> productService.createProduct(testProductDTO));

        assertEquals("Category not found", exception.getMessage());
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    public void testUpdateProduct() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));

        testProductDTO.setName("UpdatedProductName");
        ProductDTO updatedProduct = productService.updateProduct(1L, testProductDTO);

        assertEquals("UpdatedProductName", updatedProduct.getName());
         verify(productRepository, times(1)).save(any(Product.class));
        verify(categoryRepository, times(1)).findById(1L);
    }

    @Test
    public void testUpdateProduct_ProductNotFound() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> productService.updateProduct(1L, testProductDTO));

        assertEquals("Product not found", exception.getMessage());
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    public void testUpdateProduct_CategoryNotFound() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> productService.updateProduct(1L, testProductDTO));

        assertEquals("Category not found", exception.getMessage());
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    public void testGetPopularProducts() {
        when(productRepository.findAllByOrderByPopularityDescCreatedAtDesc(PageRequest.of(0, 5)))
                .thenReturn(Collections.singletonList(testProduct));

        List<ProductDTO> popularProducts = productService.getPopularProducts();

        assertNotNull(popularProducts);
        assertEquals(1, popularProducts.size());
        verify(productRepository, times(1)).findAllByOrderByPopularityDescCreatedAtDesc(PageRequest.of(0, 5));
    }

    @Test
    public void testGetProductsByPriceRange() {
        when(productRepository.findByPriceBetweenOrderByPriceAsc(any(BigDecimal.class), any(BigDecimal.class)))
                .thenReturn(Collections.singletonList(testProduct));

        List<ProductDTO> products = productService.getProductsByPriceRange(new BigDecimal("50"), new BigDecimal("200"));

        assertNotNull(products);
        assertEquals(1, products.size());
        verify(productRepository, times(1)).findByPriceBetweenOrderByPriceAsc(any(BigDecimal.class), any(BigDecimal.class));
    }

    @Test
    public void testGetProductsByCategory() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));
        when(productRepository.findByCategoryOrderByCreatedAtDesc(testCategory))
                .thenReturn(Collections.singletonList(testProduct));

        List<ProductDTO> products = productService.getProductsByCategory(1L);

        assertNotNull(products);
        assertEquals(1, products.size());
        verify(productRepository, times(1)).findByCategoryOrderByCreatedAtDesc(testCategory);
    }

    @Test
    public void testGetProductsByCategory_CategoryNotFound() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> productService.getProductsByCategory(1L));

        assertEquals("Category not found with id: 1", exception.getMessage());
        verify(productRepository, never()).findByCategoryOrderByCreatedAtDesc(any(Category.class));
    }

    @Test
    public void testSearchProductsByName() {
        when(productRepository.findByNameContainingIgnoreCaseOrderByPopularityDesc("Test"))
                .thenReturn(Collections.singletonList(testProduct));

        List<ProductDTO> products = productService.searchProductsByName("Test");

        assertNotNull(products);
        assertEquals(1, products.size());
        verify(productRepository, times(1)).findByNameContainingIgnoreCaseOrderByPopularityDesc("Test");
    }

    @Test
    public void testSearchProductsByName_BlankName() {
        List<ProductDTO> products = productService.searchProductsByName(" ");

        assertNotNull(products);
        assertTrue(products.isEmpty());
        verify(productRepository, never()).findByNameContainingIgnoreCaseOrderByPopularityDesc(anyString());
    }

    @Test
    public void testDeleteProduct_Success() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        doNothing().when(productRepository).delete(testProduct);

        productService.deleteProduct(1L);

        verify(productRepository, times(1)).delete(testProduct);
    }

    @Test
    public void testDeleteProduct_ProductNotFound() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> productService.deleteProduct(1L));

        assertEquals("Product not found with id: 1", exception.getMessage());
        verify(productRepository, never()).delete(any(Product.class));
    }

    @Test
    public void testDeleteProduct_ActiveOrders() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        doReturn(true).when(productService).hasActiveOrders(testProduct);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> productService.deleteProduct(1L));

        assertEquals("Cannot delete product with active orders", exception.getMessage());
        verify(productRepository, never()).delete(any(Product.class));
    }
}
