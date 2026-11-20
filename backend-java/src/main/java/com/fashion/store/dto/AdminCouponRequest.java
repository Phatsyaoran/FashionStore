package com.fashion.store.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminCouponRequest {

    @NotBlank(message = "Mã giảm giá không được để trống")
    private String code;

    @NotNull(message = "Phần trăm giảm không được để trống")
    @Min(value = 1, message = "Phần trăm giảm phải từ 1% đến 100%")
    @Max(value = 100, message = "Phần trăm giảm phải từ 1% đến 100%")
    private Integer discountPercent;

    @NotNull(message = "Mức giảm tối đa không được để trống")
    @DecimalMin(value = "1", message = "Mức giảm tối đa phải lớn hơn 0")
    private BigDecimal maxDiscountAmount;

    @NotNull(message = "Giá trị đơn tối thiểu không được để trống")
    @DecimalMin(value = "0", message = "Giá trị đơn tối thiểu phải >= 0")
    private BigDecimal minOrderValue;

    @NotNull(message = "Ngày hết hạn không được để trống")
    private LocalDateTime expiryDate;

    @NotNull(message = "Giới hạn lượt dùng không được để trống")
    @Min(value = 1, message = "Giới hạn lượt dùng phải >= 1")
    private Integer usageLimit;

    private Boolean isActive;
}
