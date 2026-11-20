# Hướng Dẫn Đóng Gói & Triển Khai FashionStore Bằng Docker

Hệ thống gồm 3 container chạy trên cùng mạng nội bộ `fashionstore-net`:
- **`mssql`**: Microsoft SQL Server 2022 (lưu dữ liệu bền vững qua volume `mssql_data`).
- **`backend`**: Spring Boot 3 + Java 21 (lưu ảnh upload qua volume `uploads_data`).
- **`frontend`**: Vue 3 build tĩnh phục vụ bởi Nginx, kiêm reverse proxy `/api/` và `/uploads/` sang `backend:8080`.

> Lưu ý: Luồng chạy phát triển local bằng `start_all.bat` vẫn hoạt động độc lập, không bị ảnh hưởng.

---

## 1. Chạy thử bằng Docker trên máy Local

### Bước 1: Tạo file `.env` từ mẫu
```bash
cp .env.example .env
```
Mở file `.env` và kiểm tra/chỉnh sửa các giá trị:
- `SA_PASSWORD`: Mật khẩu SQL Server (phải mạnh: ≥ 8 ký tự gồm chữ hoa, chữ thường, số và ký tự đặc biệt).
- `JWT_SECRET`: Chuỗi bí mật ≥ 64 ký tự.
- `GEMINI_API_KEY`: Điền API key từ Google AI Studio nếu muốn bật trợ lý AI Gemini (để trống vẫn chạy rule-based bình thường).
- `FRONTEND_PORT`: Mặc định `8088` (tránh đụng cổng 80 trên máy cá nhân; đổi thành `80` khi đưa lên VPS).

### Bước 2: Build và khởi chạy toàn bộ hệ thống
```bash
docker compose up -d --build
```
Kiểm tra trạng thái các container:
```bash
docker compose ps
```

---

## 2. Nạp dữ liệu khởi tạo (`schema_and_seed.sql`) lần đầu vào SQL Server

Thư mục `./database` đã được mount sẵn vào `/docker-sql` bên trong container `fashionstore-mssql`. Chạy 2 lệnh sau để tạo database `FashionStoreDB`, nạp dữ liệu mẫu và ảnh thời trang thật:

```bash
# 1. Tạo bảng và nạp dữ liệu mẫu ban đầu
docker exec -it fashionstore-mssql /opt/mssql-tools18/bin/sqlcmd \
  -S localhost -U sa -P "FashionStore@2026!" -C \
  -i /docker-sql/schema_and_seed.sql

# 2. Cập nhật ảnh thời trang Unsplash cho 26 sản phẩm mẫu
docker exec -it fashionstore-mssql /opt/mssql-tools18/bin/sqlcmd \
  -S localhost -U sa -P "FashionStore@2026!" -C \
  -i /docker-sql/update_real_fashion_images.sql
```
*(Thay `FashionStore@2026!` bằng đúng `SA_PASSWORD` trong file `.env` của bạn).*

Sau khi nạp SQL xong, khởi động lại container `backend` để `DataInitializer` tự kiểm tra bảng `ProductImages` và tài khoản mẫu:
```bash
docker compose restart backend
```
Truy cập ứng dụng tại: **http://localhost:8088** (hoặc cổng `FRONTEND_PORT` bạn đã cấu hình).

---

## 3. Lưu ý khi triển khai lên VPS Linux (Ubuntu / Debian)

1. **Cấu hình `.env` cho Production:**
   - Đổi `SA_PASSWORD` và `JWT_SECRET` sang chuỗi mạnh, ngẫu nhiên.
   - Đặt `FRONTEND_PORT=80` (hoặc đặt sau Nginx/Caddy host nếu dùng SSL).
   - Cập nhật `CORS_ALLOWED_ORIGINS=https://yourdomain.com,https://www.yourdomain.com`.
2. **Mở cổng Firewall (UFW):**
   ```bash
   sudo ufw allow 22/tcp
   sudo ufw allow 80/tcp
   sudo ufw allow 443/tcp
   sudo ufw enable
   ```
   > **Khuyến nghị bảo mật:** Trên VPS Production, nên xóa hoặc comment mục `ports: - "14333:1433"` của service `mssql` trong `docker-compose.yml` để không mở cổng SQL Server ra Internet công cộng.
3. **Gắn tên miền & HTTPS (SSL miễn phí):**
   - Trỏ bản ghi DNS `A` của tên miền về IP công khai của VPS.
   - Dùng **Caddy** hoặc **Nginx + Certbot (Let's Encrypt)** ở máy host để cấp chứng chỉ HTTPS tự động và proxy về `http://127.0.0.1:8088` (hoặc dùng Cloudflare Proxy SSL).
4. **Sao lưu dữ liệu định kỳ:**
   - Dữ liệu DB nằm trong Docker volume `mssql_data` và ảnh sản phẩm nằm trong volume `uploads_data`. Không dùng lệnh `docker compose down -v` trên Production vì cờ `-v` sẽ xóa sạch các volume này.
