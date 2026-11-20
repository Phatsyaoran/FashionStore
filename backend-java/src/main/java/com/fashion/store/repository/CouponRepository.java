package com.fashion.store.repository;

import com.fashion.store.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, Long> {

    Optional<Coupon> findByCode(String code);

    Optional<Coupon> findByCodeAndIsActiveTrue(String code);

    boolean existsByCode(String code);

    @Query("SELECT c FROM Coupon c WHERE c.isActive = true AND (c.expiryDate IS NULL OR c.expiryDate > :now)")
    List<Coupon> findActiveCoupons(@Param("now") LocalDateTime now);
}
