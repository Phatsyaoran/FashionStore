package com.fashion.store.service;

import com.fashion.store.dto.*;
import com.fashion.store.entity.*;
import com.fashion.store.exception.BadRequestException;
import com.fashion.store.exception.ResourceNotFoundException;
import com.fashion.store.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductVariantRepository productVariantRepository;
    private final CouponRepository couponRepository;
    private final UserRepository userRepository;

    @Value("${app.vietqr.bank-id:MB}")
    private String bankId;

    @Value("${app.vietqr.account-no:0123456789}")
    private String accountNo;

    @Value("${app.vietqr.account-name:NGUYEN VAN PHAT}")
    private String accountName;

    private static final BigDecimal FREESHIP_THRESHOLD = BigDecimal.valueOf(500000);
    private static final BigDecimal DEFAULT_SHIPPING_FEE = BigDecimal.valueOf(30000);

    @Override
    @Transactional
    public OrderResponseDTO createOrder(OrderCreateRequest request, String userEmail) {
        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new BadRequestException("Giỏ hàng không có sản phẩm");
        }

        User user = null;
        if (userEmail != null && !userEmail.isEmpty()) {
            user = userRepository.findByEmail(userEmail).orElse(null);
        }

        BigDecimal subTotal = BigDecimal.ZERO;
        List<OrderItem> orderItemsToSave = new ArrayList<>();

        // 1. Kiểm tra tồn kho và trừ tồn kho an toàn
        for (OrderItemRequest itemReq : request.getItems()) {
            ProductVariant variant = productVariantRepository.findById(itemReq.getProductVariantId())
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy biến thể sản phẩm ID: " + itemReq.getProductVariantId()));

            int requestedQty = itemReq.getQuantity();
            if (variant.getStockQuantity() < requestedQty) {
                throw new BadRequestException("Biến thể '" + variant.getProduct().getName() + " (" + variant.getSize() + ", " + variant.getColorName() + ")' chỉ còn " + variant.getStockQuantity() + " chiếc trong kho, không đủ đáp ứng số lượng " + requestedQty);
            }

            // Trừ tồn kho an toàn
            variant.setStockQuantity(variant.getStockQuantity() - requestedQty);
            productVariantRepository.save(variant);

            BigDecimal lineTotal = variant.getPrice().multiply(BigDecimal.valueOf(requestedQty));
            subTotal = subTotal.add(lineTotal);

            OrderItem orderItem = OrderItem.builder()
                    .productVariant(variant)
                    .productName(variant.getProduct().getName())
                    .size(variant.getSize())
                    .colorName(variant.getColorName())
                    .quantity(requestedQty)
                    .unitPrice(variant.getPrice())
                    .build();

            orderItemsToSave.add(orderItem);
        }

        // 2. Tự động tính phí vận chuyển: Freeship nếu SubTotal >= 500.000, ngược lại 30.000đ
        BigDecimal shippingFee = (subTotal.compareTo(FREESHIP_THRESHOLD) >= 0) ? BigDecimal.ZERO : DEFAULT_SHIPPING_FEE;

        // 3. Xử lý mã giảm giá (Coupon)
        BigDecimal discountAmount = BigDecimal.ZERO;
        String appliedCouponCode = null;

        if (request.getCouponCode() != null && !request.getCouponCode().trim().isEmpty()) {
            String code = request.getCouponCode().trim().toUpperCase();
            Optional<Coupon> couponOpt = couponRepository.findByCodeAndIsActiveTrue(code);
            if (couponOpt.isPresent()) {
                Coupon coupon = couponOpt.get();
                if (coupon.getExpiryDate().isAfter(LocalDateTime.now()) &&
                        coupon.getUsedCount() < coupon.getUsageLimit() &&
                        subTotal.compareTo(coupon.getMinOrderValue()) >= 0) {
                    
                    BigDecimal calc = subTotal.multiply(BigDecimal.valueOf(coupon.getDiscountPercent()))
                            .divide(BigDecimal.valueOf(100), 0, RoundingMode.HALF_UP);
                    if (calc.compareTo(coupon.getMaxDiscountAmount()) > 0) {
                        calc = coupon.getMaxDiscountAmount();
                    }
                    discountAmount = calc;
                    appliedCouponCode = coupon.getCode();

                    // Tăng số lượt dùng coupon
                    coupon.setUsedCount(coupon.getUsedCount() + 1);
                    couponRepository.save(coupon);
                }
            }
        }

        // 4. Tính tổng thanh toán cuối cùng
        BigDecimal totalAmount = subTotal.add(shippingFee).subtract(discountAmount);
        if (totalAmount.compareTo(BigDecimal.ZERO) < 0) {
            totalAmount = BigDecimal.ZERO;
        }

        // 5. Sinh mã đơn hàng duy nhất: DH + yyyyMMdd + 4 số ngẫu nhiên / sequence
        String datePart = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        long countToday = orderRepository.count() + 1;
        String orderCode = "DH" + datePart + String.format("%04d", (countToday % 10000));

        // 6. Lưu đơn hàng
        Order order = Order.builder()
                .orderCode(orderCode)
                .user(user)
                .customerName(request.getCustomerName().trim())
                .customerPhone(request.getCustomerPhone().trim())
                .shippingAddress(request.getShippingAddress().trim())
                .note(request.getNote() != null ? request.getNote().trim() : null)
                .subTotal(subTotal)
                .shippingFee(shippingFee)
                .discountAmount(discountAmount)
                .totalAmount(totalAmount)
                .couponCode(appliedCouponCode)
                .paymentMethod(request.getPaymentMethod().toUpperCase())
                .paymentStatus("UNPAID")
                .orderStatus("PENDING")
                .createdAt(LocalDateTime.now())
                .build();

        Order savedOrder = orderRepository.save(order);

        // 7. Gắn quan hệ và lưu chi tiết đơn hàng
        for (OrderItem oi : orderItemsToSave) {
            oi.setOrder(savedOrder);
            orderItemRepository.save(oi);
        }

        savedOrder.setOrderItems(orderItemsToSave);
        return mapToOrderResponseDTO(savedOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponseDTO getOrderByCode(String orderCode) {
        Order order = orderRepository.findByOrderCode(orderCode.trim())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn hàng với mã: " + orderCode));
        return mapToOrderResponseDTO(order);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponseDTO> getOrdersByPhone(String phone) {
        List<Order> orders = orderRepository.findByCustomerPhoneOrderByCreatedAtDesc(phone.trim());
        return orders.stream().map(this::mapToOrderResponseDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public OrderResponseDTO confirmPayment(String orderCode) {
        Order order = orderRepository.findByOrderCode(orderCode.trim())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn hàng với mã: " + orderCode));

        order.setPaymentStatus("PAID");
        if ("PENDING".equals(order.getOrderStatus())) {
            order.setOrderStatus("CONFIRMED");
        }
        Order updated = orderRepository.save(order);
        return mapToOrderResponseDTO(updated);
    }

    private static final Map<String, Integer> ORDER_STATUS_RANK = Map.of(
            "PENDING", 0,
            "CONFIRMED", 1,
            "PACKING", 2,
            "SHIPPING", 3,
            "COMPLETED", 4
    );

    @Override
    @Transactional
    public OrderResponseDTO updateOrderStatus(String orderCode, OrderStatusUpdateRequest request) {
        Order order = orderRepository.findByOrderCode(orderCode.trim())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn hàng: " + orderCode));

        String newStatus = request.getStatus().trim().toUpperCase();
        if (!ORDER_STATUS_RANK.containsKey(newStatus) && !"CANCELLED".equals(newStatus)) {
            throw new BadRequestException("Trạng thái đơn hàng không hợp lệ: " + newStatus);
        }

        String currentStatus = order.getOrderStatus() != null
                ? order.getOrderStatus().trim().toUpperCase()
                : "PENDING";

        if (currentStatus.equals(newStatus)) {
            return mapToOrderResponseDTO(order);
        }

        if ("CANCELLED".equals(currentStatus)) {
            throw new BadRequestException("Đơn hàng đã bị hủy, không thể chuyển sang trạng thái " + newStatus);
        }

        if ("COMPLETED".equals(currentStatus)) {
            throw new BadRequestException("Đơn hàng đã hoàn thành, không thể chuyển sang trạng thái " + newStatus);
        }

        if (!"CANCELLED".equals(newStatus)) {
            int currentRank = ORDER_STATUS_RANK.getOrDefault(currentStatus, 0);
            int targetRank = ORDER_STATUS_RANK.get(newStatus);
            if (targetRank < currentRank) {
                throw new BadRequestException("Không thể chuyển ngược trạng thái đơn hàng từ " + currentStatus + " về " + newStatus);
            }
        } else {
            // Hoàn trả tồn kho từng biến thể khi hủy đơn hàng
            List<OrderItem> items = orderItemRepository.findByOrderId(order.getId());
            for (OrderItem item : items) {
                if (item.getProductVariant() != null) {
                    ProductVariant variant = item.getProductVariant();
                    int currentStock = variant.getStockQuantity() != null ? variant.getStockQuantity() : 0;
                    variant.setStockQuantity(currentStock + item.getQuantity());
                    productVariantRepository.save(variant);
                }
            }

            // Hoàn lại lượt sử dụng mã giảm giá nếu đơn hàng có áp dụng coupon
            if (order.getCouponCode() != null && !order.getCouponCode().trim().isEmpty()) {
                couponRepository.findByCode(order.getCouponCode().trim().toUpperCase())
                        .ifPresent(coupon -> {
                            int used = coupon.getUsedCount() != null ? coupon.getUsedCount() : 0;
                            if (used > 0) {
                                coupon.setUsedCount(used - 1);
                                couponRepository.save(coupon);
                            }
                        });
            }
        }

        order.setOrderStatus(newStatus);
        if ("COMPLETED".equals(newStatus)) {
            order.setPaymentStatus("PAID");
        }

        Order updated = orderRepository.save(order);
        return mapToOrderResponseDTO(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponseDTO> getAllOrders() {
        return orderRepository.findAll().stream()
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .map(this::mapToOrderResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponseDTO> getMyOrders(String userEmail) {
        User user = userRepository.findByEmail(userEmail.trim().toLowerCase())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tài khoản người dùng"));
        return orderRepository.findByUserIdOrderByCreatedAtDesc(user.getId()).stream()
                .map(this::mapToOrderResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public OrderResponseDTO cancelMyOrder(String orderCode, String userEmail) {
        Order order = orderRepository.findByOrderCode(orderCode.trim())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn hàng: " + orderCode));

        if (order.getUser() == null || !order.getUser().getEmail().equalsIgnoreCase(userEmail.trim())) {
            throw new BadRequestException("Bạn không có quyền hủy đơn hàng này.");
        }

        if (!"PENDING".equalsIgnoreCase(order.getOrderStatus())) {
            throw new BadRequestException("Chỉ có thể tự hủy đơn hàng khi đang ở trạng thái Chờ xác nhận (PENDING).");
        }

        OrderStatusUpdateRequest req = new OrderStatusUpdateRequest();
        req.setStatus("CANCELLED");
        return updateOrderStatus(orderCode, req);
    }

    // ==========================================
    // HELPER MAPPING & VIETQR LINK GENERATION
    // ==========================================

    private OrderResponseDTO mapToOrderResponseDTO(Order order) {
        List<OrderItem> items = orderItemRepository.findByOrderId(order.getId());

        List<OrderItemDTO> itemDTOs = items.stream().map(it -> OrderItemDTO.builder()
                .id(it.getId())
                .productVariantId(it.getProductVariant() != null ? it.getProductVariant().getId() : null)
                .productName(it.getProductName())
                .size(it.getSize())
                .colorName(it.getColorName())
                .quantity(it.getQuantity())
                .unitPrice(it.getUnitPrice())
                .totalPrice(it.getUnitPrice().multiply(BigDecimal.valueOf(it.getQuantity())))
                .build()
        ).collect(Collectors.toList());

        // Sinh link VietQR QuickLink chuẩn ngân hàng MB nếu là VIETQR
        String vietQrUrl = null;
        if ("VIETQR".equalsIgnoreCase(order.getPaymentMethod())) {
            vietQrUrl = buildVietQrUrl(order.getTotalAmount().longValue(), order.getOrderCode());
        }

        return OrderResponseDTO.builder()
                .id(order.getId())
                .orderCode(order.getOrderCode())
                .userId(order.getUser() != null ? order.getUser().getId() : null)
                .customerName(order.getCustomerName())
                .customerPhone(order.getCustomerPhone())
                .shippingAddress(order.getShippingAddress())
                .note(order.getNote())
                .subTotal(order.getSubTotal())
                .shippingFee(order.getShippingFee())
                .discountAmount(order.getDiscountAmount())
                .totalAmount(order.getTotalAmount())
                .couponCode(order.getCouponCode())
                .paymentMethod(order.getPaymentMethod())
                .paymentStatus(order.getPaymentStatus())
                .orderStatus(order.getOrderStatus())
                .vietQrUrl(vietQrUrl)
                .createdAt(order.getCreatedAt())
                .orderItems(itemDTOs)
                .build();
    }

    private String buildVietQrUrl(long amount, String orderCode) {
        String encodedAccountName = URLEncoder.encode(accountName, StandardCharsets.UTF_8);
        return String.format("https://img.vietqr.io/image/%s-%s-compact2.png?amount=%d&addInfo=%s&accountName=%s",
                bankId, accountNo, amount, orderCode, encodedAccountName);
    }
}
