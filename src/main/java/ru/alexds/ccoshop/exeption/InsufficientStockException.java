package ru.alexds.ccoshop.exeption;
/**
 * Метод для добавления позиции в корзину.
 *
 * @throws InsufficientStockException если количество товара на складе недостаточно.
 */
public class InsufficientStockException extends RuntimeException {
    public InsufficientStockException(String message) {
        super(message);
    }
}
