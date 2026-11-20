-- ============================================================================
-- DỰ ÁN: E-COMMERCE FASHION STORE (HỆ THỐNG THỜI TRANG TRỰC TUYẾN)
-- TẬP TIN: database/schema_and_seed.sql
-- MỤC TIÊU: Khởi tạo CSDL FashionStoreDB, 8 bảng dữ liệu quan hệ và nạp dữ liệu mẫu
-- TIÊU CHUẨN: Microsoft SQL Server T-SQL, NVARCHAR cho mọi trường tiếng Việt
-- ============================================================================

-- 1. TẠO CƠ SỞ DỮ LIỆU NẾU CHƯA TỒN TẠI
IF NOT EXISTS (SELECT name FROM sys.databases WHERE name = N'FashionStoreDB')
BEGIN
    CREATE DATABASE FashionStoreDB COLLATE Vietnamese_CI_AS;
    PRINT N'>>> Đã tạo mới cơ sở dữ liệu FashionStoreDB thành công.';
END
ELSE
BEGIN
    PRINT N'>>> Cơ sở dữ liệu FashionStoreDB đã tồn tại.';
END
GO

USE FashionStoreDB;
GO

-- 2. DỌN DẸP BẢNG CŨ (NẾU CÓ) THEO THỨ TỰ RÀNG BUỘC KHÓA NGOẠI
IF OBJECT_ID('dbo.Reviews', 'U') IS NOT NULL DROP TABLE dbo.Reviews;
IF OBJECT_ID('dbo.OrderItems', 'U') IS NOT NULL DROP TABLE dbo.OrderItems;
IF OBJECT_ID('dbo.Orders', 'U') IS NOT NULL DROP TABLE dbo.Orders;
IF OBJECT_ID('dbo.Coupons', 'U') IS NOT NULL DROP TABLE dbo.Coupons;
IF OBJECT_ID('dbo.ProductVariants', 'U') IS NOT NULL DROP TABLE dbo.ProductVariants;
IF OBJECT_ID('dbo.Products', 'U') IS NOT NULL DROP TABLE dbo.Products;
IF OBJECT_ID('dbo.Categories', 'U') IS NOT NULL DROP TABLE dbo.Categories;
IF OBJECT_ID('dbo.Users', 'U') IS NOT NULL DROP TABLE dbo.Users;
GO

-- 3. TẠO CÁC BẢNG HỆ THỐNG
-- Bảng 1: Categories (Danh mục sản phẩm)
CREATE TABLE dbo.Categories (
    Id BIGINT IDENTITY(1,1) PRIMARY KEY,
    Name NVARCHAR(100) NOT NULL,
    Slug VARCHAR(150) NOT NULL UNIQUE,
    Description NVARCHAR(500) NULL
);
GO

-- Bảng 2: Products (Sản phẩm)
CREATE TABLE dbo.Products (
    Id BIGINT IDENTITY(1,1) PRIMARY KEY,
    CategoryId BIGINT NOT NULL,
    Name NVARCHAR(255) NOT NULL,
    Slug VARCHAR(255) NOT NULL UNIQUE,
    Description NVARCHAR(MAX) NULL,
    BasePrice DECIMAL(18, 2) NOT NULL,
    SalePrice DECIMAL(18, 2) NULL,
    IsFlashSale BIT NOT NULL DEFAULT 0,
    FlashSaleEndTime DATETIME2 NULL,
    ThumbnailUrl NVARCHAR(500) NULL,
    CreatedAt DATETIME2 NOT NULL DEFAULT GETDATE(),
    CONSTRAINT FK_Products_Categories FOREIGN KEY (CategoryId) REFERENCES dbo.Categories(Id)
);
GO

-- Bảng 3: ProductVariants (Biến thể kích cỡ, màu sắc, tồn kho)
CREATE TABLE dbo.ProductVariants (
    Id BIGINT IDENTITY(1,1) PRIMARY KEY,
    ProductId BIGINT NOT NULL,
    Size NVARCHAR(20) NOT NULL,           -- S, M, L, XL, XXL
    ColorName NVARCHAR(50) NOT NULL,      -- Đen, Trắng, Xanh Navy, Be, Xám...
    ColorHex VARCHAR(20) NOT NULL,        -- #000000, #FFFFFF, #1B2A4A...
    Price DECIMAL(18, 2) NOT NULL,
    StockQuantity INT NOT NULL DEFAULT 0,
    Sku VARCHAR(50) NOT NULL UNIQUE,
    CONSTRAINT FK_ProductVariants_Products FOREIGN KEY (ProductId) REFERENCES dbo.Products(Id) ON DELETE CASCADE
);
GO

-- Bảng 4: Users (Người dùng & Khách hàng)
CREATE TABLE dbo.Users (
    Id BIGINT IDENTITY(1,1) PRIMARY KEY,
    FullName NVARCHAR(150) NOT NULL,
    Email VARCHAR(150) NOT NULL UNIQUE,
    PasswordHash VARCHAR(255) NOT NULL,
    Phone VARCHAR(20) NULL,
    Address NVARCHAR(255) NULL,
    Role VARCHAR(20) NOT NULL DEFAULT 'CUSTOMER', -- ADMIN, CUSTOMER
    CreatedAt DATETIME2 NOT NULL DEFAULT GETDATE()
);
GO

-- Bảng 5: Coupons (Mã giảm giá & Khuyến mãi)
CREATE TABLE dbo.Coupons (
    Id BIGINT IDENTITY(1,1) PRIMARY KEY,
    Code VARCHAR(50) NOT NULL UNIQUE,
    DiscountPercent INT NOT NULL CHECK (DiscountPercent BETWEEN 1 AND 100),
    MaxDiscountAmount DECIMAL(18, 2) NOT NULL,
    MinOrderValue DECIMAL(18, 2) NOT NULL,
    ExpiryDate DATETIME2 NOT NULL,
    UsageLimit INT NOT NULL,
    UsedCount INT NOT NULL DEFAULT 0,
    IsActive BIT NOT NULL DEFAULT 1
);
GO

