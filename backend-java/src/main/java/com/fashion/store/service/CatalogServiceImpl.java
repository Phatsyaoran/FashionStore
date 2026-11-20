package com.fashion.store.service;

import com.fashion.store.dto.*;
import com.fashion.store.entity.Category;
import com.fashion.store.entity.Product;
import com.fashion.store.entity.ProductVariant;
import com.fashion.store.entity.Review;
import com.fashion.store.entity.User;
import com.fashion.store.exception.ResourceNotFoundException;
import com.fashion.store.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CatalogServiceImpl implements CatalogService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final ProductVariantRepository productVariantRepository;
    private final ReviewRepository reviewRepository;
    private final OrderItemRepository orderItemRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public List<CategoryDTO> getAllCategories() {
        List<Category> categories = categoryRepository.findAll();
        Map<Long, Long> countsMap = new HashMap<>();

        List<Object[]> rawCounts = categoryRepository.countProductsByCategory();
        for (Object[] row : rawCounts) {
            Long catId = (Long) row[0];
            Long count = (Long) row[1];
            countsMap.put(catId, count);
        }

        return categories.stream().map(cat -> CategoryDTO.builder()
                .id(cat.getId())
                .name(cat.getName())
                .slug(cat.getSlug())
                .description(cat.getDescription())
                .productCount(countsMap.getOrDefault(cat.getId(), 0L))
                .build()
        ).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryDTO getCategoryBySlug(String slug) {
        Category category = categoryRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy danh mục với slug: " + slug));
        long count = productRepository.count((root, query, cb) -> cb.equal(root.get("category").get("id"), category.getId()));
        return CategoryDTO.builder()
                .id(category.getId())
                .name(category.getName())
                .slug(category.getSlug())
                .description(category.getDescription())
                .productCount(count)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ProductDTO> getProducts(
            Long categoryId,
            String categorySlug,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            String size,
            String color,
            Boolean isFlashSale,
            String keyword,
            Pageable pageable
    ) {
        Specification<Product> spec = ProductSpecification.filterProducts(
                categoryId,
                categorySlug,
                minPrice,
                maxPrice,
                size,
                color,
                isFlashSale,
                keyword
        );

        Page<Product> productPage = productRepository.findAll(spec, pageable);
        List<ProductDTO> dtos = productPage.getContent().stream()
                .map(this::mapToProductDTO)
                .collect(Collectors.toList());

        return PageResponse.<ProductDTO>builder()
                .content(dtos)
                .pageNo(productPage.getNumber())
                .pageSize(productPage.getSize())
                .totalElements(productPage.getTotalElements())
                .totalPages(productPage.getTotalPages())
                .isLast(productPage.isLast())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public ProductDetailDTO getProductDetailBySlug(String slug) {
        Product product = productRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm với slug: " + slug));
        return mapToProductDetailDTO(product);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductDetailDTO getProductDetailById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm với ID: " + id));
        return mapToProductDetailDTO(product);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductDTO> getFlashSaleProducts() {
        List<Product> flashSales = productRepository.findActiveFlashSales(LocalDateTime.now());
        return flashSales.stream()
                .map(this::mapToProductDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductDTO> getLatestProducts(int limit) {
        Pageable pageable = PageRequest.of(0, Math.max(1, limit));
        List<Product> latest = productRepository.findLatestProducts(pageable);
        return latest.stream()
                .map(this::mapToProductDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductDTO> getRelatedProducts(Long productId, int limit) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm"));
        Pageable pageable = PageRequest.of(0, Math.max(1, limit));
        List<Product> related = productRepository.findRelatedProducts(product.getCategory().getId(), productId, pageable);
        return related.stream()
                .map(this::mapToProductDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductDetailDTO> getMixMatchProducts(Long productId, int limit) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm với ID: " + productId));
        Pageable pageable = PageRequest.of(0, Math.max(1, limit));
        Long categoryId = product.getCategory() != null ? product.getCategory().getId() : 0L;
        List<Product> matches = productRepository.findMixMatchProducts(categoryId, productId, pageable);
        return matches.stream()
                .map(this::mapToProductDetailDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReviewDTO> getProductReviews(Long productId) {
        if (!productRepository.existsById(productId)) {
            throw new ResourceNotFoundException("Không tìm thấy sản phẩm với ID: " + productId);
        }
        List<Review> reviews = reviewRepository.findByProductIdOrderByCreatedAtDesc(productId);
        return reviews.stream().map(this::mapToReviewDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ProductReviewsResponse getProductReviewsResponse(Long productId) {
        if (!productRepository.existsById(productId)) {
            throw new ResourceNotFoundException("Không tìm thấy sản phẩm với ID: " + productId);
        }

        List<Review> reviews = reviewRepository.findByProductIdOrderByCreatedAtDesc(productId);
        int total = reviews.size();

        Double rawAvg = reviewRepository.getAverageRating(productId);
        double avg = (rawAvg != null) ? Math.round(rawAvg * 10.0) / 10.0 : 0.0;

        int tightCount = 0;
        int fitCount = 0;
        int looseCount = 0;

        Map<Integer, Long> starCounts = new HashMap<>();
        for (int i = 1; i <= 5; i++) {
            starCounts.put(i, 0L);
        }

        for (Review r : reviews) {
            int rating = (r.getRating() != null && r.getRating() >= 1 && r.getRating() <= 5) ? r.getRating() : 5;
            starCounts.put(rating, starCounts.get(rating) + 1);

            if ("CHAT".equalsIgnoreCase(r.getFitFeedback())) {
                tightCount++;
            } else if ("VUA_VAN".equalsIgnoreCase(r.getFitFeedback())) {
                fitCount++;
            } else if ("RONG".equalsIgnoreCase(r.getFitFeedback())) {
                looseCount++;
            }
        }

        int fitTotal = tightCount + fitCount + looseCount;
        int tightPct = (fitTotal > 0) ? Math.round((tightCount * 100.0f) / fitTotal) : 0;
        int fitPct = (fitTotal > 0) ? Math.round((fitCount * 100.0f) / fitTotal) : 100;
        int loosePct = (fitTotal > 0) ? Math.round((looseCount * 100.0f) / fitTotal) : 0;

        List<ReviewDTO> reviewDTOs = reviews.stream()
                .map(this::mapToReviewDTO)
                .collect(Collectors.toList());

        return ProductReviewsResponse.builder()
                .ratingAverage(avg)
                .totalReviews(total)
                .tightPercent(tightPct)
                .fitPercent(fitPct)
                .loosePercent(loosePct)
                .starCounts(starCounts)
                .reviews(reviewDTOs)
                .build();
    }

    @Override
    @Transactional
    public ReviewDTO addReview(Long productId, ReviewCreateRequest request) {
        return addReview(productId, request, null);
    }

    @Override
    @Transactional
    public ReviewDTO addReview(Long productId, ReviewCreateRequest request, String userEmail) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm với ID: " + productId));

        User user = null;
        if (userEmail != null && !userEmail.isBlank()) {
            user = userRepository.findByEmail(userEmail.trim().toLowerCase()).orElse(null);
        }

        String customerName = request.getCustomerName();
        if (customerName == null || customerName.trim().isEmpty()) {
            customerName = (user != null && user.getFullName() != null) ? user.getFullName() : "Khách hàng ẩn danh";
        }

        Review review = Review.builder()
                .product(product)
                .user(user)
                .customerName(customerName.trim())
                .rating(request.getRating())
                .comment(request.getComment().trim())
                .customerHeightCm(request.getCustomerHeightCm())
                .customerWeightKg(request.getCustomerWeightKg())
                .purchasedSize(request.getPurchasedSize())
                .purchasedColor(request.getPurchasedColor())
                .fitFeedback(request.getFitFeedback())
                .createdAt(LocalDateTime.now())
                .build();

        Review saved = reviewRepository.save(review);
        return mapToReviewDTO(saved);
    }

    // ==========================================
    // HELPER MAPPING METHODS (DTO ISOLATION)
    // ==========================================

    private ProductDTO mapToProductDTO(Product product) {
        List<ProductVariant> variants = productVariantRepository.findByProductId(product.getId());

        List<String> sizes = variants.stream()
                .map(ProductVariant::getSize)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());

        List<String> colors = variants.stream()
                .map(ProductVariant::getColorName)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());

        List<String> colorHexes = variants.stream()
                .map(ProductVariant::getColorHex)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());

        int totalStock = variants.stream()
                .mapToInt(v -> v.getStockQuantity() != null ? v.getStockQuantity() : 0)
                .sum();

        Double rawAvg = reviewRepository.getAverageRating(product.getId());
        double avgRating = rawAvg != null ? Math.round(rawAvg * 10.0) / 10.0 : 0.0;
        int reviewCount = (int) reviewRepository.countByProductId(product.getId());

        Integer discountPercent = calculateDiscountPercent(product.getBasePrice(), product.getSalePrice());

        return ProductDTO.builder()
                .id(product.getId())
                .categoryId(product.getCategory() != null ? product.getCategory().getId() : null)
                .categoryName(product.getCategory() != null ? product.getCategory().getName() : "")
                .categorySlug(product.getCategory() != null ? product.getCategory().getSlug() : "")
                .name(product.getName())
                .slug(product.getSlug())
                .description(product.getDescription())
                .basePrice(product.getBasePrice())
                .salePrice(product.getSalePrice())
                .discountPercent(discountPercent)
                .isFlashSale(product.getIsFlashSale())
                .flashSaleEndTime(product.getFlashSaleEndTime())
                .thumbnailUrl(product.getThumbnailUrl())
                .availableSizes(sizes)
                .availableColors(colors)
                .availableColorHexes(colorHexes)
                .totalStock(totalStock)
                .ratingAverage(avgRating)
                .reviewCount(reviewCount)
                .createdAt(product.getCreatedAt())
                .build();
    }

    private ProductDetailDTO mapToProductDetailDTO(Product product) {
        List<ProductVariant> variants = productVariantRepository.findByProductId(product.getId());
        List<Review> reviews = reviewRepository.findByProductIdOrderByCreatedAtDesc(product.getId());

        List<ProductVariantDTO> variantDTOs = variants.stream()
                .map(v -> ProductVariantDTO.builder()
                        .id(v.getId())
                        .productId(product.getId())
                        .size(v.getSize())
                        .colorName(v.getColorName())
                        .colorHex(v.getColorHex())
                        .price(v.getPrice())
                        .stockQuantity(v.getStockQuantity())
                        .sku(v.getSku())
                        .build())
                .collect(Collectors.toList());

        List<ReviewDTO> reviewDTOs = reviews.stream()
                .map(this::mapToReviewDTO)
                .collect(Collectors.toList());

        Double rawAvg = reviewRepository.getAverageRating(product.getId());
        double avgRating = rawAvg != null ? Math.round(rawAvg * 10.0) / 10.0 : 0.0;
        int reviewCount = reviews.size();
        int totalStock = variants.stream().mapToInt(v -> v.getStockQuantity() != null ? v.getStockQuantity() : 0).sum();
        Integer discountPercent = calculateDiscountPercent(product.getBasePrice(), product.getSalePrice());

        return ProductDetailDTO.builder()
                .id(product.getId())
                .categoryId(product.getCategory() != null ? product.getCategory().getId() : null)
                .categoryName(product.getCategory() != null ? product.getCategory().getName() : "")
                .categorySlug(product.getCategory() != null ? product.getCategory().getSlug() : "")
                .name(product.getName())
                .slug(product.getSlug())
                .description(product.getDescription())
                .basePrice(product.getBasePrice())
                .salePrice(product.getSalePrice())
                .discountPercent(discountPercent)
                .isFlashSale(product.getIsFlashSale())
                .flashSaleEndTime(product.getFlashSaleEndTime())
                .thumbnailUrl(product.getThumbnailUrl())
                .ratingAverage(avgRating)
                .reviewCount(reviewCount)
                .totalStock(totalStock)
                .createdAt(product.getCreatedAt())
                .variants(variantDTOs)
                .reviews(reviewDTOs)
                .build();
    }

    private ReviewDTO mapToReviewDTO(Review review) {
        boolean verified = review.getUser() != null
                && review.getProduct() != null
                && orderItemRepository.hasCompletedPurchase(review.getUser().getId(), review.getProduct().getId());

        return ReviewDTO.builder()
                .id(review.getId())
                .productId(review.getProduct() != null ? review.getProduct().getId() : null)
                .userId(review.getUser() != null ? review.getUser().getId() : null)
                .customerName(review.getCustomerName())
                .rating(review.getRating())
                .comment(review.getComment())
                .customerHeightCm(review.getCustomerHeightCm())
                .customerWeightKg(review.getCustomerWeightKg())
                .purchasedSize(review.getPurchasedSize())
                .purchasedColor(review.getPurchasedColor())
                .fitFeedback(review.getFitFeedback())
                .verifiedPurchase(verified)
                .createdAt(review.getCreatedAt())
                .build();
    }

    private Integer calculateDiscountPercent(BigDecimal basePrice, BigDecimal salePrice) {
        if (salePrice != null && basePrice != null && basePrice.compareTo(BigDecimal.ZERO) > 0 && salePrice.compareTo(basePrice) < 0) {
            BigDecimal diff = basePrice.subtract(salePrice);
            BigDecimal pct = diff.divide(basePrice, 2, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));
            return pct.intValue();
        }
        return 0;
    }
}
