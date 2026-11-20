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
public class ProductVariantDTO {

    private Long id;
    private Long productId;
    private String size;
    private String colorName;
    private String colorHex;
    private BigDecimal price;
    private Integer stockQuantity;
    private String sku;
}