-- Bảng 6: Orders (Đơn đặt hàng)
CREATE TABLE dbo.Orders (
    Id BIGINT IDENTITY(1,1) PRIMARY KEY,
    OrderCode VARCHAR(50) NOT NULL UNIQUE,
    UserId BIGINT NULL,
    CustomerName NVARCHAR(150) NOT NULL,
    CustomerPhone VARCHAR(20) NOT NULL,
    ShippingAddress NVARCHAR(300) NOT NULL,
    Note NVARCHAR(500) NULL,
    SubTotal DECIMAL(18, 2) NOT NULL,
    ShippingFee DECIMAL(18, 2) NOT NULL DEFAULT 0,
    DiscountAmount DECIMAL(18, 2) NOT NULL DEFAULT 0,
    TotalAmount DECIMAL(18, 2) NOT NULL,
    CouponCode VARCHAR(50) NULL,
    PaymentMethod VARCHAR(20) NOT NULL,           -- COD, VIETQR
    PaymentStatus VARCHAR(20) NOT NULL DEFAULT 'UNPAID',  -- UNPAID, PAID
    OrderStatus VARCHAR(30) NOT NULL DEFAULT 'PENDING',  -- PENDING, CONFIRMED, PACKING, SHIPPING, COMPLETED, CANCELLED
    CreatedAt DATETIME2 NOT NULL DEFAULT GETDATE(),
    CONSTRAINT FK_Orders_Users FOREIGN KEY (UserId) REFERENCES dbo.Users(Id)
);
GO

-- Bảng 7: OrderItems (Chi tiết sản phẩm trong đơn)
CREATE TABLE dbo.OrderItems (
    Id BIGINT IDENTITY(1,1) PRIMARY KEY,
    OrderId BIGINT NOT NULL,
    ProductVariantId BIGINT NOT NULL,
    ProductName NVARCHAR(255) NOT NULL,
    Size NVARCHAR(20) NOT NULL,
    ColorName NVARCHAR(50) NOT NULL,
    Quantity INT NOT NULL CHECK (Quantity > 0),
    UnitPrice DECIMAL(18, 2) NOT NULL,
    CONSTRAINT FK_OrderItems_Orders FOREIGN KEY (OrderId) REFERENCES dbo.Orders(Id) ON DELETE CASCADE,
    CONSTRAINT FK_OrderItems_ProductVariants FOREIGN KEY (ProductVariantId) REFERENCES dbo.ProductVariants(Id)
);
GO

-- Bảng 8: Reviews (Đánh giá & Phản hồi độ vừa vặn)
CREATE TABLE dbo.Reviews (
    Id BIGINT IDENTITY(1,1) PRIMARY KEY,
    ProductId BIGINT NOT NULL,
    UserId BIGINT NULL,
    CustomerName NVARCHAR(150) NOT NULL,
    Rating INT NOT NULL CHECK (Rating BETWEEN 1 AND 5),
    Comment NVARCHAR(1000) NOT NULL,
    CustomerHeightCm INT NULL,
    CustomerWeightKg INT NULL,
    PurchasedSize NVARCHAR(20) NULL,
    PurchasedColor NVARCHAR(50) NULL,
    FitFeedback VARCHAR(20) NULL, -- CHAT, VUA_VAN, RONG
    CreatedAt DATETIME2 NOT NULL DEFAULT GETDATE(),
    CONSTRAINT FK_Reviews_Products FOREIGN KEY (ProductId) REFERENCES dbo.Products(Id) ON DELETE CASCADE,
    CONSTRAINT FK_Reviews_Users FOREIGN KEY (UserId) REFERENCES dbo.Users(Id)
);
GO

-- 4. TẠO INDEX TỐI ƯU HÓA HIỆU NĂNG TÌM KIẾM
CREATE INDEX IX_Products_CategoryId ON dbo.Products(CategoryId);
CREATE INDEX IX_Products_IsFlashSale ON dbo.Products(IsFlashSale);
CREATE INDEX IX_ProductVariants_ProductId ON dbo.ProductVariants(ProductId);
CREATE INDEX IX_Orders_OrderCode ON dbo.Orders(OrderCode);
CREATE INDEX IX_Orders_UserId ON dbo.Orders(UserId);
CREATE INDEX IX_Reviews_ProductId ON dbo.Reviews(ProductId);
GO

-- ============================================================================
-- 5. NẠP DỮ LIỆU MẪU (SEED DATA)
-- ============================================================================

-- 5.1 NẠP USERS (Mật khẩu '123456' băm BCrypt chuẩn)
-- Hash BCrypt mẫu: $2a$10$w09ZkC276jV27bO8bUe/6uUeQvI6vW5Zl2F4bO5m7n9Q2w3e4r5t6
INSERT INTO dbo.Users (FullName, Email, PasswordHash, Phone, Address, Role, CreatedAt)
VALUES 
(N'Quản Trị Viên', 'admin@fashion.vn', '$2a$10$w09ZkC276jV27bO8bUe/6uUeQvI6vW5Zl2F4bO5m7n9Q2w3e4r5t6', '0901234567', N'Số 88 Phố Huế, Quận Hai Bà Trưng, Hà Nội', 'ADMIN', GETDATE()),
(N'Nguyễn Văn Khách', 'khachhang@fashion.vn', '$2a$10$w09ZkC276jV27bO8bUe/6uUeQvI6vW5Zl2F4bO5m7n9Q2w3e4r5t6', '0912345678', N'Số 123 Lê Lợi, Phường Bến Nghé, Quận 1, TP. Hồ Chí Minh', 'CUSTOMER', GETDATE()),
(N'Trần Thị Mai', 'maitran@gmail.com', '$2a$10$w09ZkC276jV27bO8bUe/6uUeQvI6vW5Zl2F4bO5m7n9Q2w3e4r5t6', '0987654321', N'Số 45 Cầu Giấy, Quận Cầu Giấy, Hà Nội', 'CUSTOMER', GETDATE());
GO

-- 5.2 NẠP CATEGORIES (6 Danh mục chính)
INSERT INTO dbo.Categories (Name, Slug, Description)
VALUES
(N'Áo Thun Nam Nữ', 'ao-thun', N'Bộ sưu tập áo thun cotton compact, oversize streetwear, polo dệt bo thoáng mát'),
(N'Áo Sơ Mi Thời Trang', 'ao-so-mi', N'Áo sơ mi nam nữ công sở, vải sợi tre chống nhăn và sơ mi dạo phố phong cách Hàn Quốc'),
(N'Quần Jean & Kaki', 'quan-jean-kaki', N'Quần jean slimfit co giãn, quần ống rộng tôn dáng và quần kaki smart-casual'),
(N'Áo Khoác & Hoodie', 'ao-khoac-hoodie', N'Áo khoác gió 2 lớp chống nước, áo bomber thể thao, áo dạ dài và hoodie nỉ ấm áp'),
(N'Váy & Đầm Nữ', 'vay-dam-nu', N'Đầm xòe hoa nhí thanh lịch, đầm dự tiệc quyến rũ và chân váy xếp ly năng động'),
(N'Đồ Thể Thao & Mặc Nhà', 'do-the-thao-mac-nha', N'Bộ thể thao thun lạnh co giãn và pijama lụa satin cao cấp');
GO

