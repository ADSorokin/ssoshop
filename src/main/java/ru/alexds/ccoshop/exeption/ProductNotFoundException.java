package ru.alexds.ccoshop.exeption;
/**
 * Класс ProductNotFoundException представляет собой пользовательское исключение, которое выбрасывается,
 * когда продукт не может быть найдена в системе.
 * Это исключение наследуется от RuntimeException, что делает его непроверяемым исключением.
 * Таким образом, его можно использовать для обработки ошибок, связанных с отсутствием позиции в корзине.
 */
public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException(String message) {
        super(message);
    }
}

