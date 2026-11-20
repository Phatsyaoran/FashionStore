package com.fashion.store.service;

import com.fashion.store.dto.OrderCreateRequest;
import com.fashion.store.dto.OrderResponseDTO;
import com.fashion.store.dto.OrderStatusUpdateRequest;

import java.util.List;

public interface OrderService {

    OrderResponseDTO createOrder(OrderCreateRequest request, String userEmail);

    OrderResponseDTO getOrderByCode(String orderCode);

    List<OrderResponseDTO> getOrdersByPhone(String phone);

    OrderResponseDTO confirmPayment(String orderCode);

    OrderResponseDTO updateOrderStatus(String orderCode, OrderStatusUpdateRequest request);

    List<OrderResponseDTO> getAllOrders();
}
