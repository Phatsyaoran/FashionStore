package com.fashion.store.controller;

import com.fashion.store.dto.ApiResponse;
import com.fashion.store.dto.OrderCreateRequest;
import com.fashion.store.dto.OrderResponseDTO;
import com.fashion.store.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Tag(name = "5. Orders API", description = "Tạo đơn hàng @Transactional, kiểm tra tồn kho, VietQR và tra cứu đơn")
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @Operation(summary = "Tạo mới đơn hàng (@Transactional, trừ tồn kho an toàn, tính phí ship và sinh link VietQR)")
    public ResponseEntity<ApiResponse<OrderResponseDTO>> createOrder(
            @Valid @RequestBody OrderCreateRequest request,
            Authentication authentication
    ) {
        String email = (authentication != null) ? authentication.getName() : null;
        OrderResponseDTO created = orderService.createOrder(request, email);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Đặt hàng thành công", created));
    }

    @GetMapping("/my-orders")
    @Operation(summary = "Lấy danh sách đơn hàng của tài khoản đang đăng nhập (Yêu cầu JWT)")
    public ResponseEntity<ApiResponse<List<OrderResponseDTO>>> getMyOrders(Authentication authentication) {
        List<OrderResponseDTO> orders = orderService.getMyOrders(authentication.getName());
        return ResponseEntity.ok(ApiResponse.ok("Lấy danh sách đơn hàng của bạn thành công", orders));
    }

    @PostMapping("/{orderCode}/cancel")
    @Operation(summary = "Khách hàng tự hủy đơn hàng đang ở trạng thái PENDING (Tự hoàn tồn kho)")
    public ResponseEntity<ApiResponse<OrderResponseDTO>> cancelMyOrder(
            @PathVariable String orderCode,
            Authentication authentication
    ) {
        OrderResponseDTO cancelled = orderService.cancelMyOrder(orderCode, authentication.getName());
        return ResponseEntity.ok(ApiResponse.ok("Đã hủy đơn hàng và hoàn lại tồn kho thành công", cancelled));
    }

    @GetMapping("/{orderCodeOrPhone}")
    @Operation(summary = "Tra cứu đơn hàng theo Mã đơn hoặc Số điện thoại")
    public ResponseEntity<ApiResponse<Object>> getOrder(@PathVariable String orderCodeOrPhone) {
        String query = orderCodeOrPhone.trim();
        if (query.toUpperCase().startsWith("DH")) {
            OrderResponseDTO order = orderService.getOrderByCode(query);
            return ResponseEntity.ok(ApiResponse.ok("Tìm thấy đơn hàng", order));
        } else {
            List<OrderResponseDTO> orders = orderService.getOrdersByPhone(query);
            return ResponseEntity.ok(ApiResponse.ok("Tìm thấy danh sách đơn hàng", orders));
        }
    }

    @PostMapping("/{orderCode}/confirm-payment")
    @Operation(summary = "Xác nhận thanh toán ('Tôi đã chuyển khoản') cập nhật trạng thái đơn")
    public ResponseEntity<ApiResponse<OrderResponseDTO>> confirmPayment(@PathVariable String orderCode) {
        OrderResponseDTO updated = orderService.confirmPayment(orderCode);
        return ResponseEntity.ok(ApiResponse.ok("Xác nhận thanh toán thành công", updated));
    }
}
