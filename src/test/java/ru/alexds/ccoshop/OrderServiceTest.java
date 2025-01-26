package ru.alexds.ccoshop;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.alexds.ccoshop.dto.CartItemDTO;
import ru.alexds.ccoshop.dto.OrderDTO;
import ru.alexds.ccoshop.dto.RatingDTO;
import ru.alexds.ccoshop.entity.*;
import ru.alexds.ccoshop.exeption.OrderNotFoundException;
import ru.alexds.ccoshop.repository.OrderRepository;
import ru.alexds.ccoshop.service.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private UserService userService;

    @Mock
    private ProductService productService;

    @Mock
    private CartService cartService;

    @Mock
    private RatingService ratingService;

    @InjectMocks
    private OrderService orderService;

    private User testUser;
    private Product testProduct;
    private List<CartItemDTO> testCartItems;
    private List<OrderItem> testOrderItems;
    private Order testOrder;

    @BeforeEach
    public void setUp() {
        testUser = new User();
        testUser.setId(1L);

        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setPrice(new BigDecimal("100.00"));
        testProduct.setStockQuantity(10);

        testCartItems = Arrays.asList(CartItemDTO.builder().productId(1L).quantity(2).price(new BigDecimal("100.00")).build());

        testOrderItems = Arrays.asList(OrderItem.builder().product(testProduct).quantity(2).price(new BigDecimal("100.00")).build());

        testOrder = new Order();
        testOrder.setId(1L);
        testOrder.setUser(testUser);
        testOrder.setStatus(Status.NEW);
        testOrder.setOrderDate(LocalDateTime.now());
        testOrder.setItems(testOrderItems);
        testOrder.setTotalPrice(new BigDecimal("200.00"));
    }

    @Test
    public void testCreateOrder() {
        when(userService.getUserEntityById(1L)).thenReturn(Optional.of(testUser));
        when(productService.getProductEntityById(1L)).thenReturn(Optional.of(testProduct));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            order.setId(1L);
            return order;
        });

        OrderDTO result = orderService.createOrder(1L, 1L, 2);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(orderRepository, times(1)).save(any(Order.class));
        verify(productService, times(1)).updateProduct(1L, any());
    }

    @Test
    public void testCreateOrder_UserNotFound() {
        when(userService.getUserEntityById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> orderService.createOrder(1L, 1L, 2));
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    public void testCreateOrder_ProductNotFound() {
        when(userService.getUserEntityById(1L)).thenReturn(Optional.of(testUser));
        when(productService.getProductEntityById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> orderService.createOrder(1L, 1L, 2));
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    public void testCreateOrder_InsufficientStock() {
        testProduct.setStockQuantity(1);
        when(userService.getUserEntityById(1L)).thenReturn(Optional.of(testUser));
        when(productService.getProductEntityById(1L)).thenReturn(Optional.of(testProduct));

        assertThrows(RuntimeException.class, () -> orderService.createOrder(1L, 1L, 2));
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    public void testGetOrdersByUserId() {
        when(orderRepository.findByUserId(1L)).thenReturn(Arrays.asList(testOrder));

        List<OrderDTO> orders = orderService.getOrdersByUserId(1L);

        assertNotNull(orders);
        assertEquals(1, orders.size());
        verify(orderRepository, times(1)).findByUserId(1L);
    }

    @Test
    public void testGetOrderById_Found() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));

        Optional<OrderDTO> orderOptional = orderService.getOrderById(1L);

        assertTrue(orderOptional.isPresent());
        assertEquals(1L, orderOptional.get().getId());
        verify(orderRepository, times(1)).findById(1L);
    }

    @Test
    public void testGetOrderById_NotFound() {
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<OrderDTO> orderOptional = orderService.getOrderById(1L);

        assertFalse(orderOptional.isPresent());
        verify(orderRepository, times(1)).findById(1L);
    }

    @Test
    public void testUpdateOrderStatus() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));

        OrderDTO updatedOrderDTO = orderService.updateOrderStatus(1L, Status.COMPLETED);

        assertEquals(Status.COMPLETED, updatedOrderDTO.getStatus());
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    public void testUpdateOrderStatus_OrderNotFound() {
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> orderService.updateOrderStatus(1L, Status.COMPLETED));
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    public void testCancelOrder() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));

        OrderDTO cancelledOrderDTO = orderService.cancelOrder(1L);

        assertEquals(Status.CANCELLED, cancelledOrderDTO.getStatus());
        verify(orderRepository, times(1)).save(any(Order.class));
        for (OrderItem orderItem : testOrder.getItems()) {
            verify(productService, times(1)).updateProduct(orderItem.getProduct().getId(), any());
        }
    }

    @Test
    public void testCancelOrder_CompletedOrder() {
        testOrder.setStatus(Status.COMPLETED);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));

        assertThrows(RuntimeException.class, () -> orderService.cancelOrder(1L));
        verify(orderRepository, never()).save(any(Order.class));
        verify(productService, never()).updateProduct(anyLong(), any());
    }

    @Test
    public void testGetPurchasedProducts() {
        when(orderRepository.findCompletedOrdersByUserId(1L)).thenReturn(Arrays.asList(testOrder));

        List<Product> products = orderService.getPurchasedProducts(1L);

        assertNotNull(products);
        assertEquals(1, products.size());
        verify(orderRepository, times(1)).findCompletedOrdersByUserId(1L);
    }

    @Test
    public void testGetPurchasedProducts_NoOrders() {
        when(orderRepository.findCompletedOrdersByUserId(1L)).thenReturn(Collections.emptyList());

        List<Product> products = orderService.getPurchasedProducts(1L);

        assertNotNull(products);
        assertTrue(products.isEmpty());
        verify(orderRepository, times(1)).findCompletedOrdersByUserId(1L);
    }

    @Test
    public void testCalculateUserTotalSpent() {
        when(orderRepository.findByUserIdAndStatus(1L, Status.COMPLETED)).thenReturn(Arrays.asList(testOrder));

        BigDecimal totalSpent = orderService.calculateUserTotalSpent(1L);

        assertNotNull(totalSpent);
        assertEquals(new BigDecimal("200.00"), totalSpent);
        verify(orderRepository, times(1)).findByUserIdAndStatus(1L, Status.COMPLETED);
    }

    @Test
    public void testCalculateUserAverageOrderAmount() {
        when(orderRepository.findByUserIdAndStatus(1L, Status.COMPLETED)).thenReturn(Arrays.asList(testOrder));

        BigDecimal averageOrderAmount = orderService.calculateUserAverageOrderAmount(1L);

        assertNotNull(averageOrderAmount);
        assertEquals(new BigDecimal("200.00"), averageOrderAmount);
        verify(orderRepository, times(1)).findByUserIdAndStatus(1L, Status.COMPLETED);
    }

    @Test
    public void testGetOrdersAboveAmount() {
        when(orderRepository.findByUserIdAndStatus(1L, Status.COMPLETED)).thenReturn(Arrays.asList(testOrder));

        List<OrderDTO> orders = orderService.getOrdersAboveAmount(1L, new BigDecimal("100.00"));

        assertNotNull(orders);
        assertEquals(1, orders.size());
        verify(orderRepository, times(1)).findByUserIdAndStatus(1L, Status.COMPLETED);
    }

    @Test
    public void testCalculateMonthlySpending() {
        when(orderRepository.findByUserIdAndStatus(1L, Status.COMPLETED)).thenReturn(Arrays.asList(testOrder));

        Map<YearMonth, BigDecimal> monthlySpending = orderService.calculateMonthlySpending(1L);

        assertNotNull(monthlySpending);
        YearMonth yearMonth = YearMonth.from(testOrder.getOrderDate());
        assertTrue(monthlySpending.containsKey(yearMonth));
        assertEquals(new BigDecimal("200.00"), monthlySpending.get(yearMonth));
        verify(orderRepository, times(1)).findByUserIdAndStatus(1L, Status.COMPLETED);
    }

    @Test
    public void testCreateOrderFromCart() {
        when(cartService.getCartItemsForUser(1L)).thenReturn(testCartItems);
        when(userService.getUserEntityById(1L)).thenReturn(Optional.of(testUser));
        when(productService.getProductEntityById(1L)).thenReturn(Optional.of(testProduct));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            order.setId(1L);
            return order;
        });
        when(ratingService.saveRating(any(RatingDTO.class))).thenReturn(new Rating());

        OrderDTO result = orderService.createOrderFromCart(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(orderRepository, times(1)).save(any(Order.class));
        verify(cartService, times(1)).clearCartForUser(1L);
        verify(ratingService, times(1)).saveRating(any(RatingDTO.class));
    }

    @Test
    public void testCreateOrderFromCart_CartEmpty() {
        when(cartService.getCartItemsForUser(1L)).thenReturn(Collections.emptyList());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> orderService.createOrderFromCart(1L));

        assertEquals("Cart is empty, cannot create order", exception.getMessage());
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    public void testDeleteOrder() {
        doNothing().when(orderRepository).deleteById(1L);
        when(orderRepository.existsById(1L)).thenReturn(true);

        orderService.deleteOrder(1L);

        verify(orderRepository, times(1)).deleteById(1L);
    }

    @Test
    public void testDeleteOrder_OrderNotFound() {
        when(orderRepository.existsById(1L)).thenReturn(false);

        OrderNotFoundException exception = assertThrows(OrderNotFoundException.class, () -> orderService.deleteOrder(1L));

        assertEquals("Order not found with ID: 1", exception.getMessage());
        verify(orderRepository, never()).deleteById(1L);
    }
}
