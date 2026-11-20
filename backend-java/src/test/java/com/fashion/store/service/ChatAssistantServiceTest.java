package com.fashion.store.service;

import com.fashion.store.dto.*;
import com.fashion.store.entity.Coupon;
import com.fashion.store.entity.Product;
import com.fashion.store.entity.ProductVariant;
import com.fashion.store.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatAssistantServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductVariantRepository productVariantRepository;

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private CouponRepository couponRepository;

    @Mock
    private OrderService orderService;

    @InjectMocks
    private ChatAssistantServiceImpl chatAssistantService;

    @Test
    void testHandleSizeAdvice_WithHeightAndWeight() {
        // Given
        ChatAssistantRequest request = ChatAssistantRequest.builder()
                .message("Mình cao 1m75 nặng 65kg mặc áo thun size gì?")
                .build();

        Product product = Product.builder()
                .id(1L)
                .name("Áo Thun Cotton Compact")
                .slug("ao-thun-cotton-compact")
                .basePrice(BigDecimal.valueOf(250000))
                .salePrice(BigDecimal.valueOf(199000))
                .thumbnailUrl("https://picsum.photos/seed/fs-sp-01/600/800")
                .build();

        when(productRepository.findProductsBySizeInStock(eq("L"), any(Pageable.class)))
                .thenReturn(List.of(product));
        when(productVariantRepository.findByProductId(1L))
                .thenReturn(List.of(
                        ProductVariant.builder().id(10L).size("L").colorName("Trắng").stockQuantity(15).build()
                ));
        when(reviewRepository.getAverageRating(1L)).thenReturn(4.8);
        when(reviewRepository.countByProductId(1L)).thenReturn(10L);

        // When
        ChatAssistantResponse response = chatAssistantService.handleChatMessage(request);

        // Then
        assertNotNull(response);
        assertEquals("SIZE_ADVICE", response.getIntent());
        assertEquals("L", response.getSuggestedSize());
        assertTrue(response.getReply().contains("SIZE L"));
        assertNotNull(response.getSuggestedProducts());
        assertEquals(1, response.getSuggestedProducts().size());
        assertEquals("Áo Thun Cotton Compact", response.getSuggestedProducts().get(0).getName());
    }

    @Test
    void testHandleCouponInquiry() {
        // Given
        ChatAssistantRequest request = ChatAssistantRequest.builder()
                .message("Xin mã giảm giá hôm nay với shop")
                .build();

        Coupon coupon = Coupon.builder()
                .id(1L)
                .code("FASHION10")
                .discountPercent(10)
                .maxDiscountAmount(BigDecimal.valueOf(50000))
                .minOrderValue(BigDecimal.valueOf(300000))
                .isActive(true)
                .build();

        when(couponRepository.findActiveCoupons(any(LocalDateTime.class)))
                .thenReturn(List.of(coupon));

        // When
        ChatAssistantResponse response = chatAssistantService.handleChatMessage(request);

        // Then
        assertNotNull(response);
        assertEquals("COUPON_LIST", response.getIntent());
        assertTrue(response.getReply().contains("FASHION10"));
        assertNotNull(response.getActiveCoupons());
        assertEquals(1, response.getActiveCoupons().size());
    }

    @Test
    void testHandleOrderTracking_Found() {
        // Given
        ChatAssistantRequest request = ChatAssistantRequest.builder()
                .message("Kiểm tra đơn DH1700000001 giúp mình")
                .build();

        OrderResponseDTO order = OrderResponseDTO.builder()
                .orderCode("DH1700000001")
                .customerName("Nguyễn Văn An")
                .customerPhone("0987654321")
                .shippingAddress("123 Lê Lợi, Q.1, TP.HCM")
                .totalAmount(BigDecimal.valueOf(450000))
                .orderStatus("SHIPPING")
                .paymentStatus("PAID")
                .paymentMethod("VIETQR")
                .build();

        when(orderService.getOrderByCode("DH1700000001")).thenReturn(order);

        // When
        ChatAssistantResponse response = chatAssistantService.handleChatMessage(request);

        // Then
        assertNotNull(response);
        assertEquals("ORDER_TRACKING", response.getIntent());
        assertNotNull(response.getOrderInfo());
        assertEquals("DH1700000001", response.getOrderInfo().getOrderCode());
        assertTrue(response.getReply().contains("Nguyễn Văn An"));
    }

    @Test
    void testHandlePriceSearch_Under400k() {
        // Given
        ChatAssistantRequest request = ChatAssistantRequest.builder()
                .message("Tư vấn áo sơ mi hoặc quần jean dưới 400k")
                .build();

        Product product = Product.builder()
                .id(2L)
                .name("Áo Sơ Mi Oxford Dài Tay")
                .slug("ao-so-mi-oxford-dai-tay")
                .basePrice(BigDecimal.valueOf(420000))
                .salePrice(BigDecimal.valueOf(380000))
                .thumbnailUrl("https://picsum.photos/seed/fs-sp-02/600/800")
                .build();

        when(productRepository.findProductsByKeywordAndUnderPrice(anyString(), any(BigDecimal.class), any(Pageable.class)))
                .thenReturn(List.of(product));
        when(productVariantRepository.findByProductId(2L)).thenReturn(Collections.emptyList());
        when(reviewRepository.getAverageRating(2L)).thenReturn(5.0);
        when(reviewRepository.countByProductId(2L)).thenReturn(3L);

        // When
        ChatAssistantResponse response = chatAssistantService.handleChatMessage(request);

        // Then
        assertNotNull(response);
        assertEquals("PRICE_SEARCH", response.getIntent());
        assertNotNull(response.getSuggestedProducts());
        assertEquals(1, response.getSuggestedProducts().size());
        assertEquals("Áo Sơ Mi Oxford Dài Tay", response.getSuggestedProducts().get(0).getName());
    }
}
