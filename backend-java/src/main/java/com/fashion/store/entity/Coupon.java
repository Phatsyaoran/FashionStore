package com.fashion.store.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "Coupons")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Long id;

    @Column(name = "Code", nullable = false, unique = true, length = 50)
    private String code;

    @Column(name = "DiscountPercent", nullable = false)
    private Integer discountPercent;

    @Column(name = "MaxDiscountAmount", nullable = false, precision = 18, scale = 2)
    private BigDecimal maxDiscountAmount;

    @Column(name = "MinOrderValue", nullable = false, precision = 18, scale = 2)
    private BigDecimal minOrderValue;

    @Column(name = "ExpiryDate", nullable = false)
    private LocalDateTime expiryDate;

    @Column(name = "UsageLimit", nullable = false)
    private Integer usageLimit;

    @Column(name = "UsedCount", nullable = false)
    @Builder.Default
    private Integer usedCount = 0;

    @Column(name = "IsActive", nullable = false)
    @Builder.Default
    private Boolean isActive = true;
}
