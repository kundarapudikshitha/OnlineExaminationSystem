
package com.exam.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.exam.entity.Exam;
import com.exam.service.ExamService;

@Controller
public class ExamController {

    private final ExamService examService;

    public ExamController(ExamService examService) {
        this.examService = examService;
    }

    @GetMapping("/admin/create-exam")
    public String showCreateExam(
            HttpSession session, Model model) {

        if (session.getAttribute("loggedInAdmin") == null) {
            return "redirect:/admin/login";
        }

        model.addAttribute("exam", new Exam());
        return "create-exam";
    }

    @PostMapping("/admin/create-exam")
    public String createExam(
            @Valid @ModelAttribute("exam") Exam exam,
            BindingResult bindingResult,
            HttpSession session) {

        if (session.getAttribute("loggedInAdmin") == null) {
            return "redirect:/admin/login";
        }

        if (bindingResult.hasErrors()) {
            return "create-exam";
        }

        examService.createExam(exam);

        return "redirect:/admin/manage-exams";
    }

    @GetMapping("/admin/manage-exams")
    public String manageExams(
            HttpSession session, Model model) {

        if (session.getAttribute("loggedInAdmin") == null) {
            return "redirect:/admin/login";
        }

        model.addAttribute("exams", examService.getAllExams());

        return "manage-exams";
    }

    @GetMapping("/admin/edit-exam/{id}")
    public String showEditExam(
            @PathVariable Long id,
            HttpSession session,
            Model model) {

        if (session.getAttribute("loggedInAdmin") == null) {
            return "redirect:/admin/login";
        }

        Exam exam = examService.getExamById(id);

        if (exam == null) {
            return "redirect:/admin/manage-exams";
        }

        model.addAttribute("exam", exam);

        return "edit-exam";
    }

    @PostMapping("/admin/edit-exam/{id}")
    public String updateExam(
            @PathVariable Long id,
            @Valid @ModelAttribute("exam") Exam exam,
            BindingResult bindingResult,
            HttpSession session) {

        if (session.getAttribute("loggedInAdmin") == null) {
            return "redirect:/admin/login";
        }

        if (bindingResult.hasErrors()) {
            return "edit-exam";
        }

        examService.updateExam(id, exam);

        return "redirect:/admin/manage-exams";
    }

    // Delete exam using POST
    @PostMapping("/admin/delete-exam/{id}")
    public String deleteExam(
            @PathVariable Long id,
            HttpSession session) {

        if (session.getAttribute("loggedInAdmin") == null) {
            return "redirect:/admin/login";
        }

        examService.deleteExam(id);

        return "redirect:/admin/manage-exams";
    }
}
