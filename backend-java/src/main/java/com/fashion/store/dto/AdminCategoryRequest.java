package com.fashion.store.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminCategoryRequest {

    @NotBlank(message = "Tên danh mục không được để trống")
    private String name;

    private String slug;

    private String description;
}
