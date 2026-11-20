package com.fashion.store.controller;

import com.fashion.store.dto.*;
import com.fashion.store.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Tag(name = "6. Admin API", description = "Quản trị thống kê, cảnh báo tồn kho, cập nhật số lượng và duyệt đơn hàng")
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/dashboard/stats")
    @Operation(summary = "Lấy thống kê tổng quan doanh thu, số lượng đơn theo trạng thái và tổng sản phẩm")
    public ResponseEntity<ApiResponse<DashboardStatsDTO>> getDashboardStats() {
        DashboardStatsDTO stats = adminService.getDashboardStats();
        return ResponseEntity.ok(ApiResponse.ok("Lấy dữ liệu thống kê thành công", stats));
    }

    @GetMapping("/stats/revenue")
    @Operation(summary = "Thống kê doanh thu và số đơn theo từng ngày (7 ngày hoặc 30 ngày, loại trừ đơn CANCELLED)")
    public ResponseEntity<ApiResponse<List<DailyRevenueDTO>>> getRevenueStats(
            @RequestParam(defaultValue = "7") int days
    ) {
        List<DailyRevenueDTO> stats = adminService.getRevenueStats(days);
        return ResponseEntity.ok(ApiResponse.ok("Lấy dữ liệu biểu đồ doanh thu thành công", stats));
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload ảnh sản phẩm trực tiếp lên máy chủ (/uploads/...)")
    public ResponseEntity<ApiResponse<Map<String, String>>> uploadImage(@RequestParam("file") MultipartFile file) {
        String url = adminService.uploadImage(file);
        return ResponseEntity.ok(ApiResponse.ok("Tải ảnh lên thành công", Map.of("url", url)));
    }

    @PostMapping("/products")
    @Operation(summary = "Tạo mới sản phẩm kèm danh sách biến thể Size/Màu")
    public ResponseEntity<ApiResponse<ProductDTO>> createProduct(@Valid @RequestBody AdminProductRequest request) {
        ProductDTO created = adminService.createProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Tạo sản phẩm mới thành công", created));
    }

    @PutMapping("/products/{id}")
    @Operation(summary = "Cập nhật thông tin sản phẩm và đồng bộ danh sách biến thể")
    public ResponseEntity<ApiResponse<ProductDTO>> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody AdminProductRequest request
    ) {
        ProductDTO updated = adminService.updateProduct(id, request);
        return ResponseEntity.ok(ApiResponse.ok("Cập nhật sản phẩm thành công", updated));
    }

    @GetMapping("/dashboard/low-stock")
    @Operation(summary = "Lấy danh sách các biến thể sắp hết hàng (mặc định tồn kho <= 5 chiếc)")
    public ResponseEntity<ApiResponse<List<LowStockVariantDTO>>> getLowStockVariants(
            @RequestParam(defaultValue = "5") int threshold
    ) {
        List<LowStockVariantDTO> list = adminService.getLowStockVariants(threshold);
        return ResponseEntity.ok(ApiResponse.ok("Lấy danh sách cảnh báo tồn kho thành công", list));
    }

    @PatchMapping("/variants/{id}/stock")
    @Operation(summary = "Sửa nhanh số lượng tồn kho của một biến thể ngay tại bảng cảnh báo")
    public ResponseEntity<ApiResponse<ProductVariantDTO>> updateVariantStock(
            @PathVariable Long id,
            @Valid @RequestBody StockUpdateRequest request
    ) {
        ProductVariantDTO updated = adminService.updateVariantStock(id, request);
        return ResponseEntity.ok(ApiResponse.ok("Cập nhật số lượng tồn kho thành công", updated));
    }

    @GetMapping("/orders")
    @Operation(summary = "Tìm kiếm, lọc trạng thái và phân trang đơn hàng Admin (tránh N+1 query)")
    public ResponseEntity<ApiResponse<PageResponse<OrderResponseDTO>>> getAllOrders(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        PageResponse<OrderResponseDTO> orders = adminService.getAdminOrders(keyword, status, page, size);
        return ResponseEntity.ok(ApiResponse.ok("Lấy danh sách đơn hàng thành công", orders));
    }

    // ==========================================
    // ADMIN COUPON CRUD
    // ==========================================

    @GetMapping("/coupons")
    @Operation(summary = "Lấy danh sách toàn bộ mã giảm giá")
    public ResponseEntity<ApiResponse<List<AdminCouponDTO>>> getAllCoupons() {
        return ResponseEntity.ok(ApiResponse.ok("Lấy danh sách mã giảm giá thành công", adminService.getAllCoupons()));
    }

    @PostMapping("/coupons")
    @Operation(summary = "Tạo mã giảm giá mới")
    public ResponseEntity<ApiResponse<AdminCouponDTO>> createCoupon(@Valid @RequestBody AdminCouponRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Tạo mã giảm giá thành công", adminService.createCoupon(request)));
    }

    @PutMapping("/coupons/{id}")
    @Operation(summary = "Cập nhật thông tin mã giảm giá")
    public ResponseEntity<ApiResponse<AdminCouponDTO>> updateCoupon(
            @PathVariable Long id,
            @Valid @RequestBody AdminCouponRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.ok("Cập nhật mã giảm giá thành công", adminService.updateCoupon(id, request)));
    }

    @PatchMapping("/coupons/{id}/toggle")
    @Operation(summary = "Bật/tắt kích hoạt mã giảm giá")
    public ResponseEntity<ApiResponse<AdminCouponDTO>> toggleCoupon(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok("Cập nhật trạng thái mã giảm giá thành công", adminService.toggleCoupon(id)));
    }

    // ==========================================
    // ADMIN CATEGORY CRUD
    // ==========================================

    @GetMapping("/categories")
    @Operation(summary = "Lấy danh sách toàn bộ danh mục kèm số lượng sản phẩm")
    public ResponseEntity<ApiResponse<List<CategoryDTO>>> getAllCategories() {
        return ResponseEntity.ok(ApiResponse.ok("Lấy danh sách danh mục thành công", adminService.getAllCategories()));
    }

    @PostMapping("/categories")
    @Operation(summary = "Tạo danh mục sản phẩm mới")
    public ResponseEntity<ApiResponse<CategoryDTO>> createCategory(@Valid @RequestBody AdminCategoryRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Tạo danh mục thành công", adminService.createCategory(request)));
    }

    @PutMapping("/categories/{id}")
    @Operation(summary = "Cập nhật thông tin danh mục sản phẩm")
    public ResponseEntity<ApiResponse<CategoryDTO>> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody AdminCategoryRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.ok("Cập nhật danh mục thành công", adminService.updateCategory(id, request)));
    }

    @DeleteMapping("/categories/{id}")
    @Operation(summary = "Xóa danh mục sản phẩm (Chặn xóa nếu đang có sản phẩm)")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(@PathVariable Long id) {
        adminService.deleteCategory(id);
        return ResponseEntity.ok(ApiResponse.ok("Đã xóa danh mục thành công", null));
    }

    @PatchMapping("/orders/{orderCode}/status")
    @Operation(summary = "Cập nhật trạng thái giao vận đơn hàng (PENDING, CONFIRMED, PACKING, SHIPPING, COMPLETED, CANCELLED)")
    public ResponseEntity<ApiResponse<OrderResponseDTO>> updateOrderStatus(
            @PathVariable String orderCode,
            @Valid @RequestBody OrderStatusUpdateRequest request
    ) {
        OrderResponseDTO updated = adminService.updateOrderStatus(orderCode, request);
        return ResponseEntity.ok(ApiResponse.ok("Cập nhật trạng thái đơn hàng thành công", updated));
    }

    @PatchMapping("/products/{id}/quick-toggle-flashsale")
    @Operation(summary = "Bật/Tắt nhanh trạng thái Flash Sale của sản phẩm")
    public ResponseEntity<ApiResponse<ProductDTO>> quickToggleFlashSale(@PathVariable Long id) {
        ProductDTO updated = adminService.toggleProductFlashSale(id);
        return ResponseEntity.ok(ApiResponse.ok("Cập nhật trạng thái Flash Sale thành công", updated));
    }

    @DeleteMapping("/products/{id}")
    @Operation(summary = "Xóa an toàn sản phẩm (Kiểm tra ràng buộc đơn hàng)")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable Long id) {
        adminService.deleteProduct(id);
        return ResponseEntity.ok(ApiResponse.ok("Đã xóa sản phẩm thành công", null));
    }

    @GetMapping(value = "/export/orders-csv", produces = "text/csv; charset=UTF-8")
    @Operation(summary = "Xuất danh sách đơn hàng ra file Excel (.CSV) chuẩn tiếng Việt UTF-8 BOM")
    public ResponseEntity<byte[]> exportOrdersCsv() {
        byte[] csvBytes = adminService.exportOrdersToCsv();
        String filename = "don-hang-" + DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss").format(LocalDateTime.now()) + ".csv";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .header(HttpHeaders.CONTENT_TYPE, "text/csv; charset=UTF-8")
                .body(csvBytes);
    }

    @GetMapping(value = "/export/inventory-csv", produces = "text/csv; charset=UTF-8")
    @Operation(summary = "Xuất danh sách tồn kho toàn bộ biến thể ra file Excel (.CSV) chuẩn tiếng Việt UTF-8 BOM")
    public ResponseEntity<byte[]> exportInventoryCsv() {
        byte[] csvBytes = adminService.exportInventoryToCsv();
        String filename = "ton-kho-" + DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss").format(LocalDateTime.now()) + ".csv";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .header(HttpHeaders.CONTENT_TYPE, "text/csv; charset=UTF-8")
                .body(csvBytes);
    }
}
