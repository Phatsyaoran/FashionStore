package com.fashion.store.service;

import com.fashion.store.dto.*;
import com.fashion.store.entity.Category;
import com.fashion.store.entity.Order;
import com.fashion.store.entity.Product;
import com.fashion.store.entity.ProductVariant;
import com.fashion.store.exception.BadRequestException;
import com.fashion.store.exception.ResourceNotFoundException;
import com.fashion.store.repository.CategoryRepository;
import com.fashion.store.repository.OrderItemRepository;
import com.fashion.store.repository.OrderRepository;
import com.fashion.store.repository.ProductRepository;
import com.fashion.store.repository.ProductVariantRepository;
import com.fashion.store.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.Normalizer;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private static final Set<String> ALLOWED_IMAGE_EXTENSIONS = Set.of("jpg", "jpeg", "png", "webp");

    private final ProductRepository productRepository;
    private final ProductVariantRepository productVariantRepository;
    private final CategoryRepository categoryRepository;
    private final OrderRepository orderRepository;
    private final OrderService orderService;
    private final ReviewRepository reviewRepository;
    private final OrderItemRepository orderItemRepository;

    @Value("${app.upload.dir:./uploads}")
    private String uploadDir;

    @Override
    @Transactional(readOnly = true)
    public DashboardStatsDTO getDashboardStats() {
        BigDecimal totalRevenue = orderRepository.calculateTotalRevenue();
        long totalOrders = orderRepository.count();
        long pendingOrders = orderRepository.countByOrderStatus("PENDING");
        long shippingOrders = orderRepository.countByOrderStatus("SHIPPING");
        long completedOrders = orderRepository.countByOrderStatus("COMPLETED");
        long cancelledOrders = orderRepository.countByOrderStatus("CANCELLED");
        long totalProducts = productRepository.count();
        long lowStockCount = productVariantRepository.countByStockQuantityLessThanEqual(5);

        Map<String, Long> ordersByStatus = new HashMap<>();
        ordersByStatus.put("PENDING", pendingOrders);
        ordersByStatus.put("CONFIRMED", orderRepository.countByOrderStatus("CONFIRMED"));
        ordersByStatus.put("PACKING", orderRepository.countByOrderStatus("PACKING"));
        ordersByStatus.put("SHIPPING", shippingOrders);
        ordersByStatus.put("COMPLETED", completedOrders);
        ordersByStatus.put("CANCELLED", cancelledOrders);

        return DashboardStatsDTO.builder()
                .totalRevenue(totalRevenue)
                .totalOrders(totalOrders)
                .pendingOrders(pendingOrders)
                .shippingOrders(shippingOrders)
                .completedOrders(completedOrders)
                .cancelledOrders(cancelledOrders)
                .totalProducts(totalProducts)
                .lowStockCount(lowStockCount)
                .ordersByStatus(ordersByStatus)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<LowStockVariantDTO> getLowStockVariants(int threshold) {
        List<ProductVariant> variants = productVariantRepository.findByStockQuantityLessThanEqualOrderByStockQuantityAsc(threshold);

        return variants.stream().map(v -> {
            Product p = v.getProduct();
            return LowStockVariantDTO.builder()
                    .variantId(v.getId())
                    .productId(p != null ? p.getId() : null)
                    .productName(p != null ? p.getName() : "N/A")
                    .productSlug(p != null ? p.getSlug() : "")
                    .thumbnailUrl(p != null ? p.getThumbnailUrl() : "")
                    .size(v.getSize())
                    .colorName(v.getColorName())
                    .colorHex(v.getColorHex())
                    .sku(v.getSku())
                    .stockQuantity(v.getStockQuantity())
                    .price(v.getPrice())
                    .build();
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ProductVariantDTO updateVariantStock(Long variantId, StockUpdateRequest request) {
        ProductVariant variant = productVariantRepository.findById(variantId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy biến thể sản phẩm với ID: " + variantId));

        variant.setStockQuantity(request.getStockQuantity());
        ProductVariant saved = productVariantRepository.save(variant);

        return ProductVariantDTO.builder()
                .id(saved.getId())
                .productId(saved.getProduct() != null ? saved.getProduct().getId() : null)
                .size(saved.getSize())
                .colorName(saved.getColorName())
                .colorHex(saved.getColorHex())
                .price(saved.getPrice())
                .stockQuantity(saved.getStockQuantity())
                .sku(saved.getSku())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponseDTO> getAllOrders() {
        return orderService.getAllOrders();
    }

    @Override
    @Transactional
    public OrderResponseDTO updateOrderStatus(String orderCode, OrderStatusUpdateRequest request) {
        return orderService.updateOrderStatus(orderCode, request);
    }

    @Override
    @Transactional
    public ProductDTO toggleProductFlashSale(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm với ID: " + productId));

        boolean current = Boolean.TRUE.equals(product.getIsFlashSale());
        product.setIsFlashSale(!current);
        if (!current) {
            // Khi bật Flash Sale: gia hạn thời gian 7 ngày nếu chưa có hoặc đã hết hạn
            if (product.getFlashSaleEndTime() == null || product.getFlashSaleEndTime().isBefore(LocalDateTime.now())) {
                product.setFlashSaleEndTime(LocalDateTime.now().plusDays(7));
            }
        }
        Product saved = productRepository.save(product);
        return mapToProductDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] exportOrdersToCsv() {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            // Ghi UTF-8 BOM bắt buộc (0xEF, 0xBB, 0xBF) để Excel nhận diện chuẩn tiếng Việt
            baos.write(new byte[]{(byte) 0xEF, (byte) 0xBB, (byte) 0xBF});

            OutputStreamWriter osw = new OutputStreamWriter(baos, StandardCharsets.UTF_8);
            PrintWriter writer = new PrintWriter(osw);

            // Header CSV
            writer.println("Mã đơn hàng,Khách hàng,Số điện thoại,Địa chỉ giao hàng,Ghi chú,Tiền hàng (VNĐ),Phí ship (VNĐ),Giảm giá (VNĐ),Tổng tiền (VNĐ),Phương thức thanh toán,Trạng thái thanh toán,Trạng thái đơn hàng,Mã voucher,Ngày đặt hàng");

            List<com.fashion.store.entity.Order> orders = orderRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

            for (com.fashion.store.entity.Order o : orders) {
                String statusVi = switch (o.getOrderStatus() != null ? o.getOrderStatus().toUpperCase() : "") {
                    case "PENDING" -> "Đang chờ xác nhận";
                    case "CONFIRMED" -> "Đã xác nhận";
                    case "PACKING" -> "Đang đóng gói";
                    case "SHIPPING" -> "Đang giao hàng";
                    case "COMPLETED" -> "Hoàn thành";
                    case "CANCELLED" -> "Đã hủy";
                    default -> o.getOrderStatus();
                };

                String paymentStatusVi = "PAID".equalsIgnoreCase(o.getPaymentStatus()) ? "Đã thanh toán" : "Chưa thanh toán";
                String paymentMethodVi = "VIETQR".equalsIgnoreCase(o.getPaymentMethod()) ? "Chuyển khoản VietQR" : "Thu hộ COD khi nhận hàng";
                String createdStr = o.getCreatedAt() != null ? dtf.format(o.getCreatedAt()) : "";

                writer.println(String.join(",",
                        escapeCsv(o.getOrderCode()),
                        escapeCsv(o.getCustomerName()),
                        escapeCsv("=\"" + (o.getCustomerPhone() != null ? o.getCustomerPhone() : "") + "\""), // Giữ nguyên số 0 đầu trong Excel
                        escapeCsv(o.getShippingAddress()),
                        escapeCsv(o.getNote()),
                        escapeCsv(o.getSubTotal()),
                        escapeCsv(o.getShippingFee()),
                        escapeCsv(o.getDiscountAmount()),
                        escapeCsv(o.getTotalAmount()),
                        escapeCsv(paymentMethodVi),
                        escapeCsv(paymentStatusVi),
                        escapeCsv(statusVi),
                        escapeCsv(o.getCouponCode()),
                        escapeCsv(createdStr)
                ));
            }

            writer.flush();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi xuất file CSV đơn hàng: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] exportInventoryToCsv() {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            // Ghi UTF-8 BOM bắt buộc (0xEF, 0xBB, 0xBF) để Excel nhận diện chuẩn tiếng Việt
            baos.write(new byte[]{(byte) 0xEF, (byte) 0xBB, (byte) 0xBF});

            OutputStreamWriter osw = new OutputStreamWriter(baos, StandardCharsets.UTF_8);
            PrintWriter writer = new PrintWriter(osw);

            // Header CSV
            writer.println("Mã SKU,Tên sản phẩm,Danh mục,Kích thước,Màu sắc,Mã màu Hex,Giá bán (VNĐ),Số lượng tồn kho (chiếc),Tình trạng kho");

            List<ProductVariant> variants = productVariantRepository.findAll(Sort.by(Sort.Direction.ASC, "sku"));

            for (ProductVariant v : variants) {
                Product p = v.getProduct();
                String productName = p != null ? p.getName() : "N/A";
                String categoryName = (p != null && p.getCategory() != null) ? p.getCategory().getName() : "N/A";

                int stock = v.getStockQuantity() != null ? v.getStockQuantity() : 0;
                String statusVi;
                if (stock == 0) {
                    statusVi = "Hết hàng";
                } else if (stock <= 5) {
                    statusVi = "Sắp hết hàng (Cần nhập)";
                } else {
                    statusVi = "Còn hàng sẵn sàng";
                }

                writer.println(String.join(",",
                        escapeCsv(v.getSku()),
                        escapeCsv(productName),
                        escapeCsv(categoryName),
                        escapeCsv(v.getSize()),
                        escapeCsv(v.getColorName()),
                        escapeCsv(v.getColorHex()),
                        escapeCsv(v.getPrice()),
                        escapeCsv(stock),
                        escapeCsv(statusVi)
                ));
            }

            writer.flush();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi xuất file CSV tồn kho: " + e.getMessage(), e);
        }
    }

    private String escapeCsv(Object value) {
        if (value == null) return "\"\"";
        String str = value.toString().trim();
        if (str.contains(",") || str.contains("\"") || str.contains("\n") || str.contains("\r")) {
            return "\"" + str.replace("\"", "\"\"") + "\"";
        }
        return "\"" + str + "\"";
    }

    private ProductDTO mapToProductDTO(Product product) {
        List<ProductVariant> variants = productVariantRepository.findByProductId(product.getId());

        List<String> sizes = variants.stream()
                .map(ProductVariant::getSize)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());

        List<String> colors = variants.stream()
                .map(ProductVariant::getColorName)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());

        List<String> colorHexes = variants.stream()
                .map(ProductVariant::getColorHex)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());

        int totalStock = variants.stream()
                .mapToInt(v -> v.getStockQuantity() != null ? v.getStockQuantity() : 0)
                .sum();

        Double rawAvg = reviewRepository.getAverageRating(product.getId());
        double avgRating = rawAvg != null ? Math.round(rawAvg * 10.0) / 10.0 : 0.0;
        int reviewCount = (int) reviewRepository.countByProductId(product.getId());

        Integer discountPercent = null;
        if (product.getBasePrice() != null && product.getSalePrice() != null
                && product.getBasePrice().compareTo(product.getSalePrice()) > 0
                && product.getBasePrice().compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal diff = product.getBasePrice().subtract(product.getSalePrice());
            discountPercent = diff.multiply(BigDecimal.valueOf(100))
                    .divide(product.getBasePrice(), 0, RoundingMode.HALF_UP)
                    .intValue();
        }

        return ProductDTO.builder()
                .id(product.getId())
                .categoryId(product.getCategory() != null ? product.getCategory().getId() : null)
                .categoryName(product.getCategory() != null ? product.getCategory().getName() : "")
                .categorySlug(product.getCategory() != null ? product.getCategory().getSlug() : "")
                .name(product.getName())
                .slug(product.getSlug())
                .description(product.getDescription())
                .basePrice(product.getBasePrice())
                .salePrice(product.getSalePrice())
                .discountPercent(discountPercent)
                .isFlashSale(product.getIsFlashSale())
                .flashSaleEndTime(product.getFlashSaleEndTime())
                .thumbnailUrl(product.getThumbnailUrl())
                .availableSizes(sizes)
                .availableColors(colors)
                .availableColorHexes(colorHexes)
                .totalStock(totalStock)
                .ratingAverage(avgRating)
                .reviewCount(reviewCount)
                .createdAt(product.getCreatedAt())
                .build();
    }

    @Override
    @Transactional
    public void deleteProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm có ID: " + productId));

        // Kiểm tra ràng buộc an toàn: Nếu sản phẩm đã có đơn hàng thì không xóa cứng để tránh lỗi SQL Server 500
        if (orderItemRepository.existsByProductVariant_Product_Id(productId)) {
            throw new BadRequestException("Không thể xóa sản phẩm \"" + product.getName()
                    + "\" do đã phát sinh đơn đặt hàng trong hệ thống. Bạn có thể tắt Flash Sale hoặc chỉnh tồn kho về 0 để ngừng kinh doanh an toàn.");
        }

        productRepository.delete(product);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DailyRevenueDTO> getRevenueStats(int days) {
        int safeDays = Math.min(Math.max(days, 1), 90);
        LocalDate today = LocalDate.now();
        LocalDate startDate = today.minusDays(safeDays - 1L);

        List<Order> activeOrders = orderRepository.findActiveOrdersSince(startDate.atStartOfDay());

        Map<LocalDate, BigDecimal> revenueByDate = new HashMap<>();
        Map<LocalDate, Long> countByDate = new HashMap<>();

        for (Order o : activeOrders) {
            if (o.getCreatedAt() == null) continue;
            LocalDate d = o.getCreatedAt().toLocalDate();
            BigDecimal amount = o.getTotalAmount() != null ? o.getTotalAmount() : BigDecimal.ZERO;
            revenueByDate.merge(d, amount, BigDecimal::add);
            countByDate.merge(d, 1L, Long::sum);
        }

        DateTimeFormatter isoFmt = DateTimeFormatter.ISO_LOCAL_DATE;
        DateTimeFormatter labelFmt = DateTimeFormatter.ofPattern("dd/MM");

        List<DailyRevenueDTO> result = new ArrayList<>();
        for (int i = 0; i < safeDays; i++) {
            LocalDate d = startDate.plusDays(i);
            result.add(DailyRevenueDTO.builder()
                    .date(d.format(isoFmt))
                    .label(d.format(labelFmt))
                    .revenue(revenueByDate.getOrDefault(d, BigDecimal.ZERO))
                    .orderCount(countByDate.getOrDefault(d, 0L))
                    .build());
        }
        return result;
    }

    @Override
    public String uploadImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("Vui lòng chọn file ảnh để tải lên.");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !originalFilename.contains(".")) {
            throw new BadRequestException("Tên file ảnh không hợp lệ.");
        }

        String ext = originalFilename.substring(originalFilename.lastIndexOf('.') + 1).toLowerCase(Locale.ROOT);
        if (!ALLOWED_IMAGE_EXTENSIONS.contains(ext)) {
            throw new BadRequestException("Chỉ hỗ trợ định dạng ảnh: jpg, jpeg, png, webp.");
        }

        String contentType = file.getContentType();
        if (contentType != null && !contentType.toLowerCase(Locale.ROOT).startsWith("image/")) {
            throw new BadRequestException("File tải lên không phải là định dạng hình ảnh hợp lệ.");
        }

        try {
            Path uploadPath = Paths.get(uploadDir != null ? uploadDir : "./uploads").toAbsolutePath().normalize();
            Files.createDirectories(uploadPath);

            String uniqueName = UUID.randomUUID().toString().replace("-", "") + "." + ext;
            Path targetPath = uploadPath.resolve(uniqueName).normalize();
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            return "/uploads/" + uniqueName;
        } catch (IOException e) {
            throw new RuntimeException("Không thể lưu file ảnh lên máy chủ: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public ProductDTO createProduct(AdminProductRequest request) {
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy danh mục ID: " + request.getCategoryId()));

        String rawSlug = (request.getSlug() != null && !request.getSlug().trim().isEmpty())
                ? request.getSlug().trim()
                : request.getName().trim();
        String uniqueSlug = generateUniqueSlug(rawSlug, null);

        boolean flashSale = Boolean.TRUE.equals(request.getIsFlashSale());
        Product product = Product.builder()
                .category(category)
                .name(request.getName().trim())
                .slug(uniqueSlug)
                .description(request.getDescription() != null ? request.getDescription().trim() : "")
                .basePrice(request.getBasePrice())
                .salePrice(request.getSalePrice())
                .isFlashSale(flashSale)
                .flashSaleEndTime(flashSale ? LocalDateTime.now().plusDays(7) : null)
                .thumbnailUrl(request.getThumbnailUrl() != null ? request.getThumbnailUrl().trim() : null)
                .createdAt(LocalDateTime.now())
                .build();

        Product savedProduct = productRepository.save(product);

        Set<String> seenSkus = new HashSet<>();
        for (AdminProductVariantRequest vReq : request.getVariants()) {
            String sku = resolveUniqueSku(vReq.getSku(), savedProduct.getId(), vReq.getSize(), vReq.getColorName(), null, seenSkus);
            ProductVariant variant = ProductVariant.builder()
                    .product(savedProduct)
                    .size(vReq.getSize().trim().toUpperCase(Locale.ROOT))
                    .colorName(vReq.getColorName().trim())
                    .colorHex(vReq.getColorHex() != null && !vReq.getColorHex().trim().isEmpty() ? vReq.getColorHex().trim() : "#111827")
                    .sku(sku)
                    .price(vReq.getPrice())
                    .stockQuantity(vReq.getStockQuantity())
                    .build();
            productVariantRepository.save(variant);
        }

        return mapToProductDTO(savedProduct);
    }

    @Override
    @Transactional
    public ProductDTO updateProduct(Long productId, AdminProductRequest request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm có ID: " + productId));

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy danh mục ID: " + request.getCategoryId()));

        String rawSlug = (request.getSlug() != null && !request.getSlug().trim().isEmpty())
                ? request.getSlug().trim()
                : request.getName().trim();
        String uniqueSlug = generateUniqueSlug(rawSlug, productId);

        boolean flashSale = Boolean.TRUE.equals(request.getIsFlashSale());
        product.setCategory(category);
        product.setName(request.getName().trim());
        product.setSlug(uniqueSlug);
        product.setDescription(request.getDescription() != null ? request.getDescription().trim() : "");
        product.setBasePrice(request.getBasePrice());
        product.setSalePrice(request.getSalePrice());
        product.setIsFlashSale(flashSale);
        if (flashSale && (product.getFlashSaleEndTime() == null || product.getFlashSaleEndTime().isBefore(LocalDateTime.now()))) {
            product.setFlashSaleEndTime(LocalDateTime.now().plusDays(7));
        } else if (!flashSale) {
            product.setFlashSaleEndTime(null);
        }
        if (request.getThumbnailUrl() != null && !request.getThumbnailUrl().trim().isEmpty()) {
            product.setThumbnailUrl(request.getThumbnailUrl().trim());
        }

        Product savedProduct = productRepository.save(product);

        List<ProductVariant> existingVariants = productVariantRepository.findByProductId(productId);
        Map<Long, ProductVariant> existingMap = existingVariants.stream()
                .collect(Collectors.toMap(ProductVariant::getId, v -> v));

        Set<Long> retainedIds = new HashSet<>();
        Set<String> seenSkus = new HashSet<>();

        for (AdminProductVariantRequest vReq : request.getVariants()) {
            if (vReq.getId() != null && existingMap.containsKey(vReq.getId())) {
                ProductVariant existing = existingMap.get(vReq.getId());
                String sku = resolveUniqueSku(vReq.getSku(), productId, vReq.getSize(), vReq.getColorName(), existing.getId(), seenSkus);
                existing.setSize(vReq.getSize().trim().toUpperCase(Locale.ROOT));
                existing.setColorName(vReq.getColorName().trim());
                existing.setColorHex(vReq.getColorHex() != null && !vReq.getColorHex().trim().isEmpty() ? vReq.getColorHex().trim() : "#111827");
                existing.setSku(sku);
                existing.setPrice(vReq.getPrice());
                existing.setStockQuantity(vReq.getStockQuantity());
                productVariantRepository.save(existing);
                retainedIds.add(existing.getId());
            } else {
                String sku = resolveUniqueSku(vReq.getSku(), productId, vReq.getSize(), vReq.getColorName(), null, seenSkus);
                ProductVariant created = ProductVariant.builder()
                        .product(savedProduct)
                        .size(vReq.getSize().trim().toUpperCase(Locale.ROOT))
                        .colorName(vReq.getColorName().trim())
                        .colorHex(vReq.getColorHex() != null && !vReq.getColorHex().trim().isEmpty() ? vReq.getColorHex().trim() : "#111827")
                        .sku(sku)
                        .price(vReq.getPrice())
                        .stockQuantity(vReq.getStockQuantity())
                        .build();
                productVariantRepository.save(created);
            }
        }

        for (ProductVariant oldVariant : existingVariants) {
            if (!retainedIds.contains(oldVariant.getId())) {
                if (orderItemRepository.existsByProductVariant_Id(oldVariant.getId())) {
                    throw new BadRequestException("Không thể xóa biến thể SKU '" + oldVariant.getSku()
                            + "' vì đã có đơn hàng tham chiếu. Hãy giữ biến thể này và đặt tồn kho về 0.");
                }
                productVariantRepository.delete(oldVariant);
            }
        }

        return mapToProductDTO(savedProduct);
    }

    private String generateUniqueSlug(String input, Long currentProductId) {
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
                .replace('đ', 'd')
                .replace('Đ', 'D')
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("^-+|-+$", "");

        if (normalized.isEmpty()) {
            normalized = "san-pham";
        }

        String candidate = normalized;
        int suffix = 1;
        while (true) {
            Optional<Product> existing = productRepository.findBySlug(candidate);
            if (existing.isEmpty() || (currentProductId != null && existing.get().getId().equals(currentProductId))) {
                return candidate;
            }
            candidate = normalized + "-" + suffix++;
        }
    }

    private String resolveUniqueSku(String requestedSku, Long productId, String size, String colorName, Long currentVariantId, Set<String> seenSkus) {
        String baseSku;
        if (requestedSku != null && !requestedSku.trim().isEmpty()) {
            baseSku = requestedSku.trim().toUpperCase(Locale.ROOT);
        } else {
            String colorCode = Normalizer.normalize(colorName != null ? colorName : "STD", Normalizer.Form.NFD)
                    .replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
                    .replace('đ', 'd').replace('Đ', 'D')
                    .replaceAll("[^a-zA-Z0-9]", "")
                    .toUpperCase(Locale.ROOT);
            if (colorCode.length() > 6) colorCode = colorCode.substring(0, 6);
            if (colorCode.isEmpty()) colorCode = "STD";
            baseSku = "SP" + productId + "-" + size.trim().toUpperCase(Locale.ROOT) + "-" + colorCode;
        }

        String candidate = baseSku;
        int suffix = 1;
        while (true) {
            boolean usedInBatch = seenSkus.contains(candidate);
            Optional<ProductVariant> existing = productVariantRepository.findBySku(candidate);
            boolean usedInDb = existing.isPresent() && (currentVariantId == null || !existing.get().getId().equals(currentVariantId));

            if (!usedInBatch && !usedInDb) {
                seenSkus.add(candidate);
                return candidate;
            }
            candidate = baseSku + "-" + suffix++;
        }
    }
}
