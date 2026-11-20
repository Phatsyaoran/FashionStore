package com.fashion.store.repository;

import com.fashion.store.entity.Category;
import com.fashion.store.entity.Product;
import com.fashion.store.entity.ProductVariant;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ProductSpecification {

    public static Specification<Product> filterProducts(
            Long categoryId,
            String categorySlug,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            String size,
            String color,
            Boolean isFlashSale,
            String keyword
    ) {
        return (Root<Product> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Distinct results if joins are used
            query.distinct(true);

            // Filter by Category Id
            if (categoryId != null) {
                predicates.add(cb.equal(root.get("category").get("id"), categoryId));
            }

            // Filter by Category Slug
            if (categorySlug != null && !categorySlug.trim().isEmpty()) {
                Join<Product, Category> categoryJoin = root.join("category", JoinType.INNER);
                predicates.add(cb.equal(categoryJoin.get("slug"), categorySlug.trim()));
            }

            // Effective price expression: COALESCE(salePrice, basePrice)
            Expression<BigDecimal> effectivePrice = cb.coalesce(root.get("salePrice"), root.get("basePrice"));

            // Filter by Min Price
            if (minPrice != null) {
                predicates.add(cb.greaterThanOrEqualTo(effectivePrice, minPrice));
            }

            // Filter by Max Price
            if (maxPrice != null) {
                predicates.add(cb.lessThanOrEqualTo(effectivePrice, maxPrice));
            }

            // Filter by Size or Color via ProductVariant join
            if ((size != null && !size.trim().isEmpty()) || (color != null && !color.trim().isEmpty())) {
                Join<Product, ProductVariant> variantJoin = root.join("variants", JoinType.INNER);

                if (size != null && !size.trim().isEmpty()) {
                    predicates.add(cb.equal(cb.upper(variantJoin.get("size")), size.trim().toUpperCase()));
                }

                if (color != null && !color.trim().isEmpty()) {
                    predicates.add(cb.like(cb.lower(variantJoin.get("colorName")), "%" + color.trim().toLowerCase() + "%"));
                }
            }

            // Filter by Flash Sale
            if (Boolean.TRUE.equals(isFlashSale)) {
                predicates.add(cb.isTrue(root.get("isFlashSale")));
                predicates.add(cb.or(
                        cb.isNull(root.get("flashSaleEndTime")),
                        cb.greaterThan(root.get("flashSaleEndTime"), LocalDateTime.now())
                ));
            }

            // Search Keyword (Name or Description)
            if (keyword != null && !keyword.trim().isEmpty()) {
                String searchPattern = "%" + keyword.trim().toLowerCase() + "%";
                Predicate nameMatch = cb.like(cb.lower(root.get("name")), searchPattern);
                Predicate descMatch = cb.like(cb.lower(root.get("description")), searchPattern);
                predicates.add(cb.or(nameMatch, descMatch));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
