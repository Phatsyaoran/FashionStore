package com.fashion.store.repository;

import com.fashion.store.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    List<OrderItem> findByOrderId(Long orderId);

    boolean existsByProductVariant_Product_Id(Long productId);

    boolean existsByProductVariant_Id(Long variantId);
}
