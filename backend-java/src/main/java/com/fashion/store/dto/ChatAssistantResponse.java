package com.fashion.store.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatAssistantResponse {

    private String reply;
    private String intent; // "SIZE_ADVICE", "PRICE_SEARCH", "ORDER_TRACKING", "COUPON_LIST", "GREETING", "GENERAL"
    private String suggestedSize;
    private List<ProductDTO> suggestedProducts;
    private OrderResponseDTO orderInfo;
    private List<CouponDTO> activeCoupons;
    private List<String> quickReplies;
}
