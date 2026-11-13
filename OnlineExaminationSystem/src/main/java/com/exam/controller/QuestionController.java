
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

import com.exam.entity.Question;
import com.exam.entity.Exam;
import com.exam.service.QuestionService;
import com.exam.service.ExamService;

@Controller
public class QuestionController {

    private final QuestionService questionService;
    private final ExamService examService;

    public QuestionController(
            QuestionService questionService,
            ExamService examService) {

        this.questionService = questionService;
        this.examService = examService;
    }

    @GetMapping("/admin/add-question/{examId}")
    public String showAddQuestion(
            @PathVariable Long examId,
            HttpSession session,
            Model model) {

        if (session.getAttribute("loggedInAdmin") == null) {
            return "redirect:/admin/login";
        }

        Exam exam = examService.getExamById(examId);

        if (exam == null) {
            return "redirect:/admin/manage-exams";
        }

        Question question = new Question();
        question.setExam(exam);

        model.addAttribute("question", question);
        model.addAttribute("exam", exam);

        return "add-question";
    }

    @PostMapping("/admin/add-question/{examId}")
    public String addQuestion(
            @PathVariable Long examId,
            @Valid @ModelAttribute("question") Question question,
            BindingResult bindingResult,
            HttpSession session,
            Model model) {

        if (session.getAttribute("loggedInAdmin") == null) {
            return "redirect:/admin/login";
        }

        Exam exam = examService.getExamById(examId);

        if (exam == null) {
            return "redirect:/admin/manage-exams";
        }

        question.setExam(exam);

        if (bindingResult.hasErrors()) {
            model.addAttribute("exam", exam);
            return "add-question";
        }

        questionService.addQuestion(question);

        return "redirect:/admin/manage-questions/" + examId;
    }

    @GetMapping("/admin/manage-questions/{examId}")
    public String manageQuestions(
            @PathVariable Long examId,
            HttpSession session,
            Model model) {

        if (session.getAttribute("loggedInAdmin") == null) {
            return "redirect:/admin/login";
        }

        Exam exam = examService.getExamById(examId);

        if (exam == null) {
            return "redirect:/admin/manage-exams";
        }

        model.addAttribute("exam", exam);
        model.addAttribute(
                "questions",
                questionService.getQuestionsByExamId(examId));

        return "manage-questions";
    }

    @GetMapping("/admin/edit-question/{examId}/{questionId}")
    public String showEditQuestion(
            @PathVariable Long examId,
            @PathVariable Long questionId,
            HttpSession session,
            Model model) {

        if (session.getAttribute("loggedInAdmin") == null) {
            return "redirect:/admin/login";
        }

        Exam exam = examService.getExamById(examId);
        Question question =
                questionService.getQuestionById(questionId);

        if (exam == null || question == null ||
                question.getExam() == null ||
                !question.getExam().getId().equals(examId)) {

            return "redirect:/admin/manage-exams";
        }

        model.addAttribute("exam", exam);
        model.addAttribute("question", question);

        return "edit-question";
    }

    @PostMapping("/admin/edit-question/{examId}/{questionId}")
    public String updateQuestion(
            @PathVariable Long examId,
            @PathVariable Long questionId,
            @Valid @ModelAttribute("question") Question updatedQuestion,
            BindingResult bindingResult,
            HttpSession session,
            Model model) {

        if (session.getAttribute("loggedInAdmin") == null) {
            return "redirect:/admin/login";
        }

        Exam exam = examService.getExamById(examId);
        Question existingQuestion =
                questionService.getQuestionById(questionId);

        if (exam == null || existingQuestion == null ||
                existingQuestion.getExam() == null ||
                !existingQuestion.getExam().getId().equals(examId)) {

            return "redirect:/admin/manage-exams";
        }

        updatedQuestion.setExam(exam);

        if (bindingResult.hasErrors()) {
            model.addAttribute("exam", exam);
            return "edit-question";
        }

        questionService.updateQuestion(questionId, updatedQuestion);

        return "redirect:/admin/manage-questions/" + examId;
    }

    // Delete question using POST
    @PostMapping("/admin/delete-question/{examId}/{questionId}")
    public String deleteQuestion(
            @PathVariable Long examId,
            @PathVariable Long questionId,
            HttpSession session) {

        if (session.getAttribute("loggedInAdmin") == null) {
            return "redirect:/admin/login";
        }

        Question question =
                questionService.getQuestionById(questionId);

        if (question == null ||
                question.getExam() == null ||
                !question.getExam().getId().equals(examId)) {

            return "redirect:/admin/manage-exams";
        }

        questionService.deleteQuestion(questionId);

        return "redirect:/admin/manage-questions/" + examId;
    }
}
