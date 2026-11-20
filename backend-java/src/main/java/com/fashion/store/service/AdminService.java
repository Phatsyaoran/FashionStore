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

    PageResponse<OrderResponseDTO> getAdminOrders(String keyword, String status, int page, int size);

    OrderResponseDTO updateOrderStatus(String orderCode, OrderStatusUpdateRequest request);

    ProductDTO toggleProductFlashSale(Long productId);

    ProductDTO createProduct(AdminProductRequest request);

    ProductDTO updateProduct(Long productId, AdminProductRequest request);

    String uploadImage(MultipartFile file);

    void deleteProduct(Long productId);

    List<AdminCouponDTO> getAllCoupons();

    AdminCouponDTO createCoupon(AdminCouponRequest request);

    AdminCouponDTO updateCoupon(Long id, AdminCouponRequest request);

    AdminCouponDTO toggleCoupon(Long id);

    List<CategoryDTO> getAllCategories();

    CategoryDTO createCategory(AdminCategoryRequest request);

    CategoryDTO updateCategory(Long id, AdminCategoryRequest request);

    void deleteCategory(Long id);

    byte[] exportOrdersToCsv();

    byte[] exportInventoryToCsv();
}
