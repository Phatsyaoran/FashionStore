package com.fashion.store.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "ProductVariants")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductVariant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ProductId", nullable = false)
    private Product product;

    @Column(name = "Size", nullable = false, length = 20)
    private String size;

    @Column(name = "ColorName", nullable = false, length = 50)
    private String colorName;

    @Column(name = "ColorHex", nullable = false, length = 20)
    private String colorHex;

    @Column(name = "Price", nullable = false, precision = 18, scale = 2)
    private BigDecimal price;

    @Column(name = "StockQuantity", nullable = false)
    @Builder.Default
    private Integer stockQuantity = 0;

    @Column(name = "Sku", nullable = false, unique = true, length = 50)
    private String sku;
}
