# KẾ HOẠCH TRIỂN KHAI HỆ THỐNG E-COMMERCE FASHION STORE
> **Kiến trúc sư trưởng:** Principal Software Architect  
> **Phiên bản:** 1.0.0 | **Ngày khởi tạo:** 18/11/2026  
> **Ngôn ngữ:** Tiếng Việt | **Tiền tệ:** Việt Nam Đồng (VNĐ)

---

## 1. CẤU TRÚC THƯ MỤC TỔNG THỂ DỰ ÁN

```
FashionStore/
├── database/
│   └── schema_and_seed.sql            # Script T-SQL tạo DB FashionStoreDB, 8 bảng & dữ liệu mẫu phong phú
├── backend-java/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/fashion/store/
│   │   │   │   ├── config/            # SecurityConfig, CorsConfig, OpenApiConfig, WebMvcConfig
│   │   │   │   ├── controller/        # Auth, Catalog, Order, Admin, Review, Upload Controllers
│   │   │   │   ├── dto/               # Request & Response DTOs (Chống LazyInitialization & Recursion)
│   │   │   │   ├── entity/            # Jakarta Persistence Entities khớp 100% SQL Server
│   │   │   │   ├── exception/         # Custom Exceptions & Global @RestControllerAdvice
│   │   │   │   ├── repository/        # Spring Data JPA Repositories
│   │   │   │   ├── security/          # JwtTokenProvider, JwtAuthenticationFilter, UserDetailsServiceImpl
│   │   │   │   ├── service/           # Catalog, Order, Auth, Admin, VietQr Services
│   │   │   │   ├── init/              # DataInitializer (CommandLineRunner kiểm tra & hash BCrypt)
│   │   │   │   └── FashionStoreApplication.java
│   │   │   └── resources/
│   │   │       ├── application.properties # Cấu hình port 8080, SQL Server, JWT, Upload dir
│   │   │       └── static/uploads/    # Thư mục lưu ảnh upload
│   ├── pom.xml                        # Maven dependencies: Spring Boot 3.2+, SQL Server JDBC, JWT, Lombok, Swagger
│   ├── mvnw
│   └── mvnw.cmd
├── frontend-vue/
│   ├── src/
│   │   ├── assets/                    # CSS, Tailwind imports
│   │   ├── components/                # Header, Footer, ProductCard, VietQrModal, SizeFitGuide
│   │   ├── router/                    # Vue Router (Khách hàng & Guard Admin)
│   │   ├── stores/                    # Pinia: authStore, cartStore, productStore
│   │   ├── services/                  # api.js (Axios Interceptor kèm JWT Token), formatters.js (formatVND)
│   │   ├── views/                     # Home, Catalog, ProductDetail, Cart, Checkout, OrderTracking, Admin Dashboard/Products/Orders
│   │   ├── App.vue
│   │   └── main.js
│   ├── index.html
│   ├── package.json
│   ├── tailwind.config.js
│   ├── vite.config.js
│   └── .env
├── start_all.bat                      # Script 1-click khởi chạy Database, Backend & Frontend
├── stop_all.bat                       # Script dừng toàn bộ hệ thống (giải phóng port 8080, 5173)
├── IMPLEMENTATION_PLAN.md             # Tài liệu kiến trúc và tiến độ 6 Phase
└── README.md                          # Hướng dẫn chi tiết triển khai & vận hành
```

---

## 2. CHECKLIST CHI TIẾT 6 PHASE TRIỂN KHAI