-- 5.3 NẠP PRODUCTS (26 Sản phẩm phong phú)
-- Danh mục 1: Áo thun (1 - 5)
INSERT INTO dbo.Products (CategoryId, Name, Slug, Description, BasePrice, SalePrice, IsFlashSale, FlashSaleEndTime, ThumbnailUrl, CreatedAt)
VALUES
(1, N'Áo Thun Cotton Compact 100% Phom Regular', 'ao-thun-cotton-compact-regular', 
 N'Chất liệu 100% Cotton Compact chải kỹ mềm mịn, co giãn 2 chiều, định lượng 230gsm giữ phom cực tốt sau nhiều lần giặt. Cổ áo bo gân dệt nguyên bản không bai dão.',
 199000, 149000, 1, DATEADD(DAY, 7, GETDATE()), 'https://images.unsplash.com/photo-1521572163474-6864f9cf17ab?auto=format&fit=crop&w=800&q=80', GETDATE()),

(1, N'Áo Thun Polo Nam Phối Cổ Dệt Bo Cao Cấp', 'ao-thun-polo-nam-phoi-co-det', 
 N'Áo polo nam chất vải cá sấu mè cải tiến siêu nhẹ và thấm hút mồ hôi. Thiết kế phối viền cổ và tay áo tinh tế, phù hợp cả đi làm lẫn dạo phố.',
 289000, 229000, 1, DATEADD(DAY, 5, GETDATE()), 'https://images.unsplash.com/photo-1583743814966-8936f5b7be1a?auto=format&fit=crop&w=800&q=80', GETDATE()),

(1, N'Áo Thun Oversize Unisex Họa Tiết Streetwear', 'ao-thun-oversize-unisex-streetwear', 
 N'Phong cách đường phố hiện đại với phom áo oversize rộng rãi, tay lỡ cá tính. Hình in lụa cao cấp sắc nét, không bong tróc khi giặt máy.',
 249000, 189000, 0, NULL, 'https://images.unsplash.com/photo-1576566588028-4147f3842f27?auto=format&fit=crop&w=800&q=80', GETDATE()),

(1, N'Áo Thun Cổ Tim Nữ Modal Mềm Mát Tôn Dáng', 'ao-thun-co-tim-nu-modal', 
 N'Sợi vải sinh học Modal từ gỗ sồi tự nhiên đem lại cảm giác mát lạnh tức thì, phom áo ôm nhẹ tôn vòng eo và đường cong nữ tính.',
 179000, 139000, 0, NULL, 'https://images.unsplash.com/photo-1562157873-818bc0726f68?auto=format&fit=crop&w=800&q=80', GETDATE()),

(1, N'Áo Thun Tanktop Thể Thao Co Giãn 4 Chiều', 'ao-thun-tanktop-the-thao-co-gian', 
 N'Chất vải thun lạnh chuyên dụng cho tập gym, chạy bộ với công nghệ QuickDry khô nhanh, thoáng khí tối đa suốt ngày dài.',
 159000, 119000, 0, NULL, 'https://images.unsplash.com/photo-1503342217505-b0a15ec3261c?auto=format&fit=crop&w=800&q=80', GETDATE()),

-- Danh mục 2: Áo sơ mi (6 - 10)
(2, N'Áo Sơ Mi Nam Tay Dài Chống Nhăn Bamboo', 'ao-so-mi-nam-tay-dai-chong-nhan-bamboo', 
 N'Công nghệ dệt từ sợi tre Bamboo tự nhiên kháng khuẩn, chống nhăn hiệu quả sau khi giặt. Form dáng slim fit chuẩn quý ông công sở.',
 389000, 299000, 1, DATEADD(DAY, 10, GETDATE()), 'https://images.unsplash.com/photo-1596755094514-f87e34085b2c?auto=format&fit=crop&w=800&q=80', GETDATE()),

(2, N'Áo Sơ Mi Oxford Cổ Button Down Cổ Điển', 'ao-so-mi-oxford-co-button-down', 
 N'Chất vải Oxford dệt vân caro hạt gạo đặc trưng, độ dày dặn vừa phải, thiết kế nút gài cổ ve áo đậm chất Ivy League kinh điển.',
 359000, 279000, 0, NULL, 'https://images.unsplash.com/photo-1602810318383-e386cc2a3ccf?auto=format&fit=crop&w=800&q=80', GETDATE()),

(2, N'Áo Sơ Mi Lụa Tay Phồng Nữ Công Sở Thanh Lịch', 'ao-so-mi-lua-tay-phong-nu-cong-so', 
 N'Vải lụa tơ nhân tạo mềm rủ nhẹ nhàng, cổ áo chữ V phối nơ cách điệu kết hợp tay bồng duyên dáng, thích hợp môi trường công sở sang trọng.',
 329000, 259000, 0, NULL, 'https://images.unsplash.com/photo-1598033129183-c4f50c736f10?auto=format&fit=crop&w=800&q=80', GETDATE()),

(2, N'Áo Sơ Mi Họa Tiết Cuban Retro Đi Biển', 'ao-so-mi-hoa-tiet-cuban-retro', 
 N'Cổ áo Cuban phóng khoáng kết hợp họa tiết nhiệt đới rực rỡ, chất vải đũi xước nhẹ nhàng thoáng mát lý tưởng cho các kỳ nghỉ biển.',
 299000, 229000, 0, NULL, 'https://images.unsplash.com/photo-1620012253295-c15cc3e65df4?auto=format&fit=crop&w=800&q=80', GETDATE()),

(2, N'Áo Sơ Mi Nữ Kẻ Sọc Dáng Rộng Phong Cách Hàn Quốc', 'ao-so-mi-nu-ke-soc-han-quoc', 
 N'Thiết kế kẻ sọc dọc thanh mảnh tạo hiệu ứng vóc dáng thon gọn. Phom rộng giấu quần trẻ trung năng động được giới trẻ yêu thích.',
 279000, 219000, 0, NULL, 'https://images.unsplash.com/photo-1589310243389-96a5483213a8?auto=format&fit=crop&w=800&q=80', GETDATE()),

-- Danh mục 3: Quần Jean & Kaki (11 - 15)
(3, N'Quần Jean Nam Slimfit Co Giãn Rách Gối Cá Tính', 'quan-jean-nam-slimfit-co-gian', 
 N'Chất denim dày 12oz wash màu vintage ấn tượng, pha 2% Spandex co giãn nhẹ thoải mái vận động. Đường mài rách gối cá tính.',
 450000, 360000, 1, DATEADD(DAY, 6, GETDATE()), 'https://images.unsplash.com/photo-1542272604-787c3835535d?auto=format&fit=crop&w=800&q=80', GETDATE()),

