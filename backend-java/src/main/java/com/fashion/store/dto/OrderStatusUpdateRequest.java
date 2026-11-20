package com.fashion.store.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderStatusUpdateRequest {

    @NotBlank(message = "Trạng thái đơn hàng không được để trống")
    private String status; // PENDING, CONFIRMED, PACKING, SHIPPING, COMPLETED, CANCELLED
}