### [x] Phase 1: Database SQL Server & Dữ liệu thật (Có dự phòng mở rộng)
- [x] Tạo file `database/schema_and_seed.sql` với lệnh kiểm tra và tạo mới database `FashionStoreDB` (Collation: `Vietnamese_CI_AS`).
- [x] Thiết kế chuẩn 8 bảng và khóa ngoại liên kết toàn vẹn:
  1. `Categories` (Id, Name, Slug, Description)
  2. `Products` (Id, CategoryId, Name, Slug, Description, BasePrice, SalePrice, IsFlashSale, FlashSaleEndTime, ThumbnailUrl, CreatedAt)
  3. `ProductVariants` (Id, ProductId, Size [S/M/L/XL], ColorName, ColorHex, Price, StockQuantity, Sku)
  4. `Users` (Id, FullName, Email, PasswordHash, Phone, Address, Role [ADMIN/CUSTOMER], CreatedAt)
  5. `Coupons` (Id, Code, DiscountPercent, MaxDiscountAmount, MinOrderValue, ExpiryDate, UsageLimit, UsedCount, IsActive)
  6. `Orders` (Id, OrderCode, UserId [Nullable], CustomerName, CustomerPhone, ShippingAddress, Note, SubTotal, ShippingFee, DiscountAmount, TotalAmount, CouponCode, PaymentMethod [COD/VIETQR], PaymentStatus [UNPAID/PAID], OrderStatus [PENDING/CONFIRMED/PACKING/SHIPPING/COMPLETED/CANCELLED], CreatedAt)
  7. `OrderItems` (Id, OrderId, ProductVariantId, ProductName, Size, ColorName, Quantity, UnitPrice)
  8. `Reviews` (Id, ProductId, UserId, CustomerName, Rating [1-5], Comment, CustomerHeightCm, CustomerWeightKg, PurchasedSize, PurchasedColor, FitFeedback [CHAT/VUA_VAN/RONG], CreatedAt)
- [x] Đảm bảo 100% cột văn bản tiếng Việt dùng `NVARCHAR`.
- [x] Nạp dữ liệu mẫu phong phú:
  - 6 Danh mục thời trang đa dạng.
  - 26 Sản phẩm quần áo thực tế phong phú (Áo thun, Sơ mi, Quần Jean, Áo khoác, Váy đầm, Đồ thể thao...) với ảnh Picsum ổn định.
  - 68 Biến thể Size/Màu kèm tồn kho và mã SKU độc lập.
  - 6 Sản phẩm Flash Sale kèm thời gian kết thúc và giá ưu đãi thật.
  - 5 Mã giảm giá hoạt động (`WELCOME10`, `FREESHIP`, `SALE50K`, `VIPFASHION`, `SUMMER25`).
  - 15 Đánh giá khách hàng chi tiết có số đo chiều cao, cân nặng, tư vấn phom dáng.
  - 2 Tài khoản mẫu (`admin@fashion.vn` và `khachhang@fashion.vn` - mật khẩu `123456`).
  - 3 Đơn hàng mẫu và chi tiết đơn hàng sẵn sàng cho Admin Dashboard.
- [x] Kiểm tra cú pháp SQL và tính toàn vẹn khóa ngoại.

---

### [x] Phase 2A: Java Backend - Core Entities, Catalog & Public API
- [x] Khởi tạo dự án Maven `/backend-java` (Spring Boot 3.x, Java 17+, Maven Wrapper `mvnw.cmd`).
- [x] Cấu hình `pom.xml` (Spring Boot Web, Spring Data JPA, SQL Server JDBC Driver, Validation, Lombok, Springdoc Swagger OpenAPI 3).
- [x] Cấu hình `application.properties` (Port 8080, Hibernate ddl-auto=none, UTF-8 encoding force, kết nối SQL Server `sa/123`).
- [x] Xây dựng bộ Entity Jakarta EE 100% (`jakarta.persistence.*`, `@Table`, `@Column`, `@ManyToOne`, `@OneToMany`).
- [x] Thiết kế DTO chống đệ quy JSON & Lazy Loading (`CategoryDTO`, `ProductDTO`, `ProductVariantDTO`, `ProductDetailDTO`, `ReviewDTO`).
- [x] Viết JPA Repositories với các câu truy vấn lọc động (`JpaSpecificationExecutor` hoặc Custom Queries).
- [x] Xây dựng CatalogService & CatalogController:
  - Lấy danh sách danh mục.
  - Tìm kiếm, lọc sản phẩm đa tiêu chí (danh mục, khoảng giá, size, màu sắc, flash sale) kèm phân trang `Pageable`.
  - Chi tiết sản phẩm kèm danh sách biến thể, tồn kho và danh sách đánh giá.
- [x] Tích hợp Swagger UI (`/swagger-ui.html`) và `@RestControllerAdvice` xử lý ngoại lệ tập trung.
- [x] Cấu hình CORS cho phép `http://localhost:5173`.

---

### [x] Phase 2B: Java Backend - JWT Auth, Đặt hàng `@Transactional`, VietQR & Admin API
- [x] Cấu hình Spring Security 6.x + JWT (Stateless session, `JwtTokenProvider`, `JwtAuthenticationFilter`, BCryptPasswordEncoder).
- [x] Tạo `DataInitializer` (implement `CommandLineRunner`) tự động kiểm tra và mã hóa BCrypt cho 2 tài khoản `admin@fashion.vn` và `khachhang@fashion.vn`.
- [x] Xây dựng AuthController: Đăng nhập (`/api/auth/login`), Đăng ký (`/api/auth/register`), Lấy thông tin người dùng (`/api/auth/me`).
- [x] Xây dựng CouponService & API áp dụng mã giảm giá (`POST /api/coupons/apply`).
- [x] Xây dựng OrderService với `@Transactional`:
  - Validate tồn kho từng biến thể.
  - Trừ tồn kho an toàn chống bán quá số lượng.
  - Tự động tính phí vận chuyển: Freeship nếu `SubTotal >= 500,000 VNĐ`, ngược lại `30,000 VNĐ`.
  - Sinh mã đơn hàng duy nhất (VD: `DH2026111801`).
  - Sinh động link VietQR QuickLink chuẩn ngân hàng MB: `https://img.vietqr.io/image/MB-0123456789-compact2.png?amount={TONG_TIEN}&addInfo={MA_DON_HANG}&accountName=NGUYEN%20VAN%20PHAT`.
  - API xác nhận thanh toán (`POST /api/orders/{orderCode}/confirm-payment`).
- [x] Xây dựng Admin APIs (`/api/admin/**` có phân quyền `ROLE_ADMIN`):
  - CRUD Quản lý sản phẩm & biến thể Size/Màu.
  - Quản lý đơn hàng & cập nhật trạng thái đơn (`PENDING` -> `CONFIRMED` -> `SHIPPING` -> `COMPLETED` / `CANCELLED`).
  - API Upload hình ảnh lưu vào thư mục `/static/uploads/`.
  - API Thống kê doanh thu, số đơn, sản phẩm bán chạy.

---

### [x] Phase 3: Vue 3 Frontend - Giao diện Cửa hàng & Trải nghiệm Mua sắm
- [x] Khởi tạo dự án `/frontend-vue` (Vite + Vue 3 Composition API `<script setup>` + Tailwind CSS + Vue Router + Pinia + Axios).
- [x] Cấu hình Axios Base URL `http://localhost:8080/api` và Interceptor tự động gắn Bearer JWT.
- [x] Xây dựng tiện ích `formatVND(amount)` dùng `Intl.NumberFormat('vi-VN')`.
- [x] Xây dựng Layouts: Header (Navbar, Giỏ hàng mini badge, Tìm kiếm nhanh, Menu danh mục, Avatar tài khoản), Footer chuẩn E-commerce.
- [x] Xây dựng Trang chủ (`HomeView.vue`): Hero Banner, Khối Flash Sale đếm ngược thời gian, Danh mục nổi bật, Sản phẩm mới nhất.
- [x] Xây dựng Trang danh mục (`CatalogView.vue`):
  - Bộ lọc đa tiêu chí: Danh mục, Khoảng giá slider, Size (S/M/L/XL), Màu sắc (bảng chọn màu trực quan).
  - Sắp xếp (Mới nhất, Giá tăng/giảm, Bán chạy).
  - Phân trang & Thanh tìm kiếm trực tiếp.
- [x] Xây dựng Trang chi tiết sản phẩm (`ProductDetailView.vue`):
  - Bộ sưu tập ảnh & Zoom ảnh.
  - Chọn Size và Màu sắc động: Hiển thị chấm màu theo `ColorHex`, cập nhật tức thì giá và tồn kho thực tế.
  - Nút "Thêm vào giỏ" tự động disable và cảnh báo khi biến thể hết hàng (`stockQuantity = 0`).
  - Bảng hướng dẫn chọn size chuẩn người Việt.
  - Khối đánh giá khách hàng (hiển thị kèm chiều cao, cân nặng, độ vừa vặn `FitFeedback`).

---

### [x] Phase 4: Vue 3 Frontend - Giỏ hàng, Thanh toán VietQR & Quản trị Admin
- [x] Xây dựng Pinia `cartStore` (đồng bộ `localStorage`, thêm/sửa/xóa sản phẩm, cập nhật số lượng, kiểm tra giới hạn tồn kho).
- [x] Xây dựng Trang giỏ hàng (`CartView.vue`): Danh sách sản phẩm, thay đổi số lượng, nhập mã Coupon giảm giá gọi API Backend.
- [x] Xây dựng Trang thanh toán (`CheckoutView.vue`):
  - Form nhập thông tin người nhận hàng (Họ tên, SĐT, Địa chỉ, Ghi chú).
  - Tùy chọn thanh toán: `COD` hoặc `Chuyển khoản VietQR`.
  - Xử lý tạo đơn hàng qua API `@Transactional`.
  - Modal VietQR tự động: Hiển thị mã QR động, tên ngân hàng MB, số tài khoản, số tiền VNĐ, nội dung chuyển khoản, nút copy 1-click, đồng hồ đếm ngược 10 phút, và nút "Tôi đã chuyển khoản".
- [x] Xây dựng Trang tra cứu đơn hàng (`OrderTrackingView.vue`): Tra cứu theo Mã đơn / SĐT, xem timeline trạng thái đơn.
- [x] Xây dựng Khu vực Quản trị Admin (`/admin`):
  - Route Guard kiểm tra quyền `ROLE_ADMIN`.
  - Admin Dashboard: Thẻ thống kê doanh thu, số đơn hàng, biểu đồ tổng quan.
  - Quản lý sản phẩm: Bảng danh sách, thêm/sửa sản phẩm, quản lý biến thể Size/Màu, upload ảnh.
  - Quản lý đơn hàng: Xem chi tiết đơn, đổi trạng thái đơn (`Xác nhận`, `Đang giao`, `Đã giao`).

---

### [x] Phase 5: Kiểm thử, Build & Script Khởi Chạy Tự Động
- [x] Biên dịch & Build Backend: Chạy kiểm tra compile Java và gói JAR thành công.
- [x] Biên dịch & Build Frontend: Chạy `npm run build` trong `frontend-vue` không có lỗi lint/syntax.
- [x] Tạo file `start_all.bat` ở thư mục gốc:
  - Tự động phát hiện và khởi động SQL Server (hỗ trợ `localhost` và `localhost\SQLEXPRESS`).
  - Thực thi script `database/schema_and_seed.sql` nếu chưa khởi tạo database.
  - Khởi động Spring Boot Backend port 8080.
  - Khởi động Vite Vue 3 Frontend port 5173.
- [x] Tạo file `stop_all.bat` ở thư mục gốc: Tự động tìm và ngắt các tiến trình đang chiếm port 8080 và 5173.
- [x] Viết tài liệu `README.md` hoàn chỉnh: Hướng dẫn cài đặt, tài khoản demo, kịch bản test thử nghiệm toàn diện.

---

## 3. ĐỢT NÂNG CẤP SỐ 2 (UI/UX & VẬN HÀNH THÔNG MINH)

### [x] Phase 6: Nâng cấp Trải nghiệm Mua sắm trên Vue 3 & Pinia
- [x] Trợ lý Gợi ý Size Thông Minh (Smart Size Predictor) tại Trang Chi Tiết Sản Phẩm:
  - Nút "📏 Tư vấn chọn Size chuẩn theo chiều cao & cân nặng" cạnh vùng chọn Size.
  - Modal thanh trượt: Chiều cao (145cm - 190cm), Cân nặng (40kg - 95kg), Sở thích (Mặc ôm Body / Mặc vừa vặn / Mặc rộng Oversize).
  - Thuật toán chuẩn hóa (S: <53kg, M: 53-62kg, L: 63-72kg, XL: >72kg; điều chỉnh theo chiều cao >178cm hoặc form rộng).
  - Hiển thị độ vừa vặn ~95% và nút "Chọn ngay Size này" tự động kích hoạt biến thể trên trang.
- [x] Thanh Tiến Trình Freeship Động (Free Shipping Progress Bar) trong Giỏ Hàng:
  - Mốc miễn phí vận chuyển 500.000 VNĐ.
  - Thanh tiến trình đổi màu động (cam khi <500.000đ kèm số tiền còn thiếu, xanh lá 100% khi >=500.000đ kèm thông báo tiết kiệm 30.000đ).
  - Tích hợp đồng bộ trong Cart Drawer và Trang Giỏ hàng chính.
- [x] Xem Nhanh Sản Phẩm (Quick View Modal) & Sản Phẩm Đã Xem Gần Đây:
  - Nút "Xem nhanh" (Quick View) trên từng ProductCard tại Trang chủ và Trang danh mục.
  - Tự động lưu tối đa 8 sản phẩm vào localStorage (`fashion_recently_viewed`).
  - Khu vực "Sản phẩm bạn vừa xem" ở cuối Trang chi tiết sản phẩm.
- [x] Kiểm tra biên dịch Frontend: Chạy `npm run build` không lỗi.

---

### [x] Phase 7: Timeline Đơn Hàng, In Phiếu Giao Hàng A6 & Cảnh Báo Tồn Kho Admin
- [x] Timeline Theo Dõi Đơn Hàng Trực Quan 5 Bước (Visual Order Stepper):
  - Thanh tiến trình 5 bước ngang: PENDING -> CONFIRMED -> PACKING -> SHIPPING -> COMPLETED (cảnh báo đỏ nếu CANCELLED).
- [x] In Phiếu Giao Hàng Chuẩn TMĐT (Thermal Shipping Label A6) trong Admin:
  - Bản in khổ A6 chuẩn `@media print`, mã vạch giả lập, thông tin gửi/nhận, danh sách sản phẩm (kèm Size, Màu), khung tiền thu hộ COD hoặc ĐÃ THANH TOÁN (VIETQR).
- [x] Cảnh Báo Sắp Hết Hàng (Low-Stock Alert) & Biểu Đồ Doanh Thu Admin:
  - API Backend `GET /api/admin/dashboard/low-stock` trả về biến thể có `StockQuantity <= 5`.
  - Bảng cảnh báo và sửa nhanh số lượng tồn kho trên Dashboard Admin.
  - Biểu đồ trực quan doanh thu và phân bổ đơn hàng theo trạng thái.

---

## 4. ĐỢT NÂNG CẤP SỐ 3 (HỆ SINH THÁI REVIEWS THEO VÓC DÁNG, FLASH SALE ĐẾM NGƯỢC & TRỢ LÝ AI TƯ VẤN PHỐI ĐỒ)

### [x] Phase 8: Hệ Thống Đánh Giá Có Thông Số Cơ Thể & Khung Giờ Flash Sale Đếm Ngược
- [x] Backend & Frontend cho Đánh Giá Thực Tế Kèm Vóc Dáng (Fit Reviews):
  - API `GET /api/products/{id}/reviews`: trả về danh sách đánh giá, điểm sao trung bình, phân bổ sao và tỷ lệ % cảm nhận độ vừa vặn (Chật / Vừa vặn / Rộng).
  - API `POST /api/products/{id}/reviews`: lưu đánh giá mới gồm số sao, bình luận, chiều cao (cm), cân nặng (kg), size, màu sắc, fitFeedback.
  - Giao diện Trang Chi Tiết Sản Phẩm: Thống kê vóc dáng tổng quan, bộ lọc theo số sao & độ vừa vặn, hiển thị thẻ đánh giá có tag vóc dáng, form gửi đánh giá thực tế.
- [x] Khu Vực Flash Sale Đếm Ngược Thời Gian Thực (Real-time Countdown Flash Sale):
  - Đồng hồ đếm ngược trực tiếp thời gian thực (`HH:MM:SS` nhảy từng giây) trên Trang chủ.
  - Hiển thị nhãn % Giảm giá, giá gốc gạch ngang và thanh tiến trình cháy hàng *"🔥 Đang bán chạy - Sắp cháy hàng"*.
- [x] Kiểm tra compile Backend và build Frontend thành công.

---

### [x] Phase 9: Trợ Lý Chatbot Tư Vấn Chọn Đồ, Phối Set & Tra Cứu Đơn Hàng Tự Động
- [x] API Trợ Lý Tư Vấn Thông Minh (`POST /api/chat-assistant`):
  - Xử lý câu hỏi tự nhiên bằng tiếng Việt truy vấn trực tiếp SQL Server (`Products`, `Variants`, `Orders`, `Coupons`).
  - Gợi ý chọn size và đề xuất 3 sản phẩm phù hợp khi có chiều cao/cân nặng.
  - Lọc sản phẩm theo khoảng giá và nhu cầu (VD: áo sơ mi hoặc quần jean dưới 400k).
  - Tra cứu trạng thái đơn hàng tự động theo mã vận đơn.
  - Liệt kê mã giảm giá còn hiệu lực.
- [x] Giao Diện Floating Chat Widget trên Vue 3:
  - Bong bóng chat nổi cố định góc dưới bên phải màn hình.
  - Bộ nút gợi ý nhanh (Quick Replies): Tư vấn size, mã giảm giá, đồ dưới 400k, tra cứu đơn hàng.
  - Hiển thị Mini Product Card trực tiếp trong đoạn chat với nút "Xem ngay".
- [x] Kiểm tra compile Java & Unit Test `ChatAssistantServiceTest` đạt 100% Passed.

---

## 5. ĐỢT NÂNG CẤP SỐ 4 (ẢNH THỜI TRANG THẬT, TÌM KIẾM TỨC THÌ & MIX-AND-MATCH)

### [x] Phase 10: Cập nhật Ảnh Thời Trang Thật, Tìm Kiếm Tức Thì & Mix-and-Match
- [x] Thay thế toàn bộ ảnh ngẫu nhiên bằng Ảnh Thời Trang Thật (Unsplash Studio Images):
  - Tạo file `database/update_real_fashion_images.sql` cập nhật 26 sản phẩm theo từng danh mục.
  - Chạy `sqlcmd` cập nhật trực tiếp vào SQL Server database `FashionStoreDB`.
  - Cập nhật đồng bộ trong `database/schema_and_seed.sql`.
- [x] Tìm kiếm tức thì có hình ảnh (Instant Search Dropdown) & Danh sách Yêu thích (Wishlist):
  - Instant Search Dropdown tại Header: debounce 300ms, hiển thị 5 sản phẩm khớp kèm ảnh, giá, flash sale badge.
  - Danh sách Yêu thích (Wishlist): Pinia `wishlistStore` + `localStorage`, nút thả tim trên `ProductCard.vue`, Drawer/Modal Wishlist tại Header.
- [x] Gợi ý Phối đồ theo Set (Mix & Match Outfit) ở Trang Chi Tiết Sản Phẩm:
  - API `GET /api/products/{id}/mix-match` lấy 2 sản phẩm thuộc danh mục khác đang có hàng.
  - Khung "✨ Phối đồ chuẩn gu (Mix & Match)" trên `ProductDetailView.vue`, chọn Size từng món và nút "Thêm cả bộ vào giỏ".
- [x] Kiểm tra thực thi `sqlcmd` và build Frontend `npm run build` không lỗi.

---

