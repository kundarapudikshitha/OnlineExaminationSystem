
package com.exam.controller;

import java.util.List;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.exam.entity.Exam;
import com.exam.entity.Result;

import com.exam.service.AdminService;
import com.exam.service.ResultService;
import com.exam.service.StudentService;
import com.exam.service.ExamService;
import com.exam.service.QuestionService;

@Controller
public class AdminController {

    private final AdminService adminService;
    private final ResultService resultService;
    private final StudentService studentService;
    private final ExamService examService;
    private final QuestionService questionService;

    public AdminController(
            AdminService adminService,
            ResultService resultService,
            StudentService studentService,
            ExamService examService,
            QuestionService questionService) {

        this.adminService = adminService;
        this.resultService = resultService;
        this.studentService = studentService;
        this.examService = examService;
        this.questionService = questionService;
    }

    // Check whether admin is logged in
    private boolean isAdminLoggedIn(HttpSession session) {
        return session.getAttribute("loggedInAdmin") != null;
    }

    // Display admin login page
    @GetMapping("/admin/login")
    public String showAdminLogin() {
        return "admin-login";
    }

    // Process admin login
    @PostMapping("/admin/login")
    public String loginAdmin(
            @RequestParam String username,
            @RequestParam String password,
            HttpSession session,
            Model model) {

        String message = adminService.loginAdmin(username, password);

        if ("Admin login successful!".equals(message)) {
            session.setAttribute("loggedInAdmin", username);
            return "redirect:/admin/dashboard";
        }

        model.addAttribute("message",
                message != null ? message : "Invalid login details.");

        return "admin-login";
    }

    // Admin dashboard
    @GetMapping("/admin/dashboard")
    public String showAdminDashboard(
            HttpSession session,
            Model model) {

        if (!isAdminLoggedIn(session)) {
            return "redirect:/admin/login";
        }

        addStatisticsAttributes(model);

        return "admin-dashboard";
    }

    // Admin logout
    @GetMapping("/admin/logout")
    public String logoutAdmin(HttpSession session) {

        session.invalidate();

        return "redirect:/admin/login";
    }

    // View all examination results
    @GetMapping("/admin/view-results")
    public String viewAllResults(
            HttpSession session,
            Model model) {

        if (!isAdminLoggedIn(session)) {
            return "redirect:/admin/login";
        }

        model.addAttribute(
                "results",
                resultService.getAllResults());

        return "admin-results";
    }

    // Manage students
    @GetMapping("/admin/manage-students")
    public String manageStudents(
            HttpSession session,
            Model model) {

        if (!isAdminLoggedIn(session)) {
            return "redirect:/admin/login";
        }

        model.addAttribute(
                "students",
                studentService.getAllStudents());

        return "manage-students";
    }

    // Delete student using POST
    @PostMapping("/admin/delete-student/{id}")
    public String deleteStudent(
            @PathVariable Long id,
            HttpSession session) {

        if (!isAdminLoggedIn(session)) {
            return "redirect:/admin/login";
        }

        studentService.deleteStudent(id);

        return "redirect:/admin/manage-students";
    }

    // View admin statistics
    @GetMapping("/admin/statistics")
    public String showStatistics(
            HttpSession session,
            Model model) {

        if (!isAdminLoggedIn(session)) {
            return "redirect:/admin/login";
        }

        addStatisticsAttributes(model);

        return "admin-statistics";
    }

    // Calculate and add dashboard statistics
    private void addStatisticsAttributes(Model model) {

        List<Exam> exams = examService.getAllExams();
        List<Result> results = resultService.getAllResults();

        int totalStudents = studentService.getAllStudents().size();
        int totalExams = exams.size();

        int totalQuestions = 0;

        for (Exam exam : exams) {
            totalQuestions += questionService
                    .countQuestionsByExamId(exam.getId());
        }

        int totalSubmissions = results.size();

        int passedSubmissions = 0;
        int failedSubmissions = 0;

        for (Result result : results) {

            if (result.getTotalMarks() > 0) {

                double percentage =
                        (result.getScore() * 100.0)
                                / result.getTotalMarks();

                if (percentage >= 40) {
                    passedSubmissions++;
                } else {
                    failedSubmissions++;
                }

            } else {
                failedSubmissions++;
            }
        }

        double passPercentage = 0;

        if (totalSubmissions > 0) {
            passPercentage =
                    (passedSubmissions * 100.0) / totalSubmissions;
        }

        model.addAttribute("totalStudents", totalStudents);
        model.addAttribute("totalExams", totalExams);
        model.addAttribute("totalQuestions", totalQuestions);
        model.addAttribute("totalSubmissions", totalSubmissions);
        model.addAttribute("passedSubmissions", passedSubmissions);
        model.addAttribute("failedSubmissions", failedSubmissions);
        model.addAttribute("passPercentage", passPercentage);

        model.addAttribute("exams", exams);
        model.addAttribute("results", results);
    }
}
