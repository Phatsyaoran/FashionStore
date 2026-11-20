package com.fashion.store.controller;

import com.fashion.store.dto.ApiResponse;
import com.fashion.store.dto.CategoryDTO;
import com.fashion.store.service.CatalogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@Tag(name = "1. Categories API", description = "Quản lý và hiển thị danh mục sản phẩm thời trang")
public class CategoryController {

    private final CatalogService catalogService;

    @GetMapping
    @Operation(summary = "Lấy danh sách tất cả danh mục kèm số lượng sản phẩm")
    public ResponseEntity<ApiResponse<List<CategoryDTO>>> getAllCategories() {
        List<CategoryDTO> list = catalogService.getAllCategories();
        return ResponseEntity.ok(ApiResponse.ok("Lấy danh sách danh mục thành công", list));
    }

    @GetMapping("/{slug}")
    @Operation(summary = "Lấy chi tiết danh mục theo Slug")
    public ResponseEntity<ApiResponse<CategoryDTO>> getCategoryBySlug(@PathVariable String slug) {
        CategoryDTO dto = catalogService.getCategoryBySlug(slug);
        return ResponseEntity.ok(ApiResponse.ok("Lấy thông tin danh mục thành công", dto));
    }
}
