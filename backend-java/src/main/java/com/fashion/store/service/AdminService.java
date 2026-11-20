package com.fashion.store.service;

import com.fashion.store.dto.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface AdminService {

    DashboardStatsDTO getDashboardStats();

    List<DailyRevenueDTO> getRevenueStats(int days);

    List<LowStockVariantDTO> getLowStockVariants(int threshold);

    ProductVariantDTO updateVariantStock(Long variantId, StockUpdateRequest request);

    List<OrderResponseDTO> getAllOrders();

    OrderResponseDTO updateOrderStatus(String orderCode, OrderStatusUpdateRequest request);

    ProductDTO toggleProductFlashSale(Long productId);

    ProductDTO createProduct(AdminProductRequest request);

    ProductDTO updateProduct(Long productId, AdminProductRequest request);

    String uploadImage(MultipartFile file);

    void deleteProduct(Long productId);

    byte[] exportOrdersToCsv();

    byte[] exportInventoryToCsv();
}
