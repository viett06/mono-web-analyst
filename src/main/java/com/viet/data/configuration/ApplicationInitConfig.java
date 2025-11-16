package com.viet.data.configuration;

import com.viet.data.constant.PredefinedRole;
import com.viet.data.entity.Role;
import com.viet.data.entity.User;
import com.viet.data.repository.RoleRepository;
import com.viet.data.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;

@Configuration
@Slf4j
public class ApplicationInitConfig {
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RoleRepository roleRepository;

    @Bean
    ApplicationRunner applicationRunner(UserRepository userRepository) {
        return args -> {
            // 1. Tạo role ADMIN nếu chưa có
            if (roleRepository.findById(PredefinedRole.ADMIN_ROLE).isEmpty()) {
                Role adminRole = new Role(
                        PredefinedRole.ADMIN_ROLE,
                        "Administrator role"
                );
                roleRepository.save(adminRole);
                log.info(">>> Created ADMIN role");
            }

            // 2. Tạo role USER nếu chưa có
            if (roleRepository.findById(PredefinedRole.USER_ROLE).isEmpty()) {
                Role userRole = new Role(
                        PredefinedRole.USER_ROLE,
                        "User role"
                );
                roleRepository.save(userRole);
                log.info(">>> Created USER role");
            }

            // 3. Tạo tài khoản admin mặc định
            if (userRepository.findByUsername("admin").isEmpty()) {
                var roles = new HashSet<Role>();
                roleRepository.findById(PredefinedRole.ADMIN_ROLE).ifPresent(roles::add);

                User user = User.builder()
                        .username("admin")
                        .email("admin@example.com") // THÊM EMAIL BẮT BUỘC
                        .password(passwordEncoder.encode("admin"))
                        .roles(roles)
                        .build();

                userRepository.save(user);
                log.warn(">>> Admin account created with:");
                log.warn(">>> Username: admin");
                log.warn(">>> Email: admin@example.com");
                log.warn(">>> Password: admin");
                log.warn(">>> Please change default password!");
            }

            // 4. (Optional) Tạo tài khoản user demo
            if (userRepository.findByUsername("user").isEmpty()) {
                var roles = new HashSet<Role>();
                roleRepository.findById(PredefinedRole.USER_ROLE).ifPresent(roles::add);

                User user = User.builder()
                        .username("user")
                        .email("user@example.com") // THÊM EMAIL
                        .password(passwordEncoder.encode("user"))
                        .roles(roles)
                        .build();

                userRepository.save(user);
                log.info(">>> Demo user account created");
            }
        };
    }
}