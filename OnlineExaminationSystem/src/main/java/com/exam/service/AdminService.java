
package com.exam.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.exam.entity.Admin;
import com.exam.repository.AdminRepository;

@Service
public class AdminService {

    private final AdminRepository adminRepository;

    private final BCryptPasswordEncoder passwordEncoder =
            new BCryptPasswordEncoder();

    public AdminService(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
    }

    // Register a new admin
    @Transactional
    public String registerAdmin(Admin admin) {

        if (admin == null
                || admin.getUsername() == null
                || admin.getUsername().isBlank()
                || admin.getPassword() == null
                || admin.getPassword().isBlank()) {

            return "Username and password are required!";
        }

        String username = admin.getUsername().trim();

        if (adminRepository.existsByUsername(username)) {
            return "Admin username already exists!";
        }

        admin.setUsername(username);

        // Encrypt password before saving
        String encryptedPassword =
                passwordEncoder.encode(admin.getPassword());

        admin.setPassword(encryptedPassword);

        adminRepository.save(admin);

        return "Admin registered successfully!";
    }

    // Admin login
    @Transactional(readOnly = true)
    public String loginAdmin(String username, String password) {

        if (username == null || username.isBlank()
                || password == null || password.isBlank()) {

            return "Username and password are required!";
        }

        Admin admin = adminRepository
                .findByUsername(username.trim())
                .orElse(null);

        // Use the same message for both login failures
        if (admin == null
                || !passwordEncoder.matches(
                        password, admin.getPassword())) {

            return "Invalid username or password!";
        }

        return "Admin login successful!";
    }
}
