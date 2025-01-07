package ru.alexds.ccoshop.entity;

import lombok.AllArgsConstructor;
import lombok.Data;


import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.Map;

@Data
@AllArgsConstructor
public class OrderStatistics {
    private BigDecimal totalSpent;
    private BigDecimal averageOrderAmount;
    private long totalOrders;
    private BigDecimal maxOrderAmount;
    private BigDecimal minOrderAmount;
    private Map<YearMonth, BigDecimal> monthlySpending;
}
