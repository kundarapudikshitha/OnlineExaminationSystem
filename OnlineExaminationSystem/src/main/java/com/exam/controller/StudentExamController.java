
package com.exam.controller;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.exam.entity.Exam;
import com.exam.entity.Question;
import com.exam.entity.Result;
import com.exam.entity.Student;
import com.exam.entity.ExamAttempt;

import com.exam.service.ExamService;
import com.exam.service.QuestionService;
import com.exam.service.ResultService;
import com.exam.service.ExamAttemptService;

@Controller
public class StudentExamController {

    private final ExamService examService;
    private final QuestionService questionService;
    private final ResultService resultService;
    private final ExamAttemptService examAttemptService;

    public StudentExamController(
            ExamService examService,
            QuestionService questionService,
            ResultService resultService,
            ExamAttemptService examAttemptService) {

        this.examService = examService;
        this.questionService = questionService;
        this.resultService = resultService;
        this.examAttemptService = examAttemptService;
    }

    // Start or resume examination
    @GetMapping("/start-exam/{examId}")
    public String startExam(
            @PathVariable Long examId,
            HttpSession session,
            Model model) {

        Student student =
                (Student) session.getAttribute("loggedInStudent");

        if (student == null) {
            return "redirect:/login";
        }

        Exam exam = examService.getExamById(examId);

        if (exam == null || exam.getDurationMinutes() <= 0) {
            return "redirect:/available-exams";
        }

        ExamAttempt attempt =
                examAttemptService.startExam(student, exam);

        if (attempt.isSubmitted()) {
            return "redirect:/my-results";
        }

        long remainingSeconds =
                examAttemptService.getRemainingSeconds(attempt);

        if (remainingSeconds <= 0) {
            return "redirect:/submit-exam-expired/" + examId;
        }

        List<Question> questions =
                questionService.getQuestionsByExamId(examId);

        model.addAttribute("exam", exam);
        model.addAttribute("questions", questions);
        model.addAttribute("remainingSeconds", remainingSeconds);

        return "start-exam";
    }

    // Submit examination
    @Transactional
    @PostMapping("/submit-exam")
    public String submitExam(
            @RequestParam Long examId,
            @RequestParam Map<String, String> answers,
            HttpSession session,
            Model model) {

        Student student =
                (Student) session.getAttribute("loggedInStudent");

        if (student == null) {
            return "redirect:/login";
        }

        Exam exam = examService.getExamById(examId);

        if (exam == null) {
            return "redirect:/available-exams";
        }

        // Lock the attempt within this transaction
        ExamAttempt attempt =
                examAttemptService.getAttempt(
                        student.getId(), examId);

        if (attempt == null || attempt.isSubmitted()) {
            return "redirect:/my-results";
        }

        boolean timeExpired =
                examAttemptService.getRemainingSeconds(attempt) <= 0;

        List<Question> questions =
                questionService.getQuestionsByExamId(examId);

        int correctAnswers = 0;

        // Evaluate answers only if the exam has not expired
        if (!timeExpired) {

            for (Question question : questions) {

                String selectedAnswer =
                        answers.get("answers[" + question.getId() + "]");

                String correctAnswer =
                        question.getCorrectAnswer();

                if (selectedAnswer == null || correctAnswer == null) {
                    continue;
                }

                selectedAnswer = selectedAnswer.trim()
                        .toUpperCase(Locale.ROOT);

                correctAnswer = correctAnswer.trim()
                        .toUpperCase(Locale.ROOT);

                if (selectedAnswer.matches("[A-D]")
                        && selectedAnswer.equals(correctAnswer)) {

                    correctAnswers++;
                }
            }
        }

        // Calculate score
        int score = 0;

        if (!timeExpired
                && !questions.isEmpty()
                && exam.getTotalMarks() > 0) {

            score = Math.round(
                    (correctAnswers * (float) exam.getTotalMarks())
                            / questions.size());
        }

        // Calculate percentage
        double percentage = 0;

        if (exam.getTotalMarks() > 0) {
            percentage =
                    (score * 100.0) / exam.getTotalMarks();
        }

        String status = percentage >= 40 ? "Pass" : "Fail";

        // Save result
        Result result = new Result();

        result.setStudent(student);
        result.setExam(exam);
        result.setScore(score);
        result.setTotalMarks(exam.getTotalMarks());

        resultService.saveResult(result);

        // Mark attempt submitted using student and exam IDs
        examAttemptService.submitAttempt(
                student.getId(), examId);

        if (timeExpired) {
            return "redirect:/my-results";
        }

        model.addAttribute("exam", exam);
        model.addAttribute("score", score);
        model.addAttribute("totalMarks", exam.getTotalMarks());
        model.addAttribute("percentage", percentage);
        model.addAttribute("status", status);

        return "exam-result";
    }

    // Handle an expired examination
    @Transactional
    @GetMapping("/submit-exam-expired/{examId}")
    public String submitExpiredExam(
            @PathVariable Long examId,
            HttpSession session) {

        Student student =
                (Student) session.getAttribute("loggedInStudent");

        if (student == null) {
            return "redirect:/login";
        }

        Exam exam = examService.getExamById(examId);

        if (exam == null) {
            return "redirect:/available-exams";
        }

        // Lock the attempt within this transaction
        ExamAttempt attempt =
                examAttemptService.getAttempt(
                        student.getId(), examId);

        if (attempt == null || attempt.isSubmitted()) {
            return "redirect:/my-results";
        }

        // If time remains, return to examination
        if (examAttemptService.getRemainingSeconds(attempt) > 0) {
            return "redirect:/start-exam/" + examId;
        }

        // Expired exams receive zero marks
        Result result = new Result();

        result.setStudent(student);
        result.setExam(exam);
        result.setScore(0);
        result.setTotalMarks(exam.getTotalMarks());

        resultService.saveResult(result);

        // Mark attempt submitted
        examAttemptService.submitAttempt(
                student.getId(), examId);

        return "redirect:/my-results";
    }

    // View student results
    @GetMapping("/my-results")
    public String myResults(
            HttpSession session,
            Model model) {

        Student student =
                (Student) session.getAttribute("loggedInStudent");

        if (student == null) {
            return "redirect:/login";
        }

        model.addAttribute(
                "results",
                resultService.getResultsByStudentId(student.getId()));

        return "my-results";
    }

    // Display examination instructions
    @GetMapping("/exam-instructions/{examId}")
    public String showExamInstructions(
            @PathVariable Long examId,
            HttpSession session,
            Model model) {

        if (session.getAttribute("loggedInStudent") == null) {
            return "redirect:/login";
        }

        Exam exam = examService.getExamById(examId);

        if (exam == null) {
            return "redirect:/available-exams";
        }

        model.addAttribute("exam", exam);

        model.addAttribute(
                "questions",
                questionService.getQuestionsByExamId(examId));

        return "exam-instructions";
    }
}
