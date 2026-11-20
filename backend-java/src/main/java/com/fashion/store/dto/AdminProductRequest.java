package com.fashion.store.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminProductRequest {

    @NotBlank(message = "Tên sản phẩm không được để trống")
    private String name;

    private String slug;

    @NotNull(message = "Danh mục không được để trống")
    private Long categoryId;

    private String description;

    @NotNull(message = "Giá gốc không được để trống")
    @DecimalMin(value = "0", message = "Giá gốc phải >= 0")
    private BigDecimal basePrice;

    @DecimalMin(value = "0", message = "Giá khuyến mãi phải >= 0")
    private BigDecimal salePrice;

    private Boolean isFlashSale;

    private String thumbnailUrl;

    @Valid
    @NotEmpty(message = "Sản phẩm phải có ít nhất 1 biến thể (Size/Màu)")
    private List<AdminProductVariantRequest> variants;
}
