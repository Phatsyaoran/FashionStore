package com.fashion.store.init;

import com.fashion.store.entity.User;
import com.fashion.store.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) {
        log.info(">>> Khởi chạy DataInitializer: Kiểm tra schema bổ sung & tài khoản mẫu...");

        // 0. Đảm bảo bảng ProductImages tồn tại (PostgreSQL, tương thích với ddl-auto=none)
        try {
            jdbcTemplate.execute(
                    "CREATE TABLE IF NOT EXISTS ProductImages (" +
                    "  Id BIGSERIAL PRIMARY KEY, " +
                    "  ProductId BIGINT NOT NULL, " +
                    "  ImageUrl VARCHAR(500) NOT NULL, " +
                    "  DisplayOrder INT NOT NULL DEFAULT 0, " +
                    "  CONSTRAINT FK_ProductImages_Products FOREIGN KEY (ProductId) REFERENCES Products(Id) ON DELETE CASCADE" +
                    ")"
            );
            jdbcTemplate.execute(
                    "CREATE INDEX IF NOT EXISTS IX_ProductImages_ProductId ON ProductImages(ProductId)"
            );
        } catch (Exception e) {
            log.warn(">>> Không thể tự kiểm tra/tạo bảng ProductImages: {}", e.getMessage());
        }

        // 1. Chỉ tạo mới tài khoản Admin nếu chưa tồn tại (KHÔNG ghi đè mật khẩu cũ)
        if (userRepository.findByEmail("admin@fashion.vn").isEmpty()) {
            User newAdmin = User.builder()
                    .fullName("Quản Trị Viên")
                    .email("admin@fashion.vn")
                    .passwordHash(passwordEncoder.encode("123456"))
                    .phone("0901234567")
                    .address("Số 88 Phố Huế, Quận Hai Bà Trưng, Hà Nội")
                    .role("ADMIN")
                    .createdAt(LocalDateTime.now())
                    .build();
            userRepository.save(newAdmin);
            log.info(">>> Đã tạo mới tài khoản admin@fashion.vn");
        }

        // 2. Chỉ tạo mới tài khoản Customer nếu chưa tồn tại (KHÔNG ghi đè mật khẩu cũ)
        if (userRepository.findByEmail("khachhang@fashion.vn").isEmpty()) {
            User newCustomer = User.builder()
                    .fullName("Nguyễn Văn Khách")
                    .email("khachhang@fashion.vn")
                    .passwordHash(passwordEncoder.encode("123456"))
                    .phone("0912345678")
                    .address("Số 123 Lê Lợi, Phường Bến Nghé, Quận 1, TP. Hồ Chí Minh")
                    .role("CUSTOMER")
                    .createdAt(LocalDateTime.now())
                    .build();
            userRepository.save(newCustomer);
            log.info(">>> Đã tạo mới tài khoản khachhang@fashion.vn");
        }
    }
}
