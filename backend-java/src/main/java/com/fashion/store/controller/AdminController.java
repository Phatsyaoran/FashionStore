package com.fashion.store.controller;

import com.fashion.store.dto.*;
import com.fashion.store.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

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
    @Operation(summary = "Lấy danh sách toàn bộ đơn hàng trong hệ thống")
    public ResponseEntity<ApiResponse<List<OrderResponseDTO>>> getAllOrders() {
        List<OrderResponseDTO> orders = adminService.getAllOrders();
        return ResponseEntity.ok(ApiResponse.ok("Lấy danh sách đơn hàng thành công", orders));
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
