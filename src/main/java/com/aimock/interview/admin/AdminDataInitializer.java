package com.aimock.interview.admin;

import com.aimock.interview.common.enums.Role;
import com.aimock.interview.common.enums.UserStatus;
import com.aimock.interview.user.entity.User;
import com.aimock.interview.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminDataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;

    @Override
    public void run(String... args) {

        if (userRepository.findByEmail("admin@aimock.com").isEmpty()) {

            User admin = new User();

            admin.setEmail("admin@aimock.com");
            admin.setPassword("admin123");
            admin.setRole(Role.ADMIN);
            admin.setStatus(UserStatus.ACTIVE);

            userRepository.save(admin);
        }
    }
}