package com.fashion.store.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewCreateRequest {

    @NotNull(message = "Điểm đánh giá không được để trống")
    @Min(value = 1, message = "Điểm đánh giá tối thiểu là 1 sao")
    @Max(value = 5, message = "Điểm đánh giá tối đa là 5 sao")
    private Integer rating;

    @NotBlank(message = "Bình luận không được để trống")
    private String comment;

    private String customerName;

    private Integer customerHeightCm;

    private Integer customerWeightKg;

    private String purchasedSize;

    private String purchasedColor;

    private String fitFeedback; // CHAT, VUA_VAN, RONG
}
