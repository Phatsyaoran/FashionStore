package com.fashion.store.repository;

import com.fashion.store.entity.Product;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

    Optional<Product> findBySlug(String slug);

    boolean existsBySlug(String slug);

    List<Product> findByIsFlashSaleTrueAndFlashSaleEndTimeAfter(LocalDateTime now);

    @Query("SELECT p FROM Product p WHERE p.isFlashSale = true AND (p.flashSaleEndTime IS NULL OR p.flashSaleEndTime > :now) ORDER BY p.createdAt DESC")
    List<Product> findActiveFlashSales(@Param("now") LocalDateTime now);

    @Query("SELECT p FROM Product p ORDER BY p.createdAt DESC")
    List<Product> findLatestProducts(Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.category.id = :categoryId AND p.id <> :productId")
    List<Product> findRelatedProducts(@Param("categoryId") Long categoryId, @Param("productId") Long productId, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.category.id <> :categoryId AND p.id <> :productId ORDER BY p.createdAt DESC")
    List<Product> findMixMatchProducts(@Param("categoryId") Long categoryId, @Param("productId") Long productId, Pageable pageable);

    @Query("SELECT DISTINCT v.product FROM ProductVariant v WHERE v.size = :size AND v.stockQuantity > 0")
    List<Product> findProductsBySizeInStock(@Param("size") String size, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE COALESCE(p.salePrice, p.basePrice) <= :maxPrice ORDER BY COALESCE(p.salePrice, p.basePrice) ASC")
    List<Product> findProductsUnderPrice(@Param("maxPrice") java.math.BigDecimal maxPrice, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE (LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(p.category.name) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND COALESCE(p.salePrice, p.basePrice) <= :maxPrice ORDER BY COALESCE(p.salePrice, p.basePrice) ASC")
    List<Product> findProductsByKeywordAndUnderPrice(@Param("keyword") String keyword, @Param("maxPrice") java.math.BigDecimal maxPrice, Pageable pageable);
}
