
package com.exam.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.exam.entity.Student;
import com.exam.repository.StudentRepository;

@Service
public class StudentService {

    private final StudentRepository studentRepository;

    private final BCryptPasswordEncoder passwordEncoder =
            new BCryptPasswordEncoder();

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    // Register a new student
    @Transactional
    public String registerStudent(Student student) {

        if (student == null
                || student.getName() == null
                || student.getName().isBlank()
                || student.getEmail() == null
                || student.getEmail().isBlank()
                || student.getPassword() == null
                || student.getPassword().isBlank()) {

            return "All fields are required!";
        }

        String email = student.getEmail().trim().toLowerCase();

        if (studentRepository.existsByEmail(email)) {
            return "Email already registered!";
        }

        student.setEmail(email);

        // Encrypt password before saving
        String encryptedPassword =
                passwordEncoder.encode(student.getPassword());

        student.setPassword(encryptedPassword);

        studentRepository.save(student);

        return "Registration successful!";
    }

    // Student login
    @Transactional(readOnly = true)
    public String loginStudent(String email, String password) {

        if (email == null || email.isBlank()
                || password == null || password.isBlank()) {

            return "Email and password are required!";
        }

        Student student = studentRepository
                .findByEmail(email.trim().toLowerCase())
                .orElse(null);

        // Avoid revealing whether the email exists
        if (student == null
                || !passwordEncoder.matches(
                        password, student.getPassword())) {

            return "Invalid email or password!";
        }

        return "Login successful!";
    }

    // Get student by email
    @Transactional(readOnly = true)
    public Student getStudentByEmail(String email) {

        if (email == null || email.isBlank()) {
            return null;
        }

        return studentRepository
                .findByEmail(email.trim().toLowerCase())
                .orElse(null);
    }

    // Get all students
    @Transactional(readOnly = true)
    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    // Delete student
    @Transactional
    public void deleteStudent(Long id) {

        if (id == null) {
            throw new IllegalArgumentException(
                    "Student ID is required.");
        }

        if (studentRepository.existsById(id)) {
            studentRepository.deleteById(id);
        }
    }
}