(3, N'Quần Jean Ống Rộng Nữ Cạp Cao Retro', 'quan-jean-ong-rong-nu-cap-cao-retro', 
 N'Cạp cao ôm sát vòng eo, ống suông rộng rủ chuẩn style thập niên 90 hack chiều dài đôi chân ngoạn mục cho phái đẹp.',
 390000, 310000, 0, NULL, 'https://images.unsplash.com/photo-1624378439575-d8705ad7ae80?auto=format&fit=crop&w=800&q=80', GETDATE()),

(3, N'Quần Kaki Nam Dáng Suông Công Sở Smart Casual', 'quan-kaki-nam-dang-suong-cong-so', 
 N'Vải Kaki Cotton chéo dày dặn đã qua xử lý wash mềm chống co rút, không bám bụi. Phom quần đứng đắn cho quý ông hiện đại.',
 380000, 299000, 0, NULL, 'https://images.unsplash.com/photo-1473966968600-fa801b869a1a?auto=format&fit=crop&w=800&q=80', GETDATE()),

(3, N'Quần Tây Nữ Dáng Baggy Lưng Cao Tôn Dáng', 'quan-tay-nu-dang-baggy-lung-cao', 
 N'Chất vải tuyết mưa cao cấp dày mịn, lưng thun co giãn phía sau vừa vặn tuyệt đối, đường ly xếp phía trước tạo nét sang trọng.',
 350000, 280000, 0, NULL, 'https://images.unsplash.com/photo-1541099649105-f69ad21f3246?auto=format&fit=crop&w=800&q=80', GETDATE()),

(3, N'Quần Short Jean Nữ Viền Rách Năng Động', 'quan-short-jean-nu-vien-rach', 
 N'Quần sooc jean nữ phong cách hè rực rỡ, đường cắt lai gấu tưa chỉ phóng khoáng, dễ phối đồ cùng croptop hoặc áo thun.',
 250000, 199000, 0, NULL, 'https://images.unsplash.com/photo-1591195853828-11db59a44f6b?auto=format&fit=crop&w=800&q=80', GETDATE()),

-- Danh mục 4: Áo Khoác & Hoodie (16 - 20)
(4, N'Áo Khoác Bomber Nam Phối Túi Khóa Zip Mạnh Mẽ', 'ao-khoac-bomber-nam-phoi-tui-zip', 
 N'Lớp ngoài vải dù Poly chống gió trượt nước, lót trong trần bông quả trám giữ ấm vượt trội. Bo dệt thun cổ tay và gấu áo bền bỉ.',
 520000, 399000, 1, DATEADD(DAY, 4, GETDATE()), 'https://images.unsplash.com/photo-1551028719-00167b16eac5?auto=format&fit=crop&w=800&q=80', GETDATE()),

(4, N'Áo Khoác Gió Nam Nữ 2 Lớp Chống Thấm Nước', 'ao-khoac-gio-2-lop-chong-nuoc', 
 N'Công nghệ tráng màng PU kháng nước chuẩn đi mưa nhỏ, tích hợp mũ trùm đầu giấu gáy tiện lợi và túi khóa kéo an toàn.',
 350000, 280000, 0, NULL, 'https://images.unsplash.com/photo-1556821840-3a63f95609a7?auto=format&fit=crop&w=800&q=80', GETDATE()),

(4, N'Áo Hoodie Nỉ Bông Unisex Túi Trước Họa Tiết', 'ao-hoodie-ni-bong-unisex-tui-truoc', 
 N'Vải nỉ bông chân cua dày dặn 350gsm siêu ấm áp mùa đông. Mũ 2 lớp dày dặn kèm dây rút bản to và túi kangaroo lớn phía trước.',
 420000, 340000, 0, NULL, 'https://images.unsplash.com/photo-1591047139829-d91aecb6caea?auto=format&fit=crop&w=800&q=80', GETDATE()),

(4, N'Áo Khoác Dạ Nữ Dáng Dài Cổ Vest Sang Trọng', 'ao-khoac-da-nu-dang-dai-co-vest', 
 N'Dạ len ép cao cấp dầy dặn đứng phom chuẩn Hàn Quốc, đai thắt eo tạo điểm nhấn quý phái cho các buổi tiệc và sự kiện đông.',
 790000, 620000, 0, NULL, 'https://images.unsplash.com/photo-1544022613-e87ca75a784a?auto=format&fit=crop&w=800&q=80', GETDATE()),

(4, N'Áo Cardigan Len Dệt Kim Mềm Nữ Thu Đông', 'ao-cardigan-len-det-kim-mem-nu', 
 N'Sợi len dệt kim mềm mịn không gây ngứa dặm trên da, cúc áo ngọc trai sang trọng, màu sắc pastel thanh thoát dịu dàng.',
 340000, 270000, 0, NULL, 'https://images.unsplash.com/photo-1620799140408-edc6dcb6d633?auto=format&fit=crop&w=800&q=80', GETDATE()),

-- Danh mục 5: Váy & Đầm Nữ (21 - 24)
(5, N'Đầm Nữ Chữ A Dáng Xòe Họa Tiết Hoa Nhí Vintage', 'dam-nu-chu-a-dang-xoe-hoa-nhi', 
 N'Thiết kế cổ vuông khoe xương quai xanh quyến rũ, chất vải voan lụa 2 lớp thướt tha, họa tiết hoa nhí đồng nội lãng mạn.',
 420000, 320000, 1, DATEADD(DAY, 8, GETDATE()), 'https://images.unsplash.com/photo-1595777457583-95e059d581b8?auto=format&fit=crop&w=800&q=80', GETDATE()),

(5, N'Đầm Bodycon Dự Tiệc Cổ Vuông Quyến Rũ', 'dam-bodycon-du-tiec-co-vuong', 
 N'Chất thun gân tăm co giãn cao ôm sát trọn vẹn từng đường cong gợi cảm. Đầm xẻ tà bên hông vừa tôn dáng vừa dễ di chuyển.',
 490000, 390000, 0, NULL, 'https://images.unsplash.com/photo-1572804013309-59a88b7e92f1?auto=format&fit=crop&w=800&q=80', GETDATE()),

(5, N'Chân Váy Xếp Ly Tennis Nữ Trẻ Trung', 'chan-vay-xep-ly-tennis-nu', 
 N'Chân váy dáng chữ A xếp ly sắc sảo không lo mất nếp sau khi giặt, có quần bảo hộ may liền bên trong giúp nàng tự tin vận động.',
 220000, 175000, 0, NULL, 'https://images.unsplash.com/photo-1583496661160-fb5886a0aaaa?auto=format&fit=crop&w=800&q=80', GETDATE()),

