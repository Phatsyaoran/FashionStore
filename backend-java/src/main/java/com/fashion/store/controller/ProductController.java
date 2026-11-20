package com.fashion.store.controller;

import com.fashion.store.dto.*;
import com.fashion.store.service.CatalogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Tag(name = "2. Products API", description = "Tìm kiếm, lọc đa tiêu chí, xem chi tiết và đánh giá sản phẩm")
public class ProductController {

    private final CatalogService catalogService;

    @GetMapping
    @Operation(summary = "Lọc, tìm kiếm và phân trang sản phẩm theo đa tiêu chí")
    public ResponseEntity<ApiResponse<PageResponse<ProductDTO>>> getProducts(
            @Parameter(description = "ID danh mục") @RequestParam(required = false) Long categoryId,
            @Parameter(description = "Slug danh mục") @RequestParam(required = false) String categorySlug,
            @Parameter(description = "Giá tối thiểu") @RequestParam(required = false) BigDecimal minPrice,
            @Parameter(description = "Giá tối đa") @RequestParam(required = false) BigDecimal maxPrice,
            @Parameter(description = "Kích cỡ (S, M, L, XL)") @RequestParam(required = false) String size,
            @Parameter(description = "Màu sắc") @RequestParam(required = false) String color,
            @Parameter(description = "Chỉ lấy sản phẩm Flash Sale") @RequestParam(required = false) Boolean isFlashSale,
            @Parameter(description = "Từ khóa tìm kiếm theo tên hoặc mô tả") @RequestParam(required = false) String keyword,
            @Parameter(description = "Số trang (bắt đầu từ 0)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Số lượng sản phẩm mỗi trang") @RequestParam(defaultValue = "12") int pageSize,
            @Parameter(description = "Sắp xếp: latest, price_asc, price_desc, sale") @RequestParam(defaultValue = "latest") String sort
    ) {
        // Tránh nhầm lẫn giữa tham số clothing size (S, M, L) và pageSize (ví dụ ?size=50 từ client phân trang)
        if (size != null && size.matches("^\\d+$") && pageSize == 12) {
            pageSize = Integer.parseInt(size);
            size = null;
        }

        Sort sorting;
        switch (sort) {
            case "price_asc":
                sorting = Sort.by(Sort.Direction.ASC, "basePrice");
                break;
            case "price_desc":
                sorting = Sort.by(Sort.Direction.DESC, "basePrice");
                break;
            case "sale":
                sorting = Sort.by(Sort.Direction.DESC, "isFlashSale").and(Sort.by(Sort.Direction.DESC, "createdAt"));
                break;
            case "latest":
            default:
                sorting = Sort.by(Sort.Direction.DESC, "createdAt");
                break;
        }

        Pageable pageable = PageRequest.of(Math.max(0, page), Math.max(1, pageSize), sorting);
        PageResponse<ProductDTO> response = catalogService.getProducts(
                categoryId, categorySlug, minPrice, maxPrice, size, color, isFlashSale, keyword, pageable
        );
        return ResponseEntity.ok(ApiResponse.ok("Lấy danh sách sản phẩm thành công", response));
    }

    @GetMapping("/{slug}")
    @Operation(summary = "Lấy chi tiết sản phẩm kèm danh sách biến thể theo Slug")
    public ResponseEntity<ApiResponse<ProductDetailDTO>> getProductDetailBySlug(@PathVariable String slug) {
        ProductDetailDTO dto = catalogService.getProductDetailBySlug(slug);
        return ResponseEntity.ok(ApiResponse.ok("Lấy chi tiết sản phẩm thành công", dto));
    }

    @GetMapping("/id/{id}")
    @Operation(summary = "Lấy chi tiết sản phẩm kèm danh sách biến thể theo ID")
    public ResponseEntity<ApiResponse<ProductDetailDTO>> getProductDetailById(@PathVariable Long id) {
        ProductDetailDTO dto = catalogService.getProductDetailById(id);
        return ResponseEntity.ok(ApiResponse.ok("Lấy chi tiết sản phẩm thành công", dto));
    }

    @GetMapping("/flash-sale")
    @Operation(summary = "Lấy danh sách sản phẩm đang Flash Sale còn hiệu lực")
    public ResponseEntity<ApiResponse<List<ProductDTO>>> getFlashSaleProducts() {
        List<ProductDTO> list = catalogService.getFlashSaleProducts();
        return ResponseEntity.ok(ApiResponse.ok("Lấy sản phẩm Flash Sale thành công", list));
    }

    @GetMapping("/latest")
    @Operation(summary = "Lấy danh sách sản phẩm mới nhất")
    public ResponseEntity<ApiResponse<List<ProductDTO>>> getLatestProducts(
            @RequestParam(defaultValue = "8") int limit
    ) {
        List<ProductDTO> list = catalogService.getLatestProducts(limit);
        return ResponseEntity.ok(ApiResponse.ok("Lấy sản phẩm mới nhất thành công", list));
    }

    @GetMapping("/{productId}/related")
    @Operation(summary = "Lấy danh sách sản phẩm cùng danh mục liên quan")
    public ResponseEntity<ApiResponse<List<ProductDTO>>> getRelatedProducts(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "4") int limit
    ) {
        List<ProductDTO> list = catalogService.getRelatedProducts(productId, limit);
        return ResponseEntity.ok(ApiResponse.ok("Lấy sản phẩm liên quan thành công", list));
    }

    @GetMapping("/{productId}/mix-match")
    @Operation(summary = "Lấy danh sách sản phẩm gợi ý phối đồ (Mix & Match) thuộc danh mục khác")
    public ResponseEntity<ApiResponse<List<ProductDetailDTO>>> getMixMatchProducts(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "2") int limit
    ) {
        List<ProductDetailDTO> list = catalogService.getMixMatchProducts(productId, limit);
        return ResponseEntity.ok(ApiResponse.ok("Lấy sản phẩm phối đồ chuẩn gu thành công", list));
    }

    @GetMapping("/{productId}/reviews")
    @Operation(summary = "Lấy danh sách đánh giá của sản phẩm kèm điểm trung bình và tỷ lệ cảm nhận vóc dáng")
    public ResponseEntity<ApiResponse<ProductReviewsResponse>> getProductReviews(@PathVariable Long productId) {
        ProductReviewsResponse response = catalogService.getProductReviewsResponse(productId);
        return ResponseEntity.ok(ApiResponse.ok("Lấy đánh giá sản phẩm thành công", response));
    }

    @PostMapping("/{productId}/reviews")
    @Operation(summary = "Gửi đánh giá và phản hồi số đo cho sản phẩm")
    public ResponseEntity<ApiResponse<ReviewDTO>> addReview(
            @PathVariable Long productId,
            @Valid @RequestBody ReviewCreateRequest request
    ) {
        ReviewDTO created = catalogService.addReview(productId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Đánh giá sản phẩm đã được ghi nhận", created));
    }
}
