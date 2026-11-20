package com.fashion.store.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductReviewsResponse {

    private Double ratingAverage;
    private Integer totalReviews;
    private Integer tightPercent;
    private Integer fitPercent;
    private Integer loosePercent;
    private Map<Integer, Long> starCounts;
    private List<ReviewDTO> reviews;
}
