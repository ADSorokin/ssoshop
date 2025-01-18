package ru.alexds.ccoshop;

import com.github.javafaker.Faker;
import ru.alexds.ccoshop.entity.Category;
import ru.alexds.ccoshop.entity.Product;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TestDataGenerator {

    public static List<Product> generateTestProducts(int count, List<Category> categories) {
        Faker faker = new Faker(new Locale("en"));
        List<Product> products = new ArrayList<>();

        for (int i = 1; i <= count; i++) {
            Product product = new Product();
 //           product.setId((long) i); // Генерация id (или можно пропустить, если вы используете @GeneratedValue)
            product.setName(faker.commerce().productName());
            product.setDescription(faker.lorem().sentence(10));
            product.setPrice(BigDecimal.valueOf(faker.number().randomDouble(2, 5, 100))); // Случайная цена от 5 до 100
            product.setStockQuantity(faker.number().numberBetween(1, 100)); // Случайное количество на складе от 1 до 100
            product.setPopularity(faker.number().randomDouble(1, 0, 10)); // Случайная популярность от 0 до 10
//            product.setCategory(faker.options().option(categories)); // Случайная категория из существующих
            product.setImagePath("images/" + i++); // Путь к изображению

            products.add(product);
        }
        return products;
    }

    public static void main(String[] args) {
        // Пример использования
        List<Category> categories = new ArrayList<>(); // Здесь подразумевается, что у вас есть список категорий
        // Добавьте в categories ваши Category объекты

        List<Product> testProducts = generateTestProducts(50, categories);

        // Вы можете вывести продукты или сохранить их в БД
        testProducts.forEach(product -> System.out.println(product));
    }
}
