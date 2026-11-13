
package com.exam.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.exam.entity.Student;
import com.exam.service.StudentService;
import com.exam.service.ExamService;

@Controller
public class LoginController {

    private final StudentService studentService;
    private final ExamService examService;

    public LoginController(
            StudentService studentService,
            ExamService examService) {

        this.studentService = studentService;
        this.examService = examService;
    }

    // Show Login Page
    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }

    // Student Login
    @PostMapping("/login")
    public String loginStudent(
            @RequestParam String email,
            @RequestParam String password,
            HttpServletRequest request,
            Model model) {

        String message = studentService.loginStudent(email, password);

        if ("Login successful!".equals(message)) {

            Student student = studentService.getStudentByEmail(email);

            if (student == null) {
                model.addAttribute("message", "Unable to find student account!");
                return "login";
            }

            // Invalidate any existing session
            HttpSession oldSession = request.getSession(false);

            if (oldSession != null) {
                oldSession.invalidate();
            }

            // Create a fresh session after successful login
            HttpSession newSession = request.getSession(true);

            newSession.setAttribute("loggedInStudent", student);

            return "redirect:" + request.getContextPath() + "/dashboard";
        }

        model.addAttribute("message", message);
        return "login";
    }

    // Student Dashboard
    @GetMapping("/dashboard")
    public String showDashboard(
            HttpSession session,
            Model model) {

        Student student = (Student) session.getAttribute("loggedInStudent");

        if (student == null) {
            return "redirect:/login";
        }

        model.addAttribute("student", student);

        return "dashboard";
    }

    // Show Available Exams
    @GetMapping("/available-exams")
    public String showAvailableExams(
            HttpSession session,
            Model model) {

        Student student = (Student) session.getAttribute("loggedInStudent");

        if (student == null) {
            return "redirect:/login";
        }

        model.addAttribute("student", student);
        model.addAttribute("exams", examService.getAllExams());

        return "available-exams";
    }

    // Student Logout
    @GetMapping("/logout")
    public String logout(HttpServletRequest request) {

        HttpSession session = request.getSession(false);

        if (session != null) {
            session.invalidate();
        }

        return "redirect:" + request.getContextPath() + "/login";
    }
}
