/**
 * Пакет для утилитных классов приложения.
 */
package ru.alexds.ccoshop.utilites;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Класс PriceUtils предоставляет набор статических методов для сравнения ценовых значений типа BigDecimal с значениями типа Double.
 * Эти методы позволяют выполнять различные операции сравнения, такие как больше, меньше, равно и другие, с учетом точности и масштаба.
 * Все методы работают с объектами BigDecimal и значениями типа Double, что делает их универсальными для использования в различных частях приложения.
 */
public class PriceUtils {

    /**
     * Метод для проверки, является ли значение value1 больше значения value2.
     *
     * @param value1 Первое значение типа BigDecimal.
     * @param value2 Второе значение типа Double.
     * @return true, если value1 больше value2, иначе false.
     */
    public static boolean isGreaterThan(BigDecimal value1, Double value2) {
        return value1.compareTo(BigDecimal.valueOf(value2)) > 0;
    }

    /**
     * Метод для проверки, является ли значение value1 больше или равно значению value2.
     *
     * @param value1 Первое значение типа BigDecimal.
     * @param value2 Второе значение типа Double.
     * @return true, если value1 больше или равно value2, иначе false.
     */
    public static boolean isGreaterOrEqual(BigDecimal value1, Double value2) {
        return value1.compareTo(BigDecimal.valueOf(value2)) >= 0;
    }

    /**
     * Метод для проверки, является ли значение value1 меньше значения value2.
     *
     * @param value1 Первое значение типа BigDecimal.
     * @param value2 Второе значение типа Double.
     * @return true, если value1 меньше value2, иначе false.
     */
    public static boolean isLessThan(BigDecimal value1, Double value2) {
        return value1.compareTo(BigDecimal.valueOf(value2)) < 0;
    }

    /**
     * Метод для проверки, является ли значение value1 меньше или равно значению value2.
     *
     * @param value1 Первое значение типа BigDecimal.
     * @param value2 Второе значение типа Double.
     * @return true, если value1 меньше или равно value2, иначе false.
     */
    public static boolean isLessOrEqual(BigDecimal value1, Double value2) {
        return value1.compareTo(BigDecimal.valueOf(value2)) <= 0;
    }

    /**
     * Метод для проверки, равны ли значения value1 и value2.
     *
     * @param value1 Первое значение типа BigDecimal.
     * @param value2 Второе значение типа Double.
     * @return true, если value1 равно value2, иначе false.
     */
    public static boolean isEqual(BigDecimal value1, Double value2) {
        return value1.compareTo(BigDecimal.valueOf(value2)) == 0;
    }

    /**
     * Метод для проверки, равны ли значения value1 и value2 с учетом заданного масштаба (количества знаков после запятой).
     *
     * @param value1 Первое значение типа BigDecimal.
     * @param value2 Второе значение типа Double.
     * @param scale  Масштаб (количество знаков после запятой), с которым будет производиться сравнение.
     * @return true, если value1 равно value2 с учетом заданного масштаба, иначе false.
     */
    public static boolean isEqualWithPrecision(BigDecimal value1, Double value2, int scale) {
        return value1.setScale(scale, RoundingMode.HALF_UP)
                .equals(BigDecimal.valueOf(value2).setScale(scale, RoundingMode.HALF_UP));
    }
}