package ru.alexds.ccoshop.exeption;
/**
 * Класс OrderNotFoundException представляет собой пользовательское исключение, которое выбрасывается,
 * когда заказ не может быть найдена в системе.
 * Это исключение наследуется от RuntimeException, что делает его непроверяемым исключением.
 * Таким образом, его можно использовать для обработки ошибок, связанных с отсутствием позиции в корзине.
 */
public class OrderNotFoundException extends RuntimeException {
    public OrderNotFoundException(String message) {
        super(message);
    }
}
