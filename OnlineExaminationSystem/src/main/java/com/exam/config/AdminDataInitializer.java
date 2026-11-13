
package com.exam.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.beans.factory.annotation.Value;

import com.exam.entity.Admin;
import com.exam.repository.AdminRepository;

@Configuration
public class AdminDataInitializer {

    @Bean
    public CommandLineRunner createAdmin(
            AdminRepository adminRepository,
            BCryptPasswordEncoder passwordEncoder,
            @Value("${app.admin.username:admin}") String username,
            @Value("${app.admin.password:Admin@123}") String password) {

        return args -> {

            if (!adminRepository.existsByUsername(username)) {

                Admin admin = new Admin();

                admin.setUsername(username);
                admin.setPassword(passwordEncoder.encode(password));

                adminRepository.save(admin);

                System.out.println("Default Admin created!");
            } else {
                System.out.println("Admin account already exists.");
            }
        };
    }
}
