package com.fashion.store.repository;

import com.fashion.store.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByProductIdOrderByCreatedAtDesc(Long productId);

    Page<Review> findByProductId(Long productId, Pageable pageable);

    @Query("SELECT COALESCE(AVG(r.rating), 0.0) FROM Review r WHERE r.product.id = :productId")
    Double getAverageRating(@Param("productId") Long productId);

    long countByProductId(Long productId);

    @Query("SELECT r.product.id, COALESCE(AVG(r.rating), 0.0), COUNT(r.id) FROM Review r WHERE r.product.id IN :productIds GROUP BY r.product.id")
    List<Object[]> findRatingStatsByProductIds(@Param("productIds") Collection<Long> productIds);
}