(5, N'Đầm Maxi Đi Biển Voan Tơ Cúp Ngực Bồng Bềnh', 'dam-maxi-di-bien-voan-to', 
 N'Chiều dài chạm mắt cá chân thướt tha, cúp ngực có đệm mút cao cấp, dây váy buộc vai tinh tế cho những bức ảnh sống ảo tuyệt đẹp.',
 480000, 380000, 0, NULL, 'https://images.unsplash.com/photo-1515372039744-b8f02a3ae446?auto=format&fit=crop&w=800&q=80', GETDATE()),

-- Danh mục 6: Đồ Thể Thao & Mặc Nhà (25 - 26)
(6, N'Set Đồ Thể Thao Nam Thun Lạnh Thoáng Khí', 'set-do-the-thao-nam-thun-lanh', 
 N'Bao gồm 1 áo thun cộc tay và 1 quần short thể thao lưng thun túi khóa zip. Chất thun lạnh kim cương co giãn đàn hồi vượt trội.',
 360000, 279000, 0, NULL, 'https://images.unsplash.com/photo-1515886657613-9f3515b0c78f?auto=format&fit=crop&w=800&q=80', GETDATE()),

(6, N'Set Bộ Pijama Lụa Satin Nữ Dài Tay Cao Cấp', 'set-bo-pijama-lua-satin-nu', 
 N'Vải lụa satin thượng hạng óng ả mềm mượt như mây, viền lé phối màu chỉn chu, mang lại giấc ngủ êm dịu và vẻ đẹp quý phái tại nhà.',
 390000, 310000, 0, NULL, 'https://images.unsplash.com/photo-1594633312681-425c7b97ccd1?auto=format&fit=crop&w=800&q=80', GETDATE());
GO

-- 5.4 NẠP PRODUCT VARIANTS (2 - 3 biến thể cho mỗi sản phẩm)
-- SP 1: Áo Thun Cotton Compact
INSERT INTO dbo.ProductVariants (ProductId, Size, ColorName, ColorHex, Price, StockQuantity, Sku) VALUES
(1, N'M', N'Đen Basic', '#000000', 149000, 50, 'AT-01-M-DEN'),
(1, N'L', N'Đen Basic', '#000000', 149000, 45, 'AT-01-L-DEN'),
(1, N'XL', N'Trắng Tinh Khôi', '#FFFFFF', 149000, 30, 'AT-01-XL-TRANG');

-- SP 2: Áo Thun Polo Nam
INSERT INTO dbo.ProductVariants (ProductId, Size, ColorName, ColorHex, Price, StockQuantity, Sku) VALUES
(2, N'M', N'Xanh Navy', '#1B2A4A', 229000, 40, 'PL-02-M-NAVY'),
(2, N'L', N'Xanh Navy', '#1B2A4A', 229000, 35, 'PL-02-L-NAVY'),
(2, N'L', N'Trắng Cổ Viền', '#F4F4F4', 229000, 25, 'PL-02-L-TRANG');

-- SP 3: Áo Thun Oversize Unisex
INSERT INTO dbo.ProductVariants (ProductId, Size, ColorName, ColorHex, Price, StockQuantity, Sku) VALUES
(3, N'L', N'Xám Khói', '#708090', 189000, 60, 'AT-03-L-XAM'),
(3, N'XL', N'Đen Họa Tiết', '#111111', 189000, 50, 'AT-03-XL-DEN');

-- SP 4: Áo Thun Cổ Tim Nữ
INSERT INTO dbo.ProductVariants (ProductId, Size, ColorName, ColorHex, Price, StockQuantity, Sku) VALUES
(4, N'S', N'Hồng Pastel', '#FFB6C1', 139000, 30, 'AT-04-S-HONG'),
(4, N'M', N'Trắng Ngà', '#FFFDD0', 139000, 40, 'AT-04-M-TRANG');

-- SP 5: Áo Thun Tanktop Thể Thao
INSERT INTO dbo.ProductVariants (ProductId, Size, ColorName, ColorHex, Price, StockQuantity, Sku) VALUES
(5, N'M', N'Đen Thể Thao', '#000000', 119000, 50, 'TT-05-M-DEN'),
(5, N'L', N'Xanh Rêu Đậm', '#2E473B', 119000, 35, 'TT-05-L-REU');

-- SP 6: Áo Sơ Mi Nam Bamboo
INSERT INTO dbo.ProductVariants (ProductId, Size, ColorName, ColorHex, Price, StockQuantity, Sku) VALUES
(6, N'M', N'Trắng Công Sở', '#FFFFFF', 299000, 45, 'SM-06-M-TRANG'),
(6, N'L', N'Trắng Công Sở', '#FFFFFF', 299000, 50, 'SM-06-L-TRANG'),
(6, N'L', N'Xanh Nhạt Pastel', '#B0E0E6', 299000, 30, 'SM-06-L-XANH');

-- SP 7: Áo Sơ Mi Oxford
INSERT INTO dbo.ProductVariants (ProductId, Size, ColorName, ColorHex, Price, StockQuantity, Sku) VALUES
(7, N'M', N'Xanh Dương Oxford', '#4682B4', 279000, 35, 'SM-07-M-XANH'),
(7, N'L', N'Trắng Classic', '#FFFFFF', 279000, 40, 'SM-07-L-TRANG');

-- SP 8: Áo Sơ Mi Lụa Tay Phồng
INSERT INTO dbo.ProductVariants (ProductId, Size, ColorName, ColorHex, Price, StockQuantity, Sku) VALUES
(8, N'S', N'Be Sữa', '#F5F5DC', 259000, 25, 'SM-08-S-BE'),
(8, N'M', N'Be Sữa', '#F5F5DC', 259000, 30, 'SM-08-M-BE'),
(8, N'M', N'Đen Quyến Rũ', '#0A0A0A', 259000, 20, 'SM-08-M-DEN');

-- SP 9: Áo Sơ Mi Họa Tiết Cuban
INSERT INTO dbo.ProductVariants (ProductId, Size, ColorName, ColorHex, Price, StockQuantity, Sku) VALUES
(9, N'L', N'Vàng Họa Tiết', '#E6C229', 229000, 30, 'SM-09-L-VANG'),
(9, N'XL', N'Xanh Lá Tropical', '#2E8B57', 229000, 25, 'SM-09-XL-XANH');

-- SP 10: Áo Sơ Mi Nữ Kẻ Sọc
INSERT INTO dbo.ProductVariants (ProductId, Size, ColorName, ColorHex, Price, StockQuantity, Sku) VALUES
(10, N'S', N'Sọc Xanh Trắng', '#87CEFA', 219000, 40, 'SM-10-S-SOC'),
(10, N'M', N'Sọc Xanh Trắng', '#87CEFA', 219000, 35, 'SM-10-M-SOC');

-- SP 11: Quần Jean Nam Slimfit
INSERT INTO dbo.ProductVariants (ProductId, Size, ColorName, ColorHex, Price, StockQuantity, Sku) VALUES
(11, N'M', N'Xanh Denim Vintage', '#2C3E50', 360000, 30, 'JN-11-M-DENIM'),
(11, N'L', N'Xanh Denim Vintage', '#2C3E50', 360000, 40, 'JN-11-L-DENIM'),
(11, N'XL', N'Đen Wash Khói', '#1C1C1C', 360000, 20, 'JN-11-XL-DEN');

-- SP 12: Quần Jean Ống Rộng Nữ
INSERT INTO dbo.ProductVariants (ProductId, Size, ColorName, ColorHex, Price, StockQuantity, Sku) VALUES
(12, N'S', N'Xanh Retro Nhạt', '#5D8AA8', 310000, 25, 'JN-12-S-XANH'),
(12, N'M', N'Xanh Retro Nhạt', '#5D8AA8', 310000, 30, 'JN-12-M-XANH');

-- SP 13: Quần Kaki Nam
INSERT INTO dbo.ProductVariants (ProductId, Size, ColorName, ColorHex, Price, StockQuantity, Sku) VALUES
(13, N'M', N'Màu Be Vàng', '#C2B280', 299000, 35, 'KK-13-M-BE'),
(13, N'L', N'Màu Be Vàng', '#C2B280', 299000, 40, 'KK-13-L-BE'),
(13, N'L', N'Đen Tuyền', '#000000', 299000, 25, 'KK-13-L-DEN');

-- SP 14: Quần Tây Nữ Dáng Baggy
INSERT INTO dbo.ProductVariants (ProductId, Size, ColorName, ColorHex, Price, StockQuantity, Sku) VALUES
(14, N'S', N'Đen Công Sở', '#000000', 280000, 35, 'QT-14-S-DEN'),
(14, N'M', N'Đen Công Sở', '#000000', 280000, 30, 'QT-14-M-DEN'),
(14, N'M', N'Nâu Tây Sang Trọng', '#8B5A2B', 280000, 20, 'QT-14-M-NAU');

-- SP 15: Quần Short Jean Nữ
INSERT INTO dbo.ProductVariants (ProductId, Size, ColorName, ColorHex, Price, StockQuantity, Sku) VALUES
(15, N'S', N'Xanh Nhạt Rách', '#6CA0DC', 199000, 30, 'QS-15-S-XANH'),
(15, N'M', N'Xanh Nhạt Rách', '#6CA0DC', 199000, 35, 'QS-15-M-XANH');

-- SP 16: Áo Khoác Bomber Nam
INSERT INTO dbo.ProductVariants (ProductId, Size, ColorName, ColorHex, Price, StockQuantity, Sku) VALUES
(16, N'M', N'Đen Cá Tính', '#0B0B0B', 399000, 25, 'BM-16-M-DEN'),
(16, N'L', N'Đen Cá Tính', '#0B0B0B', 399000, 30, 'BM-16-L-DEN'),
(16, N'L', N'Xanh Rêu Quân Đội', '#3B4D3C', 399000, 20, 'BM-16-L-REU');

-- SP 17: Áo Khoác Gió Nam Nữ
INSERT INTO dbo.ProductVariants (ProductId, Size, ColorName, ColorHex, Price, StockQuantity, Sku) VALUES
(17, N'L', N'Xanh Đen Phối Bạc', '#1C2833', 280000, 45, 'KG-17-L-XANH'),
(17, N'XL', N'Đỏ Đô Nổi Bật', '#800020', 280000, 25, 'KG-17-XL-DO');

-- SP 18: Áo Hoodie Nỉ Bông
INSERT INTO dbo.ProductVariants (ProductId, Size, ColorName, ColorHex, Price, StockQuantity, Sku) VALUES
(18, N'L', N'Xám Tiêu Ấm Áp', '#A9A9A9', 340000, 35, 'HD-18-L-XAM'),
(18, N'XL', N'Đen Phối Chữ', '#141414', 340000, 40, 'HD-18-XL-DEN');

-- SP 19: Áo Khoác Dạ Nữ
INSERT INTO dbo.ProductVariants (ProductId, Size, ColorName, ColorHex, Price, StockQuantity, Sku) VALUES
(19, N'S', N'Nâu Camel Quý Tộc', '#C19A6B', 620000, 15, 'KD-19-S-CAMEL'),
(19, N'M', N'Nâu Camel Quý Tộc', '#C19A6B', 620000, 20, 'KD-19-M-CAMEL'),
(19, N'M', N'Đen Quyền Lực', '#0D0D0D', 620000, 10, 'KD-19-M-DEN');

-- SP 20: Áo Cardigan Len Dệt Kim
INSERT INTO dbo.ProductVariants (ProductId, Size, ColorName, ColorHex, Price, StockQuantity, Sku) VALUES
(20, N'S', N'Kem Vani', '#FFF8DC', 270000, 30, 'CD-20-S-KEM'),
(20, N'M', N'Xanh Cốm Dịu Dàng', '#98FB98', 270000, 25, 'CD-20-M-XANH');

-- SP 21: Đầm Nữ Chữ A Hoa Nhí
INSERT INTO dbo.ProductVariants (ProductId, Size, ColorName, ColorHex, Price, StockQuantity, Sku) VALUES
(21, N'S', N'Vàng Hoa Cúc', '#FFDB58', 320000, 25, 'DM-21-S-VANG'),
(21, N'M', N'Vàng Hoa Cúc', '#FFDB58', 320000, 30, 'DM-21-M-VANG'),
(21, N'M', N'Trắng Hoa Nhí', '#FAF0E6', 320000, 20, 'DM-21-M-TRANG');

-- SP 22: Đầm Bodycon Dự Tiệc
INSERT INTO dbo.ProductVariants (ProductId, Size, ColorName, ColorHex, Price, StockQuantity, Sku) VALUES
(22, N'S', N'Đỏ Rượu Vang', '#722F37', 390000, 20, 'BC-22-S-DO'),
(22, N'M', N'Đen Huyền Bí', '#000000', 390000, 25, 'BC-22-M-DEN');

-- SP 23: Chân Váy Xếp Ly Tennis
INSERT INTO dbo.ProductVariants (ProductId, Size, ColorName, ColorHex, Price, StockQuantity, Sku) VALUES
(23, N'S', N'Trắng Tinh', '#FFFFFF', 175000, 40, 'CV-23-S-TRANG'),
(23, N'M', N'Đen Tuyền', '#000000', 175000, 35, 'CV-23-M-DEN');

