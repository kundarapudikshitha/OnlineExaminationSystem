
package com.exam.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.exam.entity.Student;
import com.exam.service.StudentService;

import jakarta.validation.Valid;

@Controller
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    // Show Registration Page
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {

        model.addAttribute("student", new Student());

        return "register";
    }

    // Register New Student
    @PostMapping("/register")
    public String registerStudent(
            @Valid @ModelAttribute("student") Student student,
            BindingResult bindingResult,
            Model model) {

        // Check validation errors
        if (bindingResult.hasErrors()) {
            return "register";
        }

        // Register student
        String message = studentService.registerStudent(student);

        if ("Registration successful!".equals(message)) {

            model.addAttribute("successMessage", message);
            model.addAttribute("student", new Student());

        } else {

            model.addAttribute("errorMessage", message);
        }

        return "register";
    }
}
