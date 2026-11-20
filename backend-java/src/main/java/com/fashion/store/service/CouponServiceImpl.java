package com.fashion.store.service;

import com.fashion.store.dto.CouponApplyRequest;
import com.fashion.store.dto.CouponDTO;
import com.fashion.store.entity.Coupon;
import com.fashion.store.exception.BadRequestException;
import com.fashion.store.repository.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CouponServiceImpl implements CouponService {

    private final CouponRepository couponRepository;

    @Override
    @Transactional(readOnly = true)
    public CouponDTO applyCoupon(CouponApplyRequest request) {
        String code = request.getCode().trim().toUpperCase();
        Coupon coupon = couponRepository.findByCodeAndIsActiveTrue(code)
                .orElseThrow(() -> new BadRequestException("Mã giảm giá '" + code + "' không tồn tại hoặc đã hết hạn"));

        if (coupon.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Mã giảm giá '" + code + "' đã hết hạn sử dụng");
        }

        if (coupon.getUsedCount() >= coupon.getUsageLimit()) {
            throw new BadRequestException("Mã giảm giá '" + code + "' đã hết số lượt sử dụng");
        }

        BigDecimal orderVal = request.getOrderValue();
        if (orderVal.compareTo(coupon.getMinOrderValue()) < 0) {
            throw new BadRequestException("Đơn hàng phải có giá trị tối thiểu " + coupon.getMinOrderValue().intValue() + " VNĐ để áp dụng mã này");
        }

        // Tính số tiền giảm: (orderValue * discountPercent) / 100
        BigDecimal discount = orderVal.multiply(BigDecimal.valueOf(coupon.getDiscountPercent()))
                .divide(BigDecimal.valueOf(100), 0, RoundingMode.HALF_UP);

        if (discount.compareTo(coupon.getMaxDiscountAmount()) > 0) {
            discount = coupon.getMaxDiscountAmount();
        }

        return CouponDTO.builder()
                .id(coupon.getId())
                .code(coupon.getCode())
                .discountPercent(coupon.getDiscountPercent())
                .maxDiscountAmount(coupon.getMaxDiscountAmount())
                .minOrderValue(coupon.getMinOrderValue())
                .calculatedDiscount(discount)
                .build();
    }
}
