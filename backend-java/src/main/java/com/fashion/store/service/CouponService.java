package com.fashion.store.service;

import com.fashion.store.dto.CouponApplyRequest;
import com.fashion.store.dto.CouponDTO;

public interface CouponService {

    CouponDTO applyCoupon(CouponApplyRequest request);
}
