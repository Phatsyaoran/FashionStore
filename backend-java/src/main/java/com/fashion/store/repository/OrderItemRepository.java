package com.fashion.store.repository;

import com.fashion.store.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    List<OrderItem> findByOrderId(Long orderId);

    List<OrderItem> findByOrderIdIn(Collection<Long> orderIds);

    boolean existsByProductVariant_Product_Id(Long productId);

    boolean existsByProductVariant_Id(Long variantId);

    @Query("SELECT COUNT(oi) > 0 FROM OrderItem oi " +
           "WHERE oi.order.user.id = :userId " +
           "AND oi.productVariant.product.id = :productId " +
           "AND UPPER(oi.order.orderStatus) = 'COMPLETED'")
    boolean hasCompletedPurchase(@Param("userId") Long userId, @Param("productId") Long productId);
}
