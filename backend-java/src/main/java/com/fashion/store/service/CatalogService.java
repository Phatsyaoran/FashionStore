package com.fashion.store.service;

import com.fashion.store.dto.*;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

public interface CatalogService {

    List<CategoryDTO> getAllCategories();

    CategoryDTO getCategoryBySlug(String slug);

    PageResponse<ProductDTO> getProducts(
            Long categoryId,
            String categorySlug,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            String size,
            String color,
            Boolean isFlashSale,
            String keyword,
            Pageable pageable
    );

    ProductDetailDTO getProductDetailBySlug(String slug);

    ProductDetailDTO getProductDetailById(Long id);

    List<ProductDTO> getFlashSaleProducts();

    List<ProductDTO> getLatestProducts(int limit);

    List<ProductDTO> getRelatedProducts(Long productId, int limit);

    List<ProductDetailDTO> getMixMatchProducts(Long productId, int limit);

    List<ReviewDTO> getProductReviews(Long productId);

    ProductReviewsResponse getProductReviewsResponse(Long productId);

    ReviewDTO addReview(Long productId, ReviewCreateRequest request);

    ReviewDTO addReview(Long productId, ReviewCreateRequest request, String userEmail);
}
