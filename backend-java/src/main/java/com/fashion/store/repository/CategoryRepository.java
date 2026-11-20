package com.fashion.store.repository;

import com.fashion.store.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    Optional<Category> findBySlug(String slug);

    boolean existsBySlug(String slug);

    @Query("SELECT c.id AS categoryId, COUNT(p.id) AS productCount " +
           "FROM Category c LEFT JOIN c.products p " +
           "GROUP BY c.id")
    List<Object[]> countProductsByCategory();
}
