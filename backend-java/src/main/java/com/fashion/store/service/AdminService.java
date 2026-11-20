package com.fashion.store.service;

import com.fashion.store.dto.*;

import java.util.List;

public interface AdminService {

    DashboardStatsDTO getDashboardStats();

    List<LowStockVariantDTO> getLowStockVariants(int threshold);

    ProductVariantDTO updateVariantStock(Long variantId, StockUpdateRequest request);

    List<OrderResponseDTO> getAllOrders();

    OrderResponseDTO updateOrderStatus(String orderCode, OrderStatusUpdateRequest request);

    ProductDTO toggleProductFlashSale(Long productId);

    void deleteProduct(Long productId);

    byte[] exportOrdersToCsv();

    byte[] exportInventoryToCsv();
}
