
package com.exam.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.exam.entity.Admin;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {

    // Find admin using username
    Optional<Admin> findByUsername(String username);

    // Check whether username already exists
    boolean existsByUsername(String username);
}
