package com.fashion.store.service;

import com.fashion.store.dto.OrderCreateRequest;
import com.fashion.store.dto.OrderResponseDTO;
import com.fashion.store.dto.OrderStatusUpdateRequest;
import com.fashion.store.dto.PageResponse;

import java.util.List;

public interface OrderService {

    OrderResponseDTO createOrder(OrderCreateRequest request, String userEmail);

    OrderResponseDTO getOrderByCode(String orderCode);

    List<OrderResponseDTO> getOrdersByPhone(String phone);

    OrderResponseDTO confirmPayment(String orderCode);

    OrderResponseDTO updateOrderStatus(String orderCode, OrderStatusUpdateRequest request);

    List<OrderResponseDTO> getAllOrders();

    PageResponse<OrderResponseDTO> searchOrders(String keyword, String status, int page, int size);

    List<OrderResponseDTO> getMyOrders(String userEmail);

    OrderResponseDTO cancelMyOrder(String orderCode, String userEmail);
}
