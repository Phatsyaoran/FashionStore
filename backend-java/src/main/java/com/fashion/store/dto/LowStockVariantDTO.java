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
public class LowStockVariantDTO {

    private Long variantId;
    private Long productId;
    private String productName;
    private String productSlug;
    private String thumbnailUrl;
    private String size;
    private String colorName;
    private String colorHex;
    private String sku;
    private Integer stockQuantity;
    private BigDecimal price;
}
