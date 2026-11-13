
package com.exam.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.exam.entity.Student;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    // Find student using email
    Optional<Student> findByEmail(String email);

    // Check whether email already exists
    boolean existsByEmail(String email);
}
