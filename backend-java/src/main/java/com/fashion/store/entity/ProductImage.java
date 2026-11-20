package com.fashion.store.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "ProductImages")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ProductId", nullable = false)
    private Product product;

    @Column(name = "ImageUrl", nullable = false, length = 500)
    private String imageUrl;

    @Column(name = "DisplayOrder", nullable = false)
    @Builder.Default
    private Integer displayOrder = 0;
}
