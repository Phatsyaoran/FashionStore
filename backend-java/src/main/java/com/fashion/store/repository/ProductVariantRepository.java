package com.fashion.store.repository;

import com.fashion.store.entity.ProductVariant;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductVariantRepository extends JpaRepository<ProductVariant, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT v FROM ProductVariant v WHERE v.id = :id")
    Optional<ProductVariant> findByIdForUpdate(@Param("id") Long id);

    List<ProductVariant> findByProductId(Long productId);

    List<ProductVariant> findByProductIdIn(Collection<Long> productIds);

    Optional<ProductVariant> findBySku(String sku);

    boolean existsBySku(String sku);

    Optional<ProductVariant> findByProductIdAndSizeAndColorName(Long productId, String size, String colorName);

    @EntityGraph(attributePaths = {"product"})
    List<ProductVariant> findByStockQuantityLessThanEqualOrderByStockQuantityAsc(Integer threshold);

    @Query("SELECT v FROM ProductVariant v LEFT JOIN FETCH v.product p LEFT JOIN FETCH p.category ORDER BY v.sku ASC")
    List<ProductVariant> findAllWithProductAndCategoryOrderBySkuAsc();

    long countByStockQuantityLessThanEqual(Integer threshold);
}
