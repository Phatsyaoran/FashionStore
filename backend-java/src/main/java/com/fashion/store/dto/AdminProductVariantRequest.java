package com.fashion.store.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminProductVariantRequest {

    private Long id;

    @NotBlank(message = "Size không được để trống")
    private String size;

    @NotBlank(message = "Tên màu không được để trống")
    private String colorName;

    private String colorHex;

    private String sku;

    @NotNull(message = "Giá biến thể không được để trống")
    @DecimalMin(value = "0", message = "Giá biến thể phải >= 0")
    private BigDecimal price;

    @NotNull(message = "Số lượng tồn kho không được để trống")
    @Min(value = 0, message = "Số lượng tồn kho phải >= 0")
    private Integer stockQuantity;
}
