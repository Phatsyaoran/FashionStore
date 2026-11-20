package com.fashion.store.repository;

import com.fashion.store.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findByOrderCode(String orderCode);

    boolean existsByOrderCode(String orderCode);

    List<Order> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<Order> findByCustomerPhoneOrderByCreatedAtDesc(String customerPhone);

    Page<Order> findAllByOrderByCreatedAtDesc(Pageable pageable);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.orderStatus = :status")
    long countByOrderStatus(@Param("status") String status);

    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o WHERE o.paymentStatus = 'PAID' AND o.orderStatus <> 'CANCELLED'")
    BigDecimal calculateTotalRevenue();

    @Query("SELECT o FROM Order o WHERE o.createdAt >= :startDate AND o.orderStatus <> 'CANCELLED' ORDER BY o.createdAt ASC")
    List<Order> findActiveOrdersSince(@Param("startDate") LocalDateTime startDate);

    @Query("SELECT o FROM Order o WHERE " +
           "(:status IS NULL OR :status = '' OR UPPER(o.orderStatus) = UPPER(:status)) AND " +
           "(:keyword IS NULL OR :keyword = '' OR " +
           "LOWER(o.orderCode) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(o.customerName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(o.customerPhone) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "ORDER BY o.createdAt DESC")
    Page<Order> searchAdminOrders(
            @Param("keyword") String keyword,
            @Param("status") String status,
            Pageable pageable
    );
}
