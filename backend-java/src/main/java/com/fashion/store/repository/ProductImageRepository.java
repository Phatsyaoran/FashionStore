package com.fashion.store.repository;

import com.fashion.store.entity.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {

    List<ProductImage> findByProductIdOrderByDisplayOrderAscIdAsc(Long productId);

    void deleteByProductId(Long productId);
}