-- SP 24: Đầm Maxi Đi Biển Voan Tơ
INSERT INTO dbo.ProductVariants (ProductId, Size, ColorName, ColorHex, Price, StockQuantity, Sku) VALUES
(24, N'S', N'Trắng Voan Bay Bổng', '#FFFFFF', 380000, 20, 'MX-24-S-TRANG'),
(24, N'M', N'Vàng Nắng Mùa Hè', '#FFD700', 380000, 25, 'MX-24-M-VANG');

-- SP 25: Set Đồ Thể Thao Nam
INSERT INTO dbo.ProductVariants (ProductId, Size, ColorName, ColorHex, Price, StockQuantity, Sku) VALUES
(25, N'M', N'Xanh Than Mạnh Mẽ', '#191970', 279000, 35, 'ST-25-M-THAN'),
(25, N'L', N'Xanh Than Mạnh Mẽ', '#191970', 279000, 40, 'ST-25-L-THAN'),
(25, N'XL', N'Xám Chuột Thể Thao', '#4A4A4A', 279000, 25, 'ST-25-XL-XAM');

-- SP 26: Set Bộ Pijama Lụa Satin
INSERT INTO dbo.ProductVariants (ProductId, Size, ColorName, ColorHex, Price, StockQuantity, Sku) VALUES
(26, N'S', N'Hồng Đỗ Quyên', '#E0A899', 310000, 25, 'PJ-26-S-HONG'),
(26, N'M', N'Xanh Cẩm Thạch', '#00A877', 310000, 30, 'PJ-26-M-XANH');
GO

-- 5.5 NẠP COUPONS (5 Mã giảm giá hoạt động)
INSERT INTO dbo.Coupons (Code, DiscountPercent, MaxDiscountAmount, MinOrderValue, ExpiryDate, UsageLimit, UsedCount, IsActive)
VALUES
('WELCOME10', 10, 50000, 200000, DATEADD(YEAR, 1, GETDATE()), 1000, 15, 1),
('FREESHIP', 100, 30000, 300000, DATEADD(YEAR, 1, GETDATE()), 500, 42, 1),
('SALE50K', 15, 50000, 350000, DATEADD(YEAR, 1, GETDATE()), 200, 18, 1),
('VIPFASHION', 20, 100000, 600000, DATEADD(YEAR, 1, GETDATE()), 100, 9, 1),
('SUMMER25', 25, 150000, 800000, DATEADD(YEAR, 1, GETDATE()), 150, 4, 1);
GO

-- 5.6 NẠP REVIEWS (15 Đánh giá chân thực kèm số đo & độ vừa vặn)
INSERT INTO dbo.Reviews (ProductId, UserId, CustomerName, Rating, Comment, CustomerHeightCm, CustomerWeightKg, PurchasedSize, PurchasedColor, FitFeedback, CreatedAt)
VALUES
(1, 2, N'Nguyễn Văn Khách', 5, N'Vải thun 100% cotton dày dặn sờ rất thích tay. Mình 1m73 nặng 66kg chọn size L mặc vừa in, vai áo ngay ngắn. Sau 3 lần giặt máy không bị xù lông hay nhão cổ.', 173, 66, N'L', N'Đen Basic', 'VUA_VAN', DATEADD(DAY, -12, GETDATE())),
(1, 3, N'Trần Thị Mai', 5, N'Mua tặng bạn trai áo này mặc đẹp lắm, phom áo đứng form và đóng gói hộp rất sang chảnh. Vote 5 sao ủng hộ shop!', 160, 49, N'M', N'Đen Basic', 'VUA_VAN', DATEADD(DAY, -10, GETDATE())),
(2, 2, N'Phạm Hoàng Long', 5, N'Áo polo vải cá sấu dệt tổ ong thoáng mát cực kỳ, đi làm cả ngày không hề bí bách. Màu xanh navy rất tôn da nam tính.', 175, 70, N'L', N'Xanh Navy', 'VUA_VAN', DATEADD(DAY, -8, GETDATE())),
(2, NULL, N'Lê Minh Tuấn', 4, N'Chất vải đẹp, chỉ thừa được cắt gọn gàng. Ai thích mặc thoải mái thì nên tăng 1 size nhé, mình thấy hơi ôm một tí.', 170, 72, N'M', N'Xanh Navy', 'CHAT', DATEADD(DAY, -6, GETDATE())),
(3, NULL, N'Đặng Quốc Bảo', 5, N'Áo phom rộng đúng chuẩn streetwear Hàn Quốc, hình in lụa giặt máy thoải mái không bị nứt vỡ. Mặc cùng quần short rất hợp.', 168, 62, N'L', N'Xám Khói', 'VUA_VAN', DATEADD(DAY, -5, GETDATE())),
(6, 2, N'Nguyễn Văn Khách', 5, N'Áo sơ mi sợi tre bamboo sờ mát rượi, chống nhăn tốt đúng như quảng cáo, giặt xong phơi thẳng là mặc được ngay không cần là ủi nhiều.', 174, 67, N'L', N'Trắng Công Sở', 'VUA_VAN', DATEADD(DAY, -9, GETDATE())),
(7, NULL, N'Vũ Đình Trọng', 4, N'Sơ mi oxford vải dày dặn lịch sự, cổ cài nút đứng form. Đi gặp khách hàng rất tự tin. Sẽ ủng hộ shop thêm màu khác.', 178, 75, N'L', N'Xanh Dương Oxford', 'VUA_VAN', DATEADD(DAY, -4, GETDATE())),
(8, 3, N'Trần Thị Mai', 5, N'Áo lụa mềm mịn mát tay, nơ cổ thắt nhẹ nhàng nữ tính, mặc đi làm sếp khen hoài. Nàng nào 50kg chọn size M là chuẩn đẹp nhé.', 158, 48, N'M', N'Be Sữa', 'VUA_VAN', DATEADD(DAY, -11, GETDATE())),
(11, 2, N'Nguyễn Văn Khách', 5, N'Quần jean co giãn rất tốt, ngồi xổm hay đi xe máy đều êm không bị tức bụng. Màu xanh denim wash rất xịn sò.', 172, 65, N'L', N'Xanh Denim Vintage', 'VUA_VAN', DATEADD(DAY, -7, GETDATE())),
(12, 3, N'Nguyễn Thu Hằng', 5, N'Quần cạp cao hack dáng đỉnh chóp luôn mn ơi, che khuyết điểm đùi to cực tốt. Đi giày sneaker vào trông cao như 1m7!', 162, 53, N'M', N'Xanh Retro Nhạt', 'VUA_VAN', DATEADD(DAY, -3, GETDATE())),
(13, NULL, N'Bùi Thanh Tùng', 4, N'Quần kaki ống suông đẹp, chất vải đứng form không bám bụi. Mình hơi mập bụng nên mặc size L hơi vừa khít một chút.', 169, 74, N'L', N'Màu Be Vàng', 'CHAT', DATEADD(DAY, -5, GETDATE())),
(16, 2, N'Nguyễn Văn Khách', 5, N'Bomber chất lượng ngoài mong đợi, khóa kéo mượt mà, lót trần bông ấm áp. Gió mùa về khoác em này thì hết nước chấm!', 175, 68, N'L', N'Đen Cá Tính', 'VUA_VAN', DATEADD(DAY, -14, GETDATE())),
(17, NULL, N'Trịnh Quang Hưng', 5, N'Áo gió đi mưa phùn chống nước tốt, có túi khóa zip bên trong tiện để điện thoại và ví tiền. Rất đáng đồng tiền bát gạo.', 170, 65, N'L', N'Xanh Đen Phối Bạc', 'VUA_VAN', DATEADD(DAY, -2, GETDATE())),
(21, 3, N'Trần Thị Mai', 5, N'Đầm hoa nhí xinh xỉu! Vải voan tơ có lót trong kín đáo, mặc đi du lịch chụp ảnh lung linh ai cũng khen. Shop tư vấn size rất có tâm.', 159, 47, N'S', N'Vàng Hoa Cúc', 'VUA_VAN', DATEADD(DAY, -6, GETDATE())),
(25, 2, N'Nguyễn Văn Khách', 5, N'Set đồ thể thao thun lạnh nhẹ tanh, thấm mồ hôi cực nhanh khi chạy bộ. Quần có túi kéo khóa an toàn, giá lại quá hời!', 173, 67, N'L', N'Xanh Than Mạnh Mẽ', 'VUA_VAN', DATEADD(DAY, -1, GETDATE()));
GO

