package ru.alexds.ccoshop.exeption;
/**
 * Класс CartItemNotFoundException представляет собой пользовательское исключение, которое выбрасывается,
 * когда позиция в корзине (CartItem) не может быть найдена в системе.
 * Это исключение наследуется от RuntimeException, что делает его непроверяемым исключением.
 * Таким образом, его можно использовать для обработки ошибок, связанных с отсутствием позиции в корзине.
 */
public class CartItemNotFoundException extends RuntimeException {
    public CartItemNotFoundException(String message) {
        super(message);
    }
}

