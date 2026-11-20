package com.fashion.store.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CouponDTO {

    private Long id;
    private String code;
    private Integer discountPercent;
    private BigDecimal maxDiscountAmount;
    private BigDecimal minOrderValue;
    private BigDecimal calculatedDiscount;
}