-- 5.7 NẠP ĐƠN HÀNG MẪU (Sẵn sàng hiển thị dữ liệu thực tế trên Admin Dashboard)
INSERT INTO dbo.Orders (OrderCode, UserId, CustomerName, CustomerPhone, ShippingAddress, Note, SubTotal, ShippingFee, DiscountAmount, TotalAmount, CouponCode, PaymentMethod, PaymentStatus, OrderStatus, CreatedAt)
VALUES
('DH2026111501', 2, N'Nguyễn Văn Khách', '0912345678', N'Số 123 Lê Lợi, Phường Bến Nghé, Quận 1, TP. Hồ Chí Minh', N'Giao hàng giờ hành chính', 659000, 0, 50000, 609000, 'WELCOME10', 'VIETQR', 'PAID', 'COMPLETED', DATEADD(DAY, -3, GETDATE())),
('DH2026111602', 3, N'Trần Thị Mai', '0987654321', N'Số 45 Cầu Giấy, Quận Cầu Giấy, Hà Nội', N'Gọi điện trước khi giao', 579000, 0, 30000, 549000, 'FREESHIP', 'COD', 'UNPAID', 'SHIPPING', DATEADD(DAY, -2, GETDATE())),
('DH2026111703', NULL, N'Lê Minh Tuấn', '0933445566', N'Tòa nhà Landmark 81, Quận Bình Thạnh, TP. Hồ Chí Minh', N'Gửi lễ tân nhận giúp', 478000, 30000, 0, 508000, NULL, 'VIETQR', 'PAID', 'CONFIRMED', DATEADD(DAY, -1, GETDATE()));
GO

-- 5.8 NẠP CHI TIẾT ĐƠN HÀNG MẪU (Sử dụng subquery theo OrderCode & Sku để đảm bảo an toàn tuyệt đối)
-- Đơn hàng 1 (DH2026111501): 1 Áo sơ mi bamboo trắng L + 1 Quần jean nam xanh denim L
INSERT INTO dbo.OrderItems (OrderId, ProductVariantId, ProductName, Size, ColorName, Quantity, UnitPrice)
VALUES
(
    (SELECT Id FROM dbo.Orders WHERE OrderCode = 'DH2026111501'),
    (SELECT Id FROM dbo.ProductVariants WHERE Sku = 'SM-06-L-TRANG'),
    N'Áo Sơ Mi Nam Tay Dài Chống Nhăn Bamboo', N'L', N'Trắng Công Sở', 1, 299000
),
(
    (SELECT Id FROM dbo.Orders WHERE OrderCode = 'DH2026111501'),
    (SELECT Id FROM dbo.ProductVariants WHERE Sku = 'JN-11-L-DENIM'),
    N'Quần Jean Nam Slimfit Co Giãn Rách Gối Cá Tính', N'L', N'Xanh Denim Vintage', 1, 360000
);

-- Đơn hàng 2 (DH2026111602): 1 Đầm chữ A hoa nhí vàng M + 1 Áo sơ mi lụa tay phồng Be S
INSERT INTO dbo.OrderItems (OrderId, ProductVariantId, ProductName, Size, ColorName, Quantity, UnitPrice)
VALUES
(
    (SELECT Id FROM dbo.Orders WHERE OrderCode = 'DH2026111602'),
    (SELECT Id FROM dbo.ProductVariants WHERE Sku = 'DM-21-M-VANG'),
    N'Đầm Nữ Chữ A Dáng Xòe Họa Tiết Hoa Nhí Vintage', N'M', N'Vàng Hoa Cúc', 1, 320000
),
(
    (SELECT Id FROM dbo.Orders WHERE OrderCode = 'DH2026111602'),
    (SELECT Id FROM dbo.ProductVariants WHERE Sku = 'SM-08-S-BE'),
    N'Áo Sơ Mi Lụa Tay Phồng Nữ Công Sở Thanh Lịch', N'S', N'Be Sữa', 1, 259000
);

-- Đơn hàng 3 (DH2026111703): 2 Áo polo nam xanh Navy L
INSERT INTO dbo.OrderItems (OrderId, ProductVariantId, ProductName, Size, ColorName, Quantity, UnitPrice)
VALUES
(
    (SELECT Id FROM dbo.Orders WHERE OrderCode = 'DH2026111703'),
    (SELECT Id FROM dbo.ProductVariants WHERE Sku = 'PL-02-L-NAVY'),
    N'Áo Thun Polo Nam Phối Cổ Dệt Bo Cao Cấp', N'L', N'Xanh Navy', 2, 229000
);
GO

PRINT N'====================================================================';
PRINT N'>>> HOÀN TẤT NẠP CẤU TRÚC VÀ DỮ LIỆU SEED CHO FASHION STORE DB! <<<';
PRINT N'====================================================================';
GO
