package com.fashion.store.service;

import com.fashion.store.dto.ProductDTO;
import com.fashion.store.dto.ProductVariantDTO;
import com.fashion.store.dto.StockUpdateRequest;
import com.fashion.store.entity.Category;
import com.fashion.store.entity.Order;
import com.fashion.store.entity.Product;
import com.fashion.store.entity.ProductVariant;
import com.fashion.store.repository.OrderRepository;
import com.fashion.store.repository.ProductRepository;
import com.fashion.store.repository.ProductVariantRepository;
import com.fashion.store.repository.ReviewRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductVariantRepository productVariantRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderService orderService;

    @Mock
    private ReviewRepository reviewRepository;

    @InjectMocks
    private AdminServiceImpl adminService;

    @Test
    void testToggleProductFlashSale_TurnOn() {
        // Given
        Product product = Product.builder()
                .id(1L)
                .name("Áo Thun Cotton")
                .slug("ao-thun-cotton")
                .basePrice(BigDecimal.valueOf(200000))
                .salePrice(BigDecimal.valueOf(150000))
                .isFlashSale(false)
                .category(Category.builder().id(1L).name("Áo Thun").build())
                .build();

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(productVariantRepository.findByProductId(1L)).thenReturn(Collections.emptyList());
        when(reviewRepository.getAverageRating(1L)).thenReturn(4.5);
        when(reviewRepository.countByProductId(1L)).thenReturn(5L);

        // When
        ProductDTO result = adminService.toggleProductFlashSale(1L);

        // Then
        assertNotNull(result);
        assertTrue(result.getIsFlashSale());
        assertNotNull(result.getFlashSaleEndTime());
    }

    @Test
    void testExportOrdersToCsv_ContainsUtf8BomAndHeaders() {
        // Given
        Order order = Order.builder()
                .id(1L)
                .orderCode("DH1700000001")
                .customerName("Nguyễn Văn An")
                .customerPhone("0987654321")
                .shippingAddress("123 Lê Lợi, TP.HCM")
                .subTotal(BigDecimal.valueOf(500000))
                .shippingFee(BigDecimal.ZERO)
                .discountAmount(BigDecimal.valueOf(50000))
                .totalAmount(BigDecimal.valueOf(450000))
                .paymentMethod("VIETQR")
                .paymentStatus("PAID")
                .orderStatus("COMPLETED")
                .createdAt(LocalDateTime.now())
                .build();

        when(orderRepository.findAll(any(Sort.class))).thenReturn(List.of(order));

        // When
        byte[] csvBytes = adminService.exportOrdersToCsv();

        // Then
        assertNotNull(csvBytes);
        assertTrue(csvBytes.length >= 3);
        // Verify UTF-8 BOM: 0xEF, 0xBB, 0xBF
        assertEquals((byte) 0xEF, csvBytes[0]);
        assertEquals((byte) 0xBB, csvBytes[1]);
        assertEquals((byte) 0xBF, csvBytes[2]);

        String content = new String(csvBytes, 3, csvBytes.length - 3, StandardCharsets.UTF_8);
        assertTrue(content.contains("Mã đơn hàng,Khách hàng"));
        assertTrue(content.contains("DH1700000001"));
        assertTrue(content.contains("Nguyễn Văn An"));
        assertTrue(content.contains("Đã thanh toán"));
        assertTrue(content.contains("Hoàn thành"));
    }

    @Test
    void testExportInventoryToCsv_ContainsUtf8BomAndInventoryData() {
        // Given
        Category category = Category.builder().id(1L).name("Áo Thun").build();
        Product product = Product.builder().id(1L).name("Áo Thun Cotton").category(category).build();
        ProductVariant variant = ProductVariant.builder()
                .id(10L)
                .product(product)
                .sku("AT-01-M-DEN")
                .size("M")
                .colorName("Đen Basic")
                .colorHex("#000000")
                .price(BigDecimal.valueOf(150000))
                .stockQuantity(12)
                .build();

        when(productVariantRepository.findAll(any(Sort.class))).thenReturn(List.of(variant));

        // When
        byte[] csvBytes = adminService.exportInventoryToCsv();

        // Then
        assertNotNull(csvBytes);
        assertTrue(csvBytes.length >= 3);
        // Verify UTF-8 BOM: 0xEF, 0xBB, 0xBF
        assertEquals((byte) 0xEF, csvBytes[0]);
        assertEquals((byte) 0xBB, csvBytes[1]);
        assertEquals((byte) 0xBF, csvBytes[2]);

        String content = new String(csvBytes, 3, csvBytes.length - 3, StandardCharsets.UTF_8);
        assertTrue(content.contains("Mã SKU,Tên sản phẩm"));
        assertTrue(content.contains("AT-01-M-DEN"));
        assertTrue(content.contains("Áo Thun Cotton"));
        assertTrue(content.contains("Còn hàng sẵn sàng"));
    }

    @Test
    void testUpdateVariantStock() {
        // Given
        Product product = Product.builder().id(1L).build();
        ProductVariant variant = ProductVariant.builder()
                .id(10L)
                .product(product)
                .size("L")
                .colorName("Trắng")
                .stockQuantity(5)
                .sku("AT-01-L-TRANG")
                .price(BigDecimal.valueOf(180000))
                .build();

        when(productVariantRepository.findById(10L)).thenReturn(Optional.of(variant));
        when(productVariantRepository.save(any(ProductVariant.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        ProductVariantDTO updated = adminService.updateVariantStock(10L, new StockUpdateRequest(30));

        // Then
        assertNotNull(updated);
        assertEquals(30, updated.getStockQuantity());
    }
}
