package com.fashion.store.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDTO {

    private Long id;
    private Long categoryId;
    private String categoryName;
    private String categorySlug;
    private String name;
    private String slug;
    private String description;
    private BigDecimal basePrice;
    private BigDecimal salePrice;
    private Integer discountPercent;
    private Boolean isFlashSale;
    private LocalDateTime flashSaleEndTime;
    private String thumbnailUrl;
    private List<String> availableSizes;
    private List<String> availableColors;
    private List<String> availableColorHexes;
    private Integer totalStock;
    private Double ratingAverage;
    private Integer reviewCount;
    private LocalDateTime createdAt;
}
