package ru.alexds.ccoshop;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.alexds.ccoshop.dto.*;
import ru.alexds.ccoshop.entity.Category;
import ru.alexds.ccoshop.entity.Product;
import ru.alexds.ccoshop.entity.Role;
import ru.alexds.ccoshop.repository.CategoryRepository;
import ru.alexds.ccoshop.repository.ProductRepository;
import ru.alexds.ccoshop.repository.UserRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest // Запуск полного контекста приложения для интеграционных тестов
@AutoConfigureMockMvc // Автоматическая настройка MockMvc для выполнения HTTP-запросов
public class ShopIntegrationTest {

    @Autowired
    private MockMvc mockMvc; // Для выполнения HTTP-запросов

    @Autowired
    private UserRepository userRepository; // Репозиторий для работы с пользователями

    @Autowired
    private ProductRepository productRepository; // Репозиторий для работы с продуктами

    @Autowired
    private CategoryRepository categoryRepository; // Репозиторий для работы с категориями

    @Autowired
    private ObjectMapper objectMapper; // Для сериализации/десериализации объектов в JSON

    @BeforeEach
    public void setUp() {
        // Очистка и подготовка данных перед каждым тестом
        userRepository.deleteAll();
        productRepository.deleteAll();
        categoryRepository.deleteAll();

        // Создаем тестовую категорию
        Category testCategory = new Category();
        testCategory.setName("TestCategory");
        categoryRepository.save(testCategory);

        // Создаем тестовый продукт
        Product testProduct = new Product();
        testProduct.setName("TestProduct");
        testProduct.setDescription("Description of TestProduct");
        testProduct.setPrice(new BigDecimal("100.00"));
        testProduct.setStockQuantity(10);
        testProduct.setPopularity(4.5);
        testProduct.setCategory(testCategory);
        productRepository.save(testProduct);
    }

    @Test
    public void testCreateUserAndAddToCart() throws Exception {
        if (objectMapper == null || mockMvc == null) {
            throw new IllegalStateException("ObjectMapper or MockMvc is not initialized");
        }

        // Тестовые данные
        UserCreateDTO userCreateDTO = new UserCreateDTO("testuser@example.com", Role.USER, true);

        // Создаем пользователя
        MvcResult createUserResult = mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userCreateDTO)))
                .andExpect(status().isOk())
                .andReturn();

        UserDTO createdUser = objectMapper.readValue(createUserResult.getResponse().getContentAsString(), UserDTO.class);

        // Получаем продукт из базы данных
        List<Product> products = productRepository.findAll();
        Product testProduct = products.get(0);

        // Добавляем продукт в корзину пользователя
        CartItemDTO cartItemDTO = new CartItemDTO(null, createdUser.getId(), testProduct.getId(), 2, testProduct.getPrice());

        mockMvc.perform(MockMvcRequestBuilders.post("/cart-items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cartItemDTO)))
                .andExpect(status().isOk());

        // Проверяем, что продукт был добавлен в корзину
        mockMvc.perform(MockMvcRequestBuilders.get("/cart-items/user/" + createdUser.getId()))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.size()").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].productId").value(testProduct.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].quantity").value(2));
    }

    @Test
    public void testCreateOrder() throws Exception {
        if (objectMapper == null || mockMvc == null) {
            throw new IllegalStateException("ObjectMapper or MockMvc is not initialized");
        }

        // Тестовые данные
        UserCreateDTO userCreateDTO = new UserCreateDTO("testuser@example.com", Role.USER, true);

        // Создаем пользователя
        MvcResult createUserResult = mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userCreateDTO)))
                .andExpect(status().isOk())
                .andReturn();

        UserDTO createdUser = objectMapper.readValue(createUserResult.getResponse().getContentAsString(), UserDTO.class);

        // Получаем продукт из базы данных
        List<Product> products = productRepository.findAll();
        Product testProduct = products.get(0);

        // Добавляем продукт в корзину пользователя
        CartItemDTO cartItemDTO = new CartItemDTO(null, createdUser.getId(), testProduct.getId(), 2, testProduct.getPrice());

        mockMvc.perform(MockMvcRequestBuilders.post("/cart-items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cartItemDTO)))
                .andExpect(status().isOk());

        // Создаем заказ
        OrderDTO orderDTO = new OrderDTO(null, createdUser.getId(), null, LocalDateTime.now(), null, BigDecimal.ZERO);

        MvcResult createOrderResult = mockMvc.perform(MockMvcRequestBuilders.post("/orders/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderDTO)))
                .andExpect(status().isOk())
                .andReturn();

        OrderDTO createdOrder = objectMapper.readValue(createOrderResult.getResponse().getContentAsString(), OrderDTO.class);

        // Проверяем, что заказ был создан
        mockMvc.perform(MockMvcRequestBuilders.get("/orders/" + createdOrder.getId()))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.userId").value(createdUser.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.items.size()").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].productId").value(testProduct.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].quantity").value(2));
    }
}