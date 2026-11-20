# WEBSITE THƯƠNG MẠI ĐIỆN TỬ BÁN QUẦN ÁO (FASHION STORE)
> **Kiến trúc sư trưởng:** Principal Software Architect  
> **Ngôn ngữ:** Tiếng Việt | **Tiền tệ:** Việt Nam Đồng (VNĐ) | **Trạng thái:** Hoàn chỉnh 100%

---

## 1. TỔNG QUAN HỆ THỐNG & TECH STACK

Hệ thống E-Commerce Fashion Store được thiết kế theo mô hình kiến trúc phân lớp hiện đại (Layered Clean Architecture), chuẩn hóa dữ liệu tiếng Việt với Microsoft SQL Server, Backend Spring Boot 3 và Frontend Vue 3 Composition API.

```
+-------------------------------------------------------------+
|             Vue 3 (Vite + Pinia + Tailwind CSS)            |
|       Cửa hàng thời trang, Giỏ hàng, Thanh toán VietQR      |
+-------------------------------------------------------------+
                              |
                     RESTful API / JSON
                              |
+-------------------------------------------------------------+
|       Spring Boot 3.3.5 (Java 21, Jakarta EE, JPA)         |
|  Catalog, Order @Transactional, Spring Security, Swagger UI |
+-------------------------------------------------------------+
                              |
                        JDBC Driver
                              |
+-------------------------------------------------------------+
|              Microsoft SQL Server (T-SQL)                   |
|        Database FashionStoreDB (Collation Vietnamese)       |
+-------------------------------------------------------------+
```

### Công nghệ cốt lõi:
- **Backend:** Java 21 LTS, Spring Boot 3.3.5, Spring Data JPA, Spring Security, JJWT 0.11.5, Lombok, Jakarta Validation, Springdoc Swagger OpenAPI 3.
- **Database:** Microsoft SQL Server 2022 (Collation: `Vietnamese_CI_AS`, 100% trường tiếng Việt dùng `NVARCHAR`).
- **Frontend:** Node.js, Vue 3 (`<script setup>`), Vite 5, Pinia 2, Vue Router 4, Tailwind CSS 3, Axios.
- **Thanh toán tự động:** Chuẩn VietQR QuickLink ngân hàng Quân Đội (MB Bank).

---

## 2. TÀI KHOẢN MẪU & THÔNG SỐ KẾT NỐI

### 2.1. Cơ sở dữ liệu SQL Server:
- **URL JDBC:** `jdbc:sqlserver://localhost:1433;databaseName=FashionStoreDB;encrypt=true;trustServerCertificate=true`
- **Username:** `sa` | **Password:** `123`
- **Script DDL & Seed:** `database/schema_and_seed.sql`

### 2.2. Tài khoản người dùng mẫu:
| Vai trò | Email đăng nhập | Mật khẩu | Chức năng |
| :--- | :--- | :--- | :--- |
| **Quản Trị Viên (Admin)** | `admin@fashion.vn` | `123456` | Dashboard, Duyệt đơn, In phiếu A6, Sửa tồn kho |
| **Khách Hàng (Customer)** | `khachhang@fashion.vn` | `123456` | Mua hàng, Giỏ hàng, Đánh giá sản phẩm |
| **Khách Hàng 2** | `maitran@gmail.com` | `123456` | Mua sắm, Viết review kèm chiều cao/cân nặng |

*(Hệ thống tích hợp `DataInitializer` tự động kiểm tra và mã hóa BCrypt an toàn khi server khởi động).*

---

## 3. CÁC TÍNH NĂNG ĐẶC SẮC

### 3.1. Tính năng mua sắm thông minh (Phase 6):
1. **Trợ lý Gợi ý Size Thông Minh (Smart Size Predictor):**
   - Modal thanh trượt: Chiều cao (145cm - 190cm), Cân nặng (40kg - 95kg), Sở thích (Mặc ôm Body / Mặc vừa vặn / Mặc rộng Oversize).
   - Thuật toán nhân trắc học tính toán và gợi ý size với độ vừa vặn ~95%, nút "Chọn ngay Size này" tự động active đúng biến thể.
2. **Thanh Tiến Trình Freeship Động (Free Shipping Progress Bar):**
   - Mốc miễn phí vận chuyển `500.000 VNĐ`.
   - Đổi màu thời gian thực: màu cam khi chưa đủ 500k kèm số tiền thiếu; chuyển xanh lá 100% khi đủ 500k (tiết kiệm 30.000đ ship).
3. **Xem Nhanh Sản Phẩm (Quick View Modal) & Sản Phẩm Đã Xem Gần Đây:**
   - Nút "Xem nhanh" trên từng ProductCard tại Trang chủ và Trang danh mục.
   - Tự động lưu 8 sản phẩm đã xem vào `localStorage` và hiển thị ở cuối Trang chi tiết.

### 3.2. Tính năng vận hành & In ấn chuyên nghiệp (Phase 7):
1. **Timeline Theo Dõi Đơn Hàng 5 Bước (Visual Order Stepper):**
   - Thanh tiến trình trực quan: `PENDING` -> `CONFIRMED` -> `PACKING` -> `SHIPPING` -> `COMPLETED` (Cảnh báo đỏ nếu `CANCELLED`).
2. **In Phiếu Giao Hàng Chuẩn TMĐT (Thermal Shipping Label A6):**
   - Định dạng in nhiệt khổ A6, ẩn menu Admin khi bấm in, mã vạch giả lập, thông tin gửi/nhận, danh sách sản phẩm (kèm Size, Màu), khung tiền thu hộ COD hoặc "ĐÃ THANH TOÁN - THU HỘ: 0 VNĐ" nếu thanh toán qua VietQR.
3. **Cảnh Báo Sắp Hết Hàng (Low-Stock Alert) & Biểu Đồ Doanh Thu:**
   - API `GET /api/admin/dashboard/low-stock` cảnh báo các biến thể có tồn kho `<= 5`.
   - Sửa nhanh số lượng tồn kho trực tiếp ngay tại bảng cảnh báo.

### 3.3. Hệ Sinh Thái Reviews, Flash Sale & Trợ Lý AI (Phase 8 & Phase 9):
1. **Đánh Giá Thực Tế Kèm Vóc Dáng (Fit Reviews):**
   - API `GET /api/products/{id}/reviews` và `POST /api/products/{id}/reviews`.
   - Hiển thị tỷ lệ cảm nhận phom dáng (Chật / Vừa vặn / Rộng), bộ lọc đánh giá theo số sao và tag thông số: `"Khách cao 175cm - 65kg | Size L | Vừa vặn chuẩn form"`.
2. **Khung Giờ Flash Sale Đếm Ngược Thời Gian Thực:**
   - Section "⚡ FLASH SALE GIỜ VÀNG" với đồng hồ đếm ngược trực tiếp (`Giờ : Phút : Giây` nhảy mỗi giây).
   - Nhãn `% Giảm giá`, giá gốc gạch ngang và thanh tiến trình đỏ rực *"🔥 Đang bán chạy - Sắp cháy hàng"*.
3. **Trợ Lý Ảo Chatbot AI Thông Minh (`/api/chat-assistant`):**
   - Floating Chat Widget nổi cố định góc dưới bên phải màn hình.
   - Tư vấn size chuẩn phom người Việt khi khách nhập chiều cao/cân nặng (VD: *"1m75 65kg"* -> Gợi ý Size L và hiển thị 3 mẫu trang phục sẵn hàng trong kho).
   - Tra cứu tình trạng vận chuyển tức thì khi nhập mã đơn hàng (VD: *"DH..."*).
   - Liệt kê và copy 1-click mã giảm giá/freeship hôm nay.
   - Gợi ý phối đồ theo ngân sách (VD: *"đồ dưới 400k"*).
   - Mini Product Card trực tiếp trong khung chat kèm nút "Xem ngay".

---

## 4. HƯỚNG DẪN KHỞI CHẠY 1-CLICK

### Cách 1: Khởi chạy tự động bằng Script Batch
1. Chạy file `start_all.bat` ở thư mục gốc:
   - Script tự động kiểm tra SQL Server và chạy `schema_and_seed.sql` nếu chưa khởi tạo DB.
   - Tự động mở cửa sổ Backend Spring Boot port `8080`.
   - Tự động mở cửa sổ Frontend Vite port `5173`.
   - Tự động mở trình duyệt truy cập `http://localhost:5173`.
2. Khi muốn dừng hệ thống, chỉ cần nhấp đúp file `stop_all.bat` để đóng port 8080 và 5173.

### Cách 2: Khởi chạy thủ công qua Terminal
- **Bước 1 (Database):** Mở SSMS hoặc chạy lệnh:
  ```powershell
  sqlcmd -S localhost -U sa -P 123 -i database/schema_and_seed.sql
  ```
- **Bước 2 (Backend):**
  ```powershell
  cd backend-java
  mvn spring-boot:run
  ```
  *(Server lắng nghe tại `http://localhost:8080`, Swagger UI tại `http://localhost:8080/swagger-ui.html`)*
- **Bước 3 (Frontend):**
  ```powershell
  cd frontend-vue
  npm run dev
  ```
  *(Giao diện khách hàng tại `http://localhost:5173`)*