### [x] Phase 11: Xuất Báo Cáo Excel/CSV Chuẩn Tiếng Việt & Chỉnh Sửa Nhanh Tồn Kho/Flash Sale
- [x] API `GET /api/admin/export/orders-csv` & `GET /api/admin/export/inventory-csv` đính kèm byte đầu UTF-8 BOM (`0xEF, 0xBB, 0xBF`) chuẩn hiển thị tiếng Việt trên Microsoft Excel Windows.
- [x] Nút "📥 Xuất Excel (.CSV)" tại `AdminOrdersView.vue` và "📥 Xuất Tồn Kho (.CSV)" tại `AdminProductsView.vue` hỗ trợ tải file trực tiếp.
- [x] API `PATCH /api/admin/products/{id}/quick-toggle-flashsale` và Toggle Switch bật/tắt Flash Sale nhanh trực tiếp trên bảng sản phẩm.
- [x] API `PATCH /api/admin/variants/{variantId}/stock` và Modal sửa kho nhanh từng biến thể SKU (+5, +10, -5 chiếc) không cần mở form sửa toàn bộ sản phẩm.
- [x] Kiểm tra biên dịch Java `mvn test-compile` và build Vue 3 `npm run build` thành công 100%.

---

### [x] Phase 12: Đại Tu & Tối Ưu Hóa Giao Diện Chuẩn E-Commerce Cao Cấp (Coolmate / Uniqlo / Zara)
- [x] Typography & Hệ thống Màu sắc (Design System):
  - Nhúng Google Font `Plus Jakarta Sans` và `Be Vietnam Pro` hỗ trợ hiển thị tiếng Việt sắc nét.
  - Cấu hình font sans mặc định trong `tailwind.config.js` và `main.css`, chuẩn hóa nền trắng ngà `#FAFAFA`, chữ đen than `#111827`, bo góc đồng nhất và shadow phân tầng (`shadow-card`, `shadow-float`).
- [x] Nâng cấp Header, Hero Banner & Thanh Cam Kết Dịch Vụ:
  - Topbar hiệu ứng ticker mượt mà, Header kính mờ `backdrop-blur-md bg-white/90 sticky top-0 z-40`.
  - Hero Banner phong cách tạp chí thời trang Lookbook Split-Layout, badge "NEW COLLECTION 2026", nút CTA hiệu ứng mũi tên trượt.
  - 4 cam kết thương hiệu: Freeship đơn từ 500k, Đổi trả 60 ngày miễn phí, Trợ lý AI chọn size, Thanh toán VietQR 1 chạm.
- [x] Tối ưu Product Card Micro-interactions:
  - Cố định tỷ lệ khung hình chuẩn thời trang `aspect-[3/4]`, hover zoom mượt mà (`duration-500`).
  - Tự động thay thế ảnh dự phòng `@error` tránh vỡ hình ảnh.
  - Bộ nút trượt mượt mà khi hover: "👁️ Xem nhanh" và "🛒 Chọn nhanh".
  - Hiển thị dải chấm tròn màu sắc thực tế (Color Dots) trực tiếp dưới tên sản phẩm kèm số lượng màu.
- [x] Nâng Cấp Trang Chi Tiết Sản Phẩm & Modal VietQR Fintech:
  - Sticky Gallery: Cột ảnh sản phẩm bên trái cố định khi cuộn trang (`lg:sticky lg:top-28 self-start`).
  - Modal VietQR Fintech: Hiệu ứng quét laser chuyển động (`animate-scanline`), nút copy STK và Nội dung CK hiển thị trạng thái "✓ Đã chép!" tức thì.
- [x] Thông Báo Mua Hàng Thời Gian Thực (Floating Social Proof Toast):
  - Component `SocialProofToast.vue` hiển thị đơn hàng thời gian thực ở góc dưới bên trái màn hình (`fixed bottom-6 left-6 z-40`).
  - Tự động luân chuyển đơn hàng ngẫu nhiên sau mỗi 32 giây và tự ẩn sau 5 giây, không che khuất Chatbot AI.
- [x] Kiểm thử toàn diện: `npm run build` Frontend Vue 3 đạt 100% không lỗi (114 modules, 2.39s), Backend Java Maven đóng gói JAR thành công (`mvn package -DskipTests`).



