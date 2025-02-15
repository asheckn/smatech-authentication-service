package com.asheck.smatech_authentication_service.config;

import com.asheck.smatech_authentication_service.user.Role;
import com.asheck.smatech_authentication_service.user.User;
import com.asheck.smatech_authentication_service.user.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DefaultUserInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DefaultUserInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (userRepository.findByEmail("admin@hardcoded.com").isEmpty()) {
            User defaultUser = new User();
            defaultUser.setEmail("admin@hardcoded.com");
            defaultUser.setPassword(passwordEncoder.encode("Password123#"));
            defaultUser.setAddress("Hardcoded Address");
            defaultUser.setFirstName("System");
            defaultUser.setLastName("Admin");
            defaultUser.setPhoneNumber("1234567890");
            defaultUser.setIsActive(true);
            defaultUser.setRole(Role.ADMIN); // Set role based on your enums
            userRepository.save(defaultUser);
            System.out.println("Default admin user created.");
        }
    }
}

