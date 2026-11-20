package com.fashion.store.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Long id;

    @Column(name = "OrderCode", nullable = false, unique = true, length = 50)
    private String orderCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "UserId")
    private User user;

    @Column(name = "CustomerName", nullable = false, length = 150)
    private String customerName;

    @Column(name = "CustomerPhone", nullable = false, length = 20)
    private String customerPhone;

    @Column(name = "ShippingAddress", nullable = false, length = 300)
    private String shippingAddress;

    @Column(name = "Note", length = 500)
    private String note;

    @Column(name = "SubTotal", nullable = false, precision = 18, scale = 2)
    private BigDecimal subTotal;

    @Column(name = "ShippingFee", nullable = false, precision = 18, scale = 2)
    @Builder.Default
    private BigDecimal shippingFee = BigDecimal.ZERO;

    @Column(name = "DiscountAmount", nullable = false, precision = 18, scale = 2)
    @Builder.Default
    private BigDecimal discountAmount = BigDecimal.ZERO;

    @Column(name = "TotalAmount", nullable = false, precision = 18, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "CouponCode", length = 50)
    private String couponCode;

    @Column(name = "PaymentMethod", nullable = false, length = 20)
    private String paymentMethod; // COD, VIETQR

    @Column(name = "PaymentStatus", nullable = false, length = 20)
    @Builder.Default
    private String paymentStatus = "UNPAID"; // UNPAID, PAID

    @Column(name = "OrderStatus", nullable = false, length = 30)
    @Builder.Default
    private String orderStatus = "PENDING"; // PENDING, CONFIRMED, PACKING, SHIPPING, COMPLETED, CANCELLED

    @Column(name = "CreatedAt", nullable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @JsonIgnore
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<OrderItem> orderItems = new ArrayList<>();
}
