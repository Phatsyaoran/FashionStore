package com.fashion.store.controller;

import com.fashion.store.dto.ApiResponse;
import com.fashion.store.dto.CouponApplyRequest;
import com.fashion.store.dto.CouponDTO;
import com.fashion.store.service.CouponService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/coupons")
@RequiredArgsConstructor
@Tag(name = "4. Coupons API", description = "Kiểm tra và áp dụng mã giảm giá")
public class CouponController {

    private final CouponService couponService;

    @PostMapping("/apply")
    @Operation(summary = "Kiểm tra và tính toán giảm giá của coupon theo giá trị đơn hàng")
    public ResponseEntity<ApiResponse<CouponDTO>> applyCoupon(@Valid @RequestBody CouponApplyRequest request) {
        CouponDTO dto = couponService.applyCoupon(request);
        return ResponseEntity.ok(ApiResponse.ok("Áp dụng mã giảm giá thành công", dto));
    }
}
