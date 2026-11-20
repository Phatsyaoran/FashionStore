-- Migration script: Add ProductImages table for multi-image Product Gallery (Phase 6)
USE FashionStoreDB;
GO

IF OBJECT_ID('dbo.ProductImages', 'U') IS NULL
BEGIN
    CREATE TABLE dbo.ProductImages (
        Id BIGINT IDENTITY(1,1) PRIMARY KEY,
        ProductId BIGINT NOT NULL,
        ImageUrl NVARCHAR(500) NOT NULL,
        DisplayOrder INT NOT NULL DEFAULT 0,
        CONSTRAINT FK_ProductImages_Products FOREIGN KEY (ProductId) REFERENCES dbo.Products(Id) ON DELETE CASCADE
    );

    CREATE INDEX IX_ProductImages_ProductId ON dbo.ProductImages(ProductId);
END
GO
