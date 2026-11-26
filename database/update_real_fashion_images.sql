-- ==============================================================================
-- FASHION STORE E-COMMERCE - CẬP NHẬT ẢNH THỜI TRANG THẬT TỪ STUDIO UNSPLASH
-- File: database/update_real_fashion_images.sql
-- Tiêu chuẩn: PostgreSQL (Neon.tech / Render / Local PostgreSQL)
-- ==============================================================================

-- 1. DANH MỤC: ÁO THUN / POLO (CategoryId = 1)
UPDATE Products
SET ThumbnailUrl = 'https://images.unsplash.com/photo-1521572163474-6864f9cf17ab?auto=format&fit=crop&w=800&q=80'
WHERE Slug = 'ao-thun-cotton-compact-regular' OR Id = 1;

UPDATE Products
SET ThumbnailUrl = 'https://images.unsplash.com/photo-1583743814966-8936f5b7be1a?auto=format&fit=crop&w=800&q=80'
WHERE Slug = 'ao-thun-polo-nam-phoi-co-det' OR Id = 2;

UPDATE Products
SET ThumbnailUrl = 'https://images.unsplash.com/photo-1576566588028-4147f3842f27?auto=format&fit=crop&w=800&q=80'
WHERE Slug = 'ao-thun-oversize-unisex-streetwear' OR Id = 3;

UPDATE Products
SET ThumbnailUrl = 'https://images.unsplash.com/photo-1562157873-818bc0726f68?auto=format&fit=crop&w=800&q=80'
WHERE Slug = 'ao-thun-co-tim-nu-modal' OR Id = 4;

UPDATE Products
SET ThumbnailUrl = 'https://images.unsplash.com/photo-1503342217505-b0a15ec3261c?auto=format&fit=crop&w=800&q=80'
WHERE Slug = 'ao-thun-tanktop-the-thao-co-gian' OR Id = 5;

-- 2. DANH MỤC: ÁO SƠ MI (CategoryId = 2)
UPDATE Products
SET ThumbnailUrl = 'https://images.unsplash.com/photo-1596755094514-f87e34085b2c?auto=format&fit=crop&w=800&q=80'
WHERE Slug = 'ao-so-mi-nam-tay-dai-chong-nhan-bamboo' OR Id = 6;

UPDATE Products
SET ThumbnailUrl = 'https://images.unsplash.com/photo-1602810318383-e386cc2a3ccf?auto=format&fit=crop&w=800&q=80'
WHERE Slug = 'ao-so-mi-oxford-co-button-down' OR Id = 7;

UPDATE Products
SET ThumbnailUrl = 'https://images.unsplash.com/photo-1598033129183-c4f50c736f10?auto=format&fit=crop&w=800&q=80'
WHERE Slug = 'ao-so-mi-lua-tay-phong-nu-cong-so' OR Id = 8;

UPDATE Products
SET ThumbnailUrl = 'https://images.unsplash.com/photo-1620012253295-c15cc3e65df4?auto=format&fit=crop&w=800&q=80'
WHERE Slug = 'ao-so-mi-hoa-tiet-cuban-retro' OR Id = 9;

UPDATE Products
SET ThumbnailUrl = 'https://images.unsplash.com/photo-1589310243389-96a5483213a8?auto=format&fit=crop&w=800&q=80'
WHERE Slug = 'ao-so-mi-nu-ke-soc-han-quoc' OR Id = 10;

-- 3. DANH MỤC: QUẦN JEAN & KAKI (CategoryId = 3)
UPDATE Products
SET ThumbnailUrl = 'https://images.unsplash.com/photo-1542272604-787c3835535d?auto=format&fit=crop&w=800&q=80'
WHERE Slug = 'quan-jean-nam-slimfit-co-gian' OR Id = 11;

UPDATE Products
SET ThumbnailUrl = 'https://images.unsplash.com/photo-1624378439575-d8705ad7ae80?auto=format&fit=crop&w=800&q=80'
WHERE Slug = 'quan-jean-ong-rong-nu-cap-cao-retro' OR Id = 12;

