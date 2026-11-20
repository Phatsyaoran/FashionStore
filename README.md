# 🛍️ FashionStore — Nền Tảng Thương Mại Điện Tử Thời Trang Full-Stack

![Java](https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.5-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)
![MS SQL Server](https://img.shields.io/badge/SQL%20Server-2022-CC2927?style=for-the-badge&logo=microsoftsqlserver&logoColor=white)
![Vue 3](https://img.shields.io/badge/Vue.js-3-4FC08D?style=for-the-badge&logo=vuedotjs&logoColor=white)
![Vite](https://img.shields.io/badge/Vite-5-646CFF?style=for-the-badge&logo=vite&logoColor=white)
![Pinia](https://img.shields.io/badge/Pinia-State-FFD859?style=for-the-badge&logo=vuedotjs&logoColor=black)
![Tailwind CSS](https://img.shields.io/badge/Tailwind%20CSS-3-06B6D4?style=for-the-badge&logo=tailwindcss&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-Compose-2496ED?style=for-the-badge&logo=docker&logoColor=white)

**FashionStore** là hệ thống web bán quần áo thời trang hoàn chỉnh (Production-Ready) xây dựng theo kiến trúc tách biệt **RESTful API (Spring Boot 3 + SQL Server)** và **SPA hiện đại (Vue 3 + Pinia + Tailwind CSS)**, tích hợp trợ lý tư vấn AI **Gemini + RAG kho hàng thật**, thanh toán **VietQR**, quản trị tồn kho an toàn đa luồng và đóng gói triển khai trọn gói bằng **Docker Compose**.

---

## 🏛️ Kiến Trúc Hệ Thống

```text
+---------------------------+        +----------------------------------+
|   Client (Web / Mobile)   | -----> |  Nginx (Docker) / Vite (Local)   |
|  Vue 3 + Pinia + Tailwind | <----- |  SPA Router + Reverse Proxy      |
+---------------------------+        |  /api/** & /uploads/** -> :8080  |
                                     +----------------+-----------------+
                                                      |
                                                      v
                                     +----------------------------------+
                                     |  Spring Boot 3.3.5 REST API      |
                                     |  - JWT Auth + RBAC (ROLE_ADMIN)  |
                                     |  - Pessimistic Lock Tồn Kho      |
                                     |  - File Upload (/uploads/**)     |
                                     +--------+----------------+--------+
                                              |                |
                           +------------------+                +------------------+
                           v                                                      v
            +-----------------------------+                       +-------------------------------+
            |   Microsoft SQL Server      |                       |  Gemini AI API (RAG Kho Hàng) |
            |   (FashionStoreDB)          |                       |  Timeout 8s -> Auto Fallback  |
            |   8+ Bảng chuẩn Collation   |                       |  về Rule-Based nội bộ         |
            +-----------------------------+                       +-------------------------------+
```

---

## ✨ Tính Năng Nổi Bật

### 👤 Phía Khách Hàng (Storefront)
- **Khám phá & Lọc đa tiêu chí:** Lọc kết hợp theo Danh mục, Khoảng giá, Size áo (`S–XXL`) & Size quần (`28–32`), Màu sắc, Flash Sale, Sắp xếp và Phân trang; tìm kiếm tức thì (Instant Search) ngay trên Header & Mobile Drawer.
- **Chi tiết sản phẩm & Album ảnh (Product Gallery):** Hỗ trợ nhiều ảnh phụ dạng thumbnail cuộn ngang (vuốt trượt mượt trên Mobile), chọn biến thể Size/Màu với tồn kho thật, gợi ý chọn size thông minh và bộ phối đồ **Mix & Match**.
- **Đánh giá kèm vóc dáng (Fit Reviews):** Hiển thị thông số chiều cao/cân nặng, tỷ lệ Chật/Vừa vặn/Rộng, lọc theo số sao và xác thực nhãn **`✓ Đã mua hàng`** dựa trên đơn `COMPLETED` thực tế.
- **Giỏ hàng, Coupon & Thanh toán VietQR:** Thanh tiến trình Freeship đơn ≥ 500k, áp dụng mã giảm giá kiểm tra hạn dùng/giới hạn lượt, đặt hàng COD hoặc quét mã **VietQR QuickLink**.
- **Hồ sơ cá nhân (`/profile`) & Tra cứu đơn (`/order-tracking`):** Xem lịch sử đơn hàng của tôi, tự hủy đơn đang `PENDING` (tự động hoàn tồn kho), đổi mật khẩu BCrypt; tra cứu công khai tự động che một phần SĐT và địa chỉ bảo vệ PII.
- **Trợ lý AI tư vấn phối đồ (`ChatWidget`):** Kết nối **Gemini API** kèm dữ liệu kho hàng & mã giảm giá thật; tự động fallback về bộ xử lý rule-based khi không có key hoặc timeout.

### 🛠️ Phía Quản Trị (Admin Portal)
- **Dashboard trực quan (`/admin`):** Thống kê doanh thu thực tế (đã loại đơn `CANCELLED`), biểu đồ cột & đường **Chart.js** theo 7 ngày / 30 ngày, bảng cảnh báo biến thể sắp hết hàng (≤ 5 chiếc) kèm sửa kho nhanh tại chỗ.
- **CRUD Sản phẩm, Biến thể & Upload ảnh (`/admin/products`):** Thêm/sửa/xóa an toàn sản phẩm, upload ảnh đại diện & album nhiều ảnh phụ trực tiếp lên server, quản lý dòng biến thể Size/Màu/SKU/Giá/Tồn kho, bật/tắt nhanh Flash Sale và xuất tồn kho `.CSV` chuẩn UTF-8 BOM.
- **Quản lý Đơn hàng & In phiếu A6 (`/admin/orders`):** Tìm kiếm theo mã đơn/tên/SĐT, lọc theo trạng thái, phân trang chống N+1 query, chặn chuyển ngược trạng thái, hoàn tồn kho & coupon khi hủy đơn, xuất `.CSV` và in phiếu giao hàng **khổ A6** có mã vạch **Code128** thật (`JsBarcode`).
- **Quản lý Mã giảm giá & Danh mục (`/admin/coupons`, `/admin/categories`):** CRUD đầy đủ mã khuyến mãi (bật/tắt kích hoạt nhanh) và danh mục sản phẩm (chặn xóa danh mục đang có sản phẩm).

---

## ⚙️ Điểm Nhấn Kỹ Thuật

- **Bảo mật & Phân quyền chặt chẽ:** Spring Security 6 + Stateless JWT (HS256) + BCrypt, khóa cứng toàn bộ `/api/admin/**` bằng `.hasRole("ADMIN")`, tự động logout khi HTTP `401`, thu hẹp CORS theo whitelist cấu hình qua biến môi trường, chống XSS trong ChatWidget.
- **Toàn vẹn tồn kho đồng thời (Concurrency):** Sử dụng `@Lock(LockModeType.PESSIMISTIC_WRITE)` trên truy vấn `ProductVariant` trong `@Transactional` khi đặt hàng nhằm chống bán âm kho, sinh mã đơn ngẫu nhiên `DHyyyyMMddXXXXX` chống trùng tuyệt đối.
- **Tự động hoàn kho & hoàn mã:** Khi Admin hoặc khách hàng hủy đơn, hệ thống tự cộng trả `stockQuantity` cho từng biến thể và giảm `usedCount` của coupon.
- **Hiệu năng & UX Mobile:** Truy vấn đơn hàng Admin dùng batch `findByOrderIdIn` loại bỏ N+1; ảnh sản phẩm dùng `loading="lazy"` + fallback `@error`; hệ thống Toast dùng chung thay thế hoàn toàn `alert()`; Mobile Hamburger Drawer mượt mà.
- **Sẵn sàng triển khai Production:** Multi-stage Dockerfiles (`eclipse-temurin:21` & `node:20 -> nginx:alpine`), Docker Compose kèm healthcheck + volume bền vững, và GitHub Actions CI tự động kiểm tra build.

---

## 🚀 Hướng Dẫn Chạy Nhanh

### Cách 1: Chạy Local trên Windows (Phát triển)
**Yêu cầu:** JDK 21, Node.js 20+, Microsoft SQL Server (chạy cổng `1433`).
1. Nạp file `database/schema_and_seed.sql` và `database/update_real_fashion_images.sql` vào SQL Server.
2. Chạy file `start_all.bat` ở thư mục gốc để khởi động đồng thời Backend (`:8080`), Frontend (`:5173`) và Ngrok (nếu có).
3. Truy cập `http://localhost:5173` (Swagger UI tại `http://localhost:8080/swagger-ui.html`).
4. Để dừng toàn bộ dịch vụ, chạy `stop_all.bat`.

### Cách 2: Chạy trọn gói bằng Docker Compose
**Yêu cầu:** Docker Desktop / Docker Engine + Docker Compose v2.
```bash
# 1. Tạo file biến môi trường từ mẫu
cp .env.example .env

# 2. Build và khởi chạy 3 container (mssql, backend, frontend)
docker compose up -d --build
```
Xem hướng dẫn nạp SQL lần đầu và triển khai lên VPS Linux chi tiết tại [DEPLOY.md](./DEPLOY.md).

---

## 🔑 Tài Khoản Demo

| Vai trò | Email | Mật khẩu | Ghi chú |
|---|---|---|---|
| **Quản trị viên (Admin)** | `admin@fashion.vn` | `123456` | Truy cập `/admin`, `/admin/products`, `/admin/orders`, `/admin/coupons`, `/admin/categories` |
| **Khách hàng (Customer)** | `khachhang@fashion.vn` | `123456` | Đặt hàng, xem `/profile`, tự hủy đơn `PENDING`, đổi mật khẩu |

---

## 📁 Cấu Trúc Thư Mục

```text
FashionStore/
├── .github/workflows/ci.yml      # GitHub Actions CI (Backend JDK 21 + Frontend Node 20)
├── backend-java/                 # Spring Boot 3.3.5 REST API + Dockerfile
│   ├── src/main/java/com/fashion/store/
│   │   ├── config/               # SecurityConfig, CorsConfig, WebMvcConfig, OpenApiConfig
│   │   ├── controller/           # Auth, Catalog, Order, Coupon, ChatAssistant, Admin
│   │   ├── dto/                  # Request/Response DTOs + Bean Validation
│   │   ├── entity/               # JPA Entities (Product, Variant, ProductImage, Order, ...)
│   │   ├── repository/           # Spring Data JPA + Specifications + Pessimistic Lock
│   │   ├── security/             # JwtTokenProvider, JwtAuthenticationFilter
│   │   └── service/              # Business Logic, Gemini RAG, CSV Export
│   └── .env.example
├── frontend-vue/                 # Vue 3 + Vite + Pinia + Tailwind + Nginx Dockerfile
│   ├── src/
│   │   ├── components/           # Header, Footer, ProductCard, ChatWidget, ToastContainer, ...
│   │   ├── router/               # Vue Router + Auth/Admin Guards + Dynamic Title
│   │   ├── services/             # Axios Interceptor (JWT + 401 handler), Formatters
│   │   ├── stores/               # Pinia (auth, cart, wishlist, product, toast)
│   │   └── views/                # Storefront views + views/admin/*
│   └── nginx.conf
├── database/                     # SQL Server schema, seed data & real Unsplash images
├── docs/screenshots/             # Thư mục chứa ảnh chụp màn hình minh họa
├── docker-compose.yml            # Điều phối mssql + backend + frontend
├── DEPLOY.md                     # Tài liệu triển khai Docker & VPS Linux
└── .env.example                  # Mẫu biến môi trường Docker Compose
```

---

## 📸 Ảnh Chụp Màn Hình (Screenshots)

> Đặt các file ảnh chụp màn hình vào thư mục `docs/screenshots/` theo đúng tên file bên dưới để tự động hiển thị trên GitHub.

| Trang Chủ & Flash Sale | Chi Tiết Sản Phẩm & Trợ Lý AI |
| :---: | :---: |
| ![Trang chủ](docs/screenshots/home.png) | ![Chi tiết sản phẩm & AI Chat](docs/screenshots/product-detail-ai.png) |
| **Giao Diện Mobile & Drawer** | **Admin Dashboard (Biểu Đồ Doanh Thu)** |
| ![Mobile Drawer](docs/screenshots/mobile-drawer.png) | ![Admin Dashboard](docs/screenshots/admin-dashboard.png) |
| **Quản Lý Sản Phẩm & Album Ảnh** | **In Phiếu Giao Hàng A6 (Code128)** |
| ![Admin Products](docs/screenshots/admin-products.png) | ![Phiếu in A6](docs/screenshots/thermal-label-a6.png) |
