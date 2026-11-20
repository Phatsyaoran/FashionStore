package com.fashion.store.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardStatsDTO {

    private BigDecimal totalRevenue;
    private Long totalOrders;
    private Long pendingOrders;
    private Long shippingOrders;
    private Long completedOrders;
    private Long cancelledOrders;
    private Long totalProducts;
    private Long lowStockCount;
    private Map<String, Long> ordersByStatus;
    private Map<String, BigDecimal> revenueByStatus;
}
