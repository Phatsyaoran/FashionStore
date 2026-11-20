package com.fashion.store.service;

import com.fashion.store.dto.*;
import com.fashion.store.entity.Coupon;
import com.fashion.store.entity.Product;
import com.fashion.store.entity.ProductVariant;
import com.fashion.store.exception.ResourceNotFoundException;
import com.fashion.store.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatAssistantServiceImpl implements ChatAssistantService {

    private final ProductRepository productRepository;
    private final ProductVariantRepository productVariantRepository;
    private final ReviewRepository reviewRepository;
    private final CouponRepository couponRepository;
    private final OrderService orderService;

    private static final List<String> DEFAULT_QUICK_REPLIES = List.of(
            "📏 Tư vấn chọn Size cho tôi",
            "🎁 Xin mã giảm giá hôm nay",
            "👕 Gợi ý đồ dưới 400k",
            "📦 Tra cứu đơn hàng"
    );

    @Override
    @Transactional(readOnly = true)
    public ChatAssistantResponse handleChatMessage(ChatAssistantRequest request) {
        String msg = request.getMessage() != null ? request.getMessage().trim() : "";
        String lowerMsg = msg.toLowerCase();

        // 1. Kiểm tra intent: Tra cứu đơn hàng (Order Tracking)
        String orderCode = extractOrderCode(msg);
        if (orderCode != null || (lowerMsg.contains("đơn") && containsAny(lowerMsg, "tra cứu", "kiểm tra", "tình trạng", "đến đâu", "vận chuyển", "mã"))) {
            if (orderCode != null) {
                return handleOrderTracking(orderCode);
            } else {
                return ChatAssistantResponse.builder()
                        .intent("ORDER_TRACKING")
                        .reply("Dạ bạn vui lòng cung cấp **Mã đơn hàng** (ví dụ: `DH1701234567` được gửi qua tin nhắn/email khi đặt hàng) để em tra cứu tình trạng vận chuyển tức thì cho bạn nhé! 🚚")
                        .quickReplies(List.of("Kiểm tra đơn DH1700000001", "Trang chủ", "Tư vấn chọn Size"))
                        .build();
            }
        }

        // 2. Kiểm tra intent: Hỏi mã giảm giá (Coupons)
        if (containsAny(lowerMsg, "mã giảm giá", "coupon", "voucher", "khuyến mãi", "ưu đãi", "giảm giá", "xin mã", "mã freeship", "có mã nào")) {
            return handleCouponInquiry();
        }

        // 3. Kiểm tra intent: Tư vấn chọn Size (Size Advice)
        Integer height = extractHeight(msg);
        Integer weight = extractWeight(msg);
        if ((height != null && weight != null) || weight != null || containsAny(lowerMsg, "tư vấn size", "chọn size", "mặc size", "size gì", "vừa không", "số đo", "chiều cao", "cân nặng")) {
            return handleSizeAdvice(height, weight, lowerMsg);
        }

        // 4. Kiểm tra intent: Gợi ý trang phục / Tìm đồ theo giá (Price Search & Recommendation)
        if (containsAny(lowerMsg, "dưới", "khoảng", "tầm", "giá", "áo", "quần", "váy", "đầm", "polo", "sơ mi", "jean", "gợi ý", "tìm đồ", "mua gì", "bán chạy")) {
            return handleProductRecommendation(lowerMsg);
        }

        // 5. Default Greeting & General Help
        return ChatAssistantResponse.builder()
                .intent("GREETING")
                .reply("Dạ xin chào bạn! Em là **Trợ Lý Ảo Thông Minh** của FashionStore ✨\n\nEm có thể hỗ trợ bạn trực tiếp các việc sau:\n" +
                        "• 📏 **Tư vấn chọn Size chuẩn** theo chiều cao & cân nặng\n" +
                        "• 🎁 **Tặng mã giảm giá & freeship** hot nhất hôm nay\n" +
                        "• 👕 **Gợi ý phối đồ** theo nhu cầu và ngân sách (VD: đồ dưới 400k)\n" +
                        "• 📦 **Tra cứu đơn hàng** tự động theo mã vận đơn\n\n" +
                        "Bạn cần em hỗ trợ điều gì cứ nhắn ngay cho em nhé! 👇")
                .quickReplies(DEFAULT_QUICK_REPLIES)
                .build();
    }

    // ==========================================
    // HANDLERS FOR EACH INTENT
    // ==========================================

    private ChatAssistantResponse handleOrderTracking(String orderCode) {
        try {
            OrderResponseDTO order = orderService.getOrderByCode(orderCode);
            String statusVi = translateOrderStatus(order.getOrderStatus());
            String paymentVi = "PAID".equalsIgnoreCase(order.getPaymentStatus()) ? "Đã thanh toán" : "Chưa thanh toán (Thu COD)";
            String formattedTotal = formatVND(order.getTotalAmount());

            String reply = String.format(
                    "📦 **Thông tin đơn hàng #%s của bạn:**\n\n" +
                            "• Trạng thái: **%s**\n" +
                            "• Người nhận: **%s** (%s)\n" +
                            "• Địa chỉ giao: %s\n" +
                            "• Thanh toán: **%s** qua %s\n" +
                            "• Tổng tiền: **%s**\n\n" +
                            "💡 Bạn cũng có thể xem trực quan tiến trình 5 bước tại trang **[Tra cứu đơn hàng](/order-tracking)**.",
                    order.getOrderCode(), statusVi, order.getCustomerName(), order.getCustomerPhone(),
                    order.getShippingAddress(), paymentVi, order.getPaymentMethod(), formattedTotal
            );

            return ChatAssistantResponse.builder()
                    .intent("ORDER_TRACKING")
                    .reply(reply)
                    .orderInfo(order)
                    .quickReplies(List.of("Trang chủ", "Tư vấn chọn Size", "Xin mã giảm giá hôm nay"))
                    .build();
        } catch (ResourceNotFoundException e) {
            return ChatAssistantResponse.builder()
                    .intent("ORDER_TRACKING")
                    .reply(String.format("Dạ em rất tiếc! Hệ thống chưa tìm thấy đơn hàng với mã **%s**. Bạn vui lòng kiểm tra lại chính xác mã đơn (thường có dạng `DH...`) hoặc liên hệ hotline để được hỗ trợ nhé!", orderCode))
                    .quickReplies(List.of("Nhập lại mã đơn", "Tư vấn chọn Size", "Săn mã giảm giá"))
                    .build();
        }
    }

    private ChatAssistantResponse handleCouponInquiry() {
        List<Coupon> activeList = couponRepository.findActiveCoupons(LocalDateTime.now());
        List<CouponDTO> dtos = activeList.stream()
                .map(c -> CouponDTO.builder()
                        .id(c.getId())
                        .code(c.getCode())
                        .discountPercent(c.getDiscountPercent())
                        .maxDiscountAmount(c.getMaxDiscountAmount())
                        .minOrderValue(c.getMinOrderValue())
                        .build())
                .collect(Collectors.toList());

        if (dtos.isEmpty()) {
            return ChatAssistantResponse.builder()
                    .intent("COUPON_LIST")
                    .reply("Dạ hiện tại các mã giảm giá đặc biệt đang chuẩn bị lên sóng cho đợt khuyến mãi sắp tới. Đừng quên đơn hàng từ 500.000đ luôn được **MIỄN PHÍ VẬN CHUYỂN TOÀN QUỐC** nha bạn! 🎉")
                    .quickReplies(DEFAULT_QUICK_REPLIES)
                    .build();
        }

        StringBuilder sb = new StringBuilder("🎁 **Danh sách Mã Giảm Giá Đang Hoạt Động Hôm Nay:**\n\n");
        for (CouponDTO c : dtos) {
            sb.append(String.format("• Mã ` %s ` : **Giảm %d%%** (Tối đa %s cho đơn từ %s)\n",
                    c.getCode(),
                    c.getDiscountPercent(),
                    formatVND(c.getMaxDiscountAmount()),
                    formatVND(c.getMinOrderValue())
            ));
        }
        sb.append("\n💡 Bạn chỉ cần sao chép mã và dán vào ô **Áp dụng Mã giảm giá** ở Giỏ hàng hoặc Thanh toán nhé!");

        return ChatAssistantResponse.builder()
                .intent("COUPON_LIST")
                .reply(sb.toString())
                .activeCoupons(dtos)
                .quickReplies(List.of("👕 Gợi ý đồ dưới 400k", "📏 Tư vấn chọn Size cho tôi", "Xem giỏ hàng"))
                .build();
    }

    private ChatAssistantResponse handleSizeAdvice(Integer height, Integer weight, String lowerMsg) {
        if (weight == null) {
            return ChatAssistantResponse.builder()
                    .intent("SIZE_ADVICE")
                    .reply("Dạ để em tư vấn chuẩn phom dáng người Việt nhất (~95% độ vừa vặn), bạn vui lòng cho em biết thêm **Chiều cao (cm)** và **Cân nặng (kg)** của bạn nhé!\n\nVí dụ: Bạn có thể nhắn `Mình cao 1m75 nặng 65kg` hoặc `1m68 58kg` 👇")
                    .quickReplies(List.of("Cao 1m65 nặng 55kg", "Cao 1m75 nặng 68kg", "Cao 1m80 nặng 75kg"))
                    .build();
        }

        int h = height != null ? height : 170; // Giả định trung bình nếu thiếu chiều cao
        int w = weight;

        // Tính size theo chuẩn phom người Việt
        String suggestedSize;
        if (w < 53) {
            suggestedSize = "S";
        } else if (w <= 62) {
            suggestedSize = "M";
        } else if (w <= 72) {
            suggestedSize = "L";
        } else {
            suggestedSize = "XL";
        }

        // Tăng 1 size nếu người cao gầy trên 178cm hoặc yêu cầu mặc rộng
        if ((h >= 178 || lowerMsg.contains("rộng") || lowerMsg.contains("oversize")) && !"XL".equals(suggestedSize)) {
            if ("S".equals(suggestedSize)) suggestedSize = "M";
            else if ("M".equals(suggestedSize)) suggestedSize = "L";
            else if ("L".equals(suggestedSize)) suggestedSize = "XL";
        }

        // Truy vấn 3 sản phẩm đang có sẵn đúng size này trong kho
        List<Product> products = productRepository.findProductsBySizeInStock(suggestedSize, PageRequest.of(0, 3));
        List<ProductDTO> productDTOs = products.stream()
                .map(this::mapToProductDTO)
                .collect(Collectors.toList());

        String reply = String.format(
                "📏 **Kết Quả Tư Vấn Phom Dáng:**\n\n" +
                        "Với chiều cao **%dcm** và cân nặng **%dkg**, phom dáng chuẩn nhất cho bạn là **SIZE %s** (Độ vừa vặn dự đoán ~95%%).\n\n" +
                        "🔥 Em vừa kiểm tra kho và tìm thấy **3 mẫu thời trang đang sẵn hàng chuẩn Size %s** rất hợp với bạn dưới đây:",
                h, w, suggestedSize, suggestedSize
        );

        return ChatAssistantResponse.builder()
                .intent("SIZE_ADVICE")
                .suggestedSize(suggestedSize)
                .reply(reply)
                .suggestedProducts(productDTOs)
                .quickReplies(List.of("🎁 Xin mã giảm giá hôm nay", "👕 Gợi ý đồ dưới 400k", "Chọn size khác"))
                .build();
    }

    private ChatAssistantResponse handleProductRecommendation(String lowerMsg) {
        BigDecimal maxPrice = extractMaxPrice(lowerMsg);
        String keyword = extractCategoryKeyword(lowerMsg);

        List<Product> products;
        if (keyword != null && !keyword.isEmpty()) {
            products = productRepository.findProductsByKeywordAndUnderPrice(keyword, maxPrice, PageRequest.of(0, 4));
            if (products.isEmpty()) {
                products = productRepository.findProductsUnderPrice(maxPrice, PageRequest.of(0, 4));
            }
        } else {
            products = productRepository.findProductsUnderPrice(maxPrice, PageRequest.of(0, 4));
        }

        List<ProductDTO> productDTOs = products.stream()
                .map(this::mapToProductDTO)
                .collect(Collectors.toList());

        String categoryHint = keyword != null ? "dòng sản phẩm **" + keyword + "** " : "";
        String reply = String.format(
                "✨ Dạ em đã lọc giúp bạn các mẫu %scó giá cực tốt **dưới %s** đang bán rất chạy tại shop:\n\n" +
                        "Bạn có thể bấm **Xem ngay** trên từng sản phẩm để chọn size, màu và thêm vào giỏ nhé! 👇",
                categoryHint, formatVND(maxPrice)
        );

        return ChatAssistantResponse.builder()
                .intent("PRICE_SEARCH")
                .reply(reply)
                .suggestedProducts(productDTOs)
                .quickReplies(List.of("📏 Tư vấn chọn Size cho tôi", "🎁 Xin mã giảm giá hôm nay", "📦 Tra cứu đơn hàng"))
                .build();
    }

    // ==========================================
    // UTILITY PARSERS & HELPERS
    // ==========================================

    private String extractOrderCode(String text) {
        if (text == null) return null;
        // Match DH followed by digits/chars, or ORD, FS
        Pattern p = Pattern.compile("\\b(DH[0-9A-Za-z]+|FS[0-9A-Za-z]+|ORD[0-9A-Za-z]+)\\b", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(text);
        if (m.find()) {
            return m.group(1).toUpperCase();
        }
        return null;
    }

    private Integer extractHeight(String text) {
        if (text == null) return null;
        // Pattern 1: 1m75, 1m70, 1m68, 1.75, 1.68
        Pattern p1 = Pattern.compile("(?:cao\\s*)?(\\d{1})[m\\.,](\\d{1,2})", Pattern.CASE_INSENSITIVE);
        Matcher m1 = p1.matcher(text);
        if (m1.find()) {
            int meter = Integer.parseInt(m1.group(1));
            String part2 = m1.group(2);
            int cm = part2.length() == 1 ? Integer.parseInt(part2) * 10 : Integer.parseInt(part2);
            int total = meter * 100 + cm;
            if (total >= 140 && total <= 210) return total;
        }

        // Pattern 2: 175cm, cao 175
        Pattern p2 = Pattern.compile("(?:cao\\s*)?(\\d{3})\\s*(?:cm)?", Pattern.CASE_INSENSITIVE);
        Matcher m2 = p2.matcher(text);
        while (m2.find()) {
            int cm = Integer.parseInt(m2.group(1));
            if (cm >= 140 && cm <= 210) return cm;
        }

        return null;
    }

    private Integer extractWeight(String text) {
        if (text == null) return null;
        // Pattern: 65kg, nặng 65kg, nang 65, 65 kí
        Pattern p = Pattern.compile("(?:nặng|nang|cân|can)?\\s*(\\d{2,3})\\s*(?:kg|kí|ký|kilo)?", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(text);
        while (m.find()) {
            String valStr = m.group(1);
            int val = Integer.parseInt(valStr);
            // Height typically >= 140; weight is typically 35 - 130
            if (val >= 35 && val <= 130) {
                return val;
            }
        }
        return null;
    }

    private BigDecimal extractMaxPrice(String text) {
        if (text == null) return BigDecimal.valueOf(500000);
        // Pattern: 400k, 400.000, 400000, 1tr, 1 triệu
        Pattern p = Pattern.compile("(?:dưới|khoảng|tầm|tới)?\\s*(\\d+(?:[\\.,]\\d+)?)\\s*(k|tr|triệu|000)?", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(text);
        while (m.find()) {
            try {
                String numStr = m.group(1).replace(",", ".");
                String unit = m.group(2) != null ? m.group(2).toLowerCase() : "";
                double num = Double.parseDouble(numStr);

                if ("k".equals(unit)) {
                    return BigDecimal.valueOf((long) (num * 1000));
                } else if ("tr".equals(unit) || "triệu".equals(unit)) {
                    return BigDecimal.valueOf((long) (num * 1000000));
                } else if ("000".equals(unit)) {
                    return BigDecimal.valueOf((long) (num * 1000));
                } else if (num >= 50000) {
                    return BigDecimal.valueOf((long) num);
                } else if (num > 0 && num <= 1000) {
                    // Trọng lượng/chiều cao không phải giá tiền
                    continue;
                }
            } catch (Exception ignored) {
            }
        }
        return BigDecimal.valueOf(450000); // Mặc định nếu không tìm thấy
    }

    private String extractCategoryKeyword(String text) {
        if (text == null) return null;
        if (text.contains("sơ mi") || text.contains("so mi")) return "Sơ Mi";
        if (text.contains("áo thun") || text.contains("ao thun") || text.contains("t-shirt")) return "Áo Thun";
        if (text.contains("polo")) return "Polo";
        if (text.contains("jean") || text.contains("bò")) return "Jean";
        if (text.contains("quần âu") || text.contains("quan au") || text.contains("quần tây")) return "Quần Âu";
        if (text.contains("quần short") || text.contains("short")) return "Short";
        if (text.contains("váy") || text.contains("đầm")) return "Váy";
        if (text.contains("khoác") || text.contains("jacket")) return "Khoác";
        return null;
    }

    private boolean containsAny(String source, String... targets) {
        if (source == null) return false;
        for (String t : targets) {
            if (source.contains(t)) return true;
        }
        return false;
    }

    private String translateOrderStatus(String status) {
        if (status == null) return "Chờ xử lý";
        return switch (status.toUpperCase()) {
            case "PENDING" -> "⏳ Đang chờ shop xác nhận";
            case "CONFIRMED" -> "✅ Đã xác nhận đơn hàng";
            case "PACKING" -> "📦 Đang đóng gói cẩn thận";
            case "SHIPPING" -> "🚚 Đang trên đường giao đến bạn";
            case "COMPLETED" -> "🎉 Giao hàng thành công";
            case "CANCELLED" -> "❌ Đơn hàng đã hủy";
            default -> status;
        };
    }

    private String formatVND(BigDecimal amount) {
        if (amount == null) return "0 VNĐ";
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.of("vi", "VN"));
        DecimalFormat df = new DecimalFormat("#,###", symbols);
        return df.format(amount) + " VNĐ";
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
}
