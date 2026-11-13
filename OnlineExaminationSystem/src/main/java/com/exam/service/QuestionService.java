
package com.exam.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.exam.entity.Question;
import com.exam.repository.QuestionRepository;

@Service
public class QuestionService {

    private final QuestionRepository questionRepository;

    public QuestionService(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    // Add a new question
    @Transactional
    public Question addQuestion(Question question) {

        if (question == null || question.getExam() == null) {
            throw new IllegalArgumentException(
                    "Valid question and exam are required.");
        }

        return questionRepository.save(question);
    }

    // Get all questions belonging to an exam
    @Transactional(readOnly = true)
    public List<Question> getQuestionsByExamId(Long examId) {

        if (examId == null) {
            throw new IllegalArgumentException("Exam ID is required.");
        }

        return questionRepository.findByExamId(examId);
    }

    // Count questions belonging to an exam
    @Transactional(readOnly = true)
    public long countQuestionsByExamId(Long examId) {

        if (examId == null) {
            throw new IllegalArgumentException("Exam ID is required.");
        }

        return questionRepository.countByExamId(examId);
    }

    // Get question by ID
    @Transactional(readOnly = true)
    public Question getQuestionById(Long id) {

        if (id == null) {
            return null;
        }

        return questionRepository.findById(id).orElse(null);
    }

    // Update question
    @Transactional
    public Question updateQuestion(Long id, Question updatedQuestion) {

        if (id == null || updatedQuestion == null) {
            throw new IllegalArgumentException(
                    "Valid question ID and question are required.");
        }

        Question existingQuestion =
                questionRepository.findById(id).orElse(null);

        if (existingQuestion == null) {
            return null;
        }

        existingQuestion.setQuestionText(
                updatedQuestion.getQuestionText());

        existingQuestion.setOptionA(updatedQuestion.getOptionA());
        existingQuestion.setOptionB(updatedQuestion.getOptionB());
        existingQuestion.setOptionC(updatedQuestion.getOptionC());
        existingQuestion.setOptionD(updatedQuestion.getOptionD());

        existingQuestion.setCorrectAnswer(
                updatedQuestion.getCorrectAnswer());

        return questionRepository.save(existingQuestion);
    }

    // Delete question
    @Transactional
    public void deleteQuestion(Long id) {

        if (id == null) {
            throw new IllegalArgumentException("Question ID is required.");
        }

        if (questionRepository.existsById(id)) {
            questionRepository.deleteById(id);
        }
    }
}
