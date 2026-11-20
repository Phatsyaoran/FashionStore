package com.fashion.store.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "Reviews")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ProductId", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "UserId")
    private User user;

    @Column(name = "CustomerName", nullable = false, length = 150)
    private String customerName;

    @Column(name = "Rating", nullable = false)
    private Integer rating;

    @Column(name = "Comment", nullable = false, length = 1000)
    private String comment;

    @Column(name = "CustomerHeightCm")
    private Integer customerHeightCm;

    @Column(name = "CustomerWeightKg")
    private Integer customerWeightKg;

    @Column(name = "PurchasedSize", length = 20)
    private String purchasedSize;

    @Column(name = "PurchasedColor", length = 50)
    private String purchasedColor;

    @Column(name = "FitFeedback", length = 20)
    private String fitFeedback; // CHAT, VUA_VAN, RONG

    @Column(name = "CreatedAt", nullable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}
