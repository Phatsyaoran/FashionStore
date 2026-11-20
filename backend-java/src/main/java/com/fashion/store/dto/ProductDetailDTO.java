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
public class ProductDetailDTO {

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
    private List<String> galleryImages;
    private Double ratingAverage;
    private Integer reviewCount;
    private Integer totalStock;
    private LocalDateTime createdAt;
    private List<ProductVariantDTO> variants;
    private List<ReviewDTO> reviews;
}