UPDATE Products
SET ThumbnailUrl = 'https://images.unsplash.com/photo-1473966968600-fa801b869a1a?auto=format&fit=crop&w=800&q=80'
WHERE Slug = 'quan-kaki-nam-dang-suong-cong-so' OR Id = 13;

UPDATE Products
SET ThumbnailUrl = 'https://images.unsplash.com/photo-1541099649105-f69ad21f3246?auto=format&fit=crop&w=800&q=80'
WHERE Slug = 'quan-tay-nu-dang-baggy-lung-cao' OR Id = 14;

UPDATE Products
SET ThumbnailUrl = 'https://images.unsplash.com/photo-1591195853828-11db59a44f6b?auto=format&fit=crop&w=800&q=80'
WHERE Slug = 'quan-short-jean-nu-vien-rach' OR Id = 15;

-- 4. DANH MỤC: ÁO KHOÁC & HOODIE (CategoryId = 4)
UPDATE Products
SET ThumbnailUrl = 'https://images.unsplash.com/photo-1551028719-00167b16eac5?auto=format&fit=crop&w=800&q=80'
WHERE Slug = 'ao-khoac-bomber-nam-phoi-tui-zip' OR Id = 16;

UPDATE Products
SET ThumbnailUrl = 'https://images.unsplash.com/photo-1556821840-3a63f95609a7?auto=format&fit=crop&w=800&q=80'
WHERE Slug = 'ao-khoac-gio-2-lop-chong-nuoc' OR Id = 17;

UPDATE Products
SET ThumbnailUrl = 'https://images.unsplash.com/photo-1591047139829-d91aecb6caea?auto=format&fit=crop&w=800&q=80'
WHERE Slug = 'ao-hoodie-ni-bong-unisex-tui-truoc' OR Id = 18;

UPDATE Products
SET ThumbnailUrl = 'https://images.unsplash.com/photo-1544022613-e87ca75a784a?auto=format&fit=crop&w=800&q=80'
WHERE Slug = 'ao-khoac-da-nu-dang-dai-co-vest' OR Id = 19;

UPDATE Products
SET ThumbnailUrl = 'https://images.unsplash.com/photo-1620799140408-edc6dcb6d633?auto=format&fit=crop&w=800&q=80'
WHERE Slug = 'ao-cardigan-len-det-kim-mem-nu' OR Id = 20;

-- 5. DANH MỤC: VÁY & ĐẦM NỮ (CategoryId = 5)
UPDATE Products
SET ThumbnailUrl = 'https://images.unsplash.com/photo-1595777457583-95e059d581b8?auto=format&fit=crop&w=800&q=80'
WHERE Slug = 'dam-nu-chu-a-dang-xoe-hoa-nhi' OR Id = 21;

UPDATE Products
SET ThumbnailUrl = 'https://images.unsplash.com/photo-1572804013309-59a88b7e92f1?auto=format&fit=crop&w=800&q=80'
WHERE Slug = 'dam-bodycon-du-tiec-co-vuong' OR Id = 22;

UPDATE Products
SET ThumbnailUrl = 'https://images.unsplash.com/photo-1583496661160-fb5886a0aaaa?auto=format&fit=crop&w=800&q=80'
WHERE Slug = 'chan-vay-xep-ly-tennis-nu' OR Id = 23;

UPDATE Products
SET ThumbnailUrl = 'https://images.unsplash.com/photo-1515372039744-b8f02a3ae446?auto=format&fit=crop&w=800&q=80'
WHERE Slug = 'dam-maxi-di-bien-voan-to' OR Id = 24;

-- 6. DANH MỤC: ĐỒ THỂ THAO & MẶC NHÀ (CategoryId = 6)
UPDATE Products
SET ThumbnailUrl = 'https://images.unsplash.com/photo-1515886657613-9f3515b0c78f?auto=format&fit=crop&w=800&q=80'
WHERE Slug = 'set-do-the-thao-nam-thun-lanh' OR Id = 25;

UPDATE Products
SET ThumbnailUrl = 'https://images.unsplash.com/photo-1594633312681-425c7b97ccd1?auto=format&fit=crop&w=800&q=80'
WHERE Slug = 'set-bo-pijama-lua-satin-nu' OR Id = 26;
