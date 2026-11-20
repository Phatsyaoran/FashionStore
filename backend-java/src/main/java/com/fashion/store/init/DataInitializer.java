package com.fashion.store.init;

import com.fashion.store.entity.User;
import com.fashion.store.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        log.info(">>> Khởi chạy DataInitializer: Đồng bộ và kiểm tra mã hóa mật khẩu BCrypt...");

        // 1. Kiểm tra tài khoản Admin
        userRepository.findByEmail("admin@fashion.vn").ifPresentOrElse(
                admin -> {
                    if (!passwordEncoder.matches("123456", admin.getPasswordHash())) {
                        admin.setPasswordHash(passwordEncoder.encode("123456"));
                        admin.setRole("ADMIN");
                        userRepository.save(admin);
                        log.info(">>> Đã cập nhật lại mật khẩu BCrypt chuẩn cho admin@fashion.vn");
                    } else {
                        log.info(">>> Mật khẩu admin@fashion.vn đã được mã hóa BCrypt chính xác.");
                    }
                },
                () -> {
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
        );

        // 2. Kiểm tra tài khoản Customer
        userRepository.findByEmail("khachhang@fashion.vn").ifPresentOrElse(
                customer -> {
                    if (!passwordEncoder.matches("123456", customer.getPasswordHash())) {
                        customer.setPasswordHash(passwordEncoder.encode("123456"));
                        customer.setRole("CUSTOMER");
                        userRepository.save(customer);
                        log.info(">>> Đã cập nhật lại mật khẩu BCrypt chuẩn cho khachhang@fashion.vn");
                    } else {
                        log.info(">>> Mật khẩu khachhang@fashion.vn đã được mã hóa BCrypt chính xác.");
                    }
                },
                () -> {
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
        );
    }
}
