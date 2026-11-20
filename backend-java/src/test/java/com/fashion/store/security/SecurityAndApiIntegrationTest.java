package com.fashion.store.security;

import com.fashion.store.config.CorsConfig;
import com.fashion.store.config.SecurityConfig;
import com.fashion.store.controller.AdminController;
import com.fashion.store.controller.AuthController;
import com.fashion.store.controller.OrderController;
import com.fashion.store.controller.ProductController;
import com.fashion.store.dto.*;
import com.fashion.store.service.AdminService;
import com.fashion.store.service.AuthService;
import com.fashion.store.service.CatalogService;
import com.fashion.store.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = {
        AuthController.class,
        OrderController.class,
        AdminController.class,
        ProductController.class
})
@Import({
        SecurityConfig.class,
        CorsConfig.class,
        JwtTokenProvider.class,
        JwtAuthenticationFilter.class
})
class SecurityAndApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private AuthService authService;

    @MockBean
    private OrderService orderService;

    @MockBean
    private AdminService adminService;

    @MockBean
    private CatalogService catalogService;

    private String customerToken;
    private String adminToken;

    @BeforeEach
    void setUp() {
        customerToken = jwtTokenProvider.generateToken("khachhang@fashion.vn", "CUSTOMER");
        adminToken = jwtTokenProvider.generateToken("admin@fashion.vn", "ADMIN");
    }

    @Test
    void customerCanGetMyOrdersAndChangePassword() throws Exception {
        OrderResponseDTO sampleOrder = OrderResponseDTO.builder()
                .id(1L)
                .orderCode("DH20261120ABCDE")
                .customerName("Nguyễn Văn Khách")
                .orderStatus("PENDING")
                .paymentStatus("UNPAID")
                .totalAmount(BigDecimal.valueOf(350000))
                .build();

        when(orderService.getMyOrders("khachhang@fashion.vn")).thenReturn(List.of(sampleOrder));
        doNothing().when(authService).changePassword(eq("khachhang@fashion.vn"), any(ChangePasswordRequest.class));

        // 1. GET /api/orders/my-orders -> 200 OK
        mockMvc.perform(get("/api/orders/my-orders")
                        .header("Authorization", "Bearer " + customerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].orderCode").value("DH20261120ABCDE"));

        // 2. PUT /api/auth/change-password -> 200 OK
        mockMvc.perform(put("/api/auth/change-password")
                        .header("Authorization", "Bearer " + customerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"currentPassword\":\"123456\",\"newPassword\":\"654321\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void unauthenticatedRequestsAreBlockedWith401() throws Exception {
        mockMvc.perform(get("/api/orders/my-orders"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(put("/api/auth/change-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"currentPassword\":\"123456\",\"newPassword\":\"654321\"}"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(get("/api/admin/dashboard/stats"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void customerCallingAdminEndpointsIsBlockedWith403() throws Exception {
        mockMvc.perform(get("/api/admin/dashboard/stats")
                        .header("Authorization", "Bearer " + customerToken))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/api/admin/orders")
                        .header("Authorization", "Bearer " + customerToken))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/api/admin/coupons")
                        .header("Authorization", "Bearer " + customerToken))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/api/admin/categories")
                        .header("Authorization", "Bearer " + customerToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminCanAccessAllCoreAdminEndpoints() throws Exception {
        when(adminService.getDashboardStats()).thenReturn(DashboardStatsDTO.builder()
                .totalRevenue(BigDecimal.valueOf(1250000))
                .totalOrders(5L)
                .pendingOrders(1L)
                .totalProducts(26L)
                .ordersByStatus(Map.of("PENDING", 1L, "COMPLETED", 4L))
                .build());

        when(adminService.getRevenueStats(7)).thenReturn(List.of(
                DailyRevenueDTO.builder()
                        .date("2026-11-20")
                        .label("20/11")
                        .revenue(BigDecimal.valueOf(500000))
                        .orderCount(2L)
                        .build()
        ));

        when(catalogService.getProducts(any(), any(), any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(PageResponse.<ProductDTO>builder()
                        .content(List.of(ProductDTO.builder().id(1L).name("Áo Thun Compact").slug("ao-thun-compact").build()))
                        .pageNo(0).pageSize(100).totalElements(1L).totalPages(1).isLast(true)
                        .build());

        when(adminService.getAdminOrders(any(), any(), eq(0), eq(10)))
                .thenReturn(PageResponse.<OrderResponseDTO>builder()
                        .content(List.of(OrderResponseDTO.builder().id(1L).orderCode("DH20261120ABCDE").orderStatus("PENDING").build()))
                        .pageNo(0).pageSize(10).totalElements(1L).totalPages(1).isLast(true)
                        .build());

        when(adminService.getAllCoupons()).thenReturn(List.of(
                AdminCouponDTO.builder()
                        .id(1L).code("WELCOME10").discountPercent(10)
                        .maxDiscountAmount(BigDecimal.valueOf(50000))
                        .minOrderValue(BigDecimal.valueOf(200000))
                        .expiryDate(LocalDateTime.now().plusDays(30))
                        .usageLimit(100).usedCount(2).isActive(true)
                        .build()
        ));

        when(adminService.getAllCategories()).thenReturn(List.of(
                CategoryDTO.builder().id(1L).name("Áo Thun").slug("ao-thun").productCount(5L).build()
        ));

        // 1. Dashboard stats -> 200
        mockMvc.perform(get("/api/admin/dashboard/stats")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.totalOrders").value(5));

        // 2. Revenue 7 days -> 200
        mockMvc.perform(get("/api/admin/stats/revenue?days=7")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].label").value("20/11"));

        // 3. Products list -> 200
        mockMvc.perform(get("/api/products?pageSize=100")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].slug").value("ao-thun-compact"));

        // 4. Orders paginated -> 200
        mockMvc.perform(get("/api/admin/orders?page=0&size=10")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].orderCode").value("DH20261120ABCDE"))
                .andExpect(jsonPath("$.data.totalPages").value(1));

        // 5. Coupons list -> 200
        mockMvc.perform(get("/api/admin/coupons")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].code").value("WELCOME10"));

        // 6. Categories list -> 200
        mockMvc.perform(get("/api/admin/categories")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].slug").value("ao-thun"));
    }
}
