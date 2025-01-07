package ru.alexds.ccoshop.utilites;

import org.springframework.context.annotation.Bean;

import java.math.BigDecimal;
import java.math.RoundingMode;


public class PriceUtils {
    public static boolean isGreaterThan(BigDecimal value1, Double value2) {
        return value1.compareTo(BigDecimal.valueOf(value2)) > 0;
    }

    public static boolean isGreaterOrEqual(BigDecimal value1, Double value2) {
        return value1.compareTo(BigDecimal.valueOf(value2)) >= 0;
    }

    public static boolean isLessThan(BigDecimal value1, Double value2) {
        return value1.compareTo(BigDecimal.valueOf(value2)) < 0;
    }

    public static boolean isLessOrEqual(BigDecimal value1, Double value2) {
        return value1.compareTo(BigDecimal.valueOf(value2)) <= 0;
    }

    public static boolean isEqual(BigDecimal value1, Double value2) {
        return value1.compareTo(BigDecimal.valueOf(value2)) == 0;
    }

    public static boolean isEqualWithPrecision(BigDecimal value1, Double value2, int scale) {
        return value1.setScale(scale, RoundingMode.HALF_UP)
                .equals(BigDecimal.valueOf(value2).setScale(scale, RoundingMode.HALF_UP));
    }
}
