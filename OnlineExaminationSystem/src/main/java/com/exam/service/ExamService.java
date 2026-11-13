
package com.exam.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.exam.entity.Exam;
import com.exam.repository.ExamRepository;

@Service
public class ExamService {

    private final ExamRepository examRepository;

    public ExamService(ExamRepository examRepository) {
        this.examRepository = examRepository;
    }

    // Create a new exam
    @Transactional
    public Exam createExam(Exam exam) {

        if (exam == null) {
            throw new IllegalArgumentException(
                    "Exam details are required.");
        }

        if (exam.getDurationMinutes() <= 0) {
            throw new IllegalArgumentException(
                    "Exam duration must be greater than zero.");
        }

        if (exam.getTotalMarks() < 0) {
            throw new IllegalArgumentException(
                    "Total marks cannot be negative.");
        }

        return examRepository.save(exam);
    }

    // Get all exams
    @Transactional(readOnly = true)
    public List<Exam> getAllExams() {
        return examRepository.findAll();
    }

    // Get exam by ID
    @Transactional(readOnly = true)
    public Exam getExamById(Long id) {

        if (id == null) {
            return null;
        }

        return examRepository.findById(id).orElse(null);
    }

    // Update an exam
    @Transactional
    public Exam updateExam(Long id, Exam updatedExam) {

        if (id == null || updatedExam == null) {
            throw new IllegalArgumentException(
                    "Valid exam ID and exam details are required.");
        }

        if (updatedExam.getDurationMinutes() <= 0) {
            throw new IllegalArgumentException(
                    "Exam duration must be greater than zero.");
        }

        if (updatedExam.getTotalMarks() < 0) {
            throw new IllegalArgumentException(
                    "Total marks cannot be negative.");
        }

        Exam existingExam =
                examRepository.findById(id).orElse(null);

        if (existingExam == null) {
            return null;
        }

        existingExam.setTitle(updatedExam.getTitle());
        existingExam.setDescription(updatedExam.getDescription());
        existingExam.setDurationMinutes(
                updatedExam.getDurationMinutes());
        existingExam.setTotalMarks(updatedExam.getTotalMarks());

        return examRepository.save(existingExam);
    }

    // Delete an exam
    @Transactional
    public void deleteExam(Long id) {

        if (id == null) {
            throw new IllegalArgumentException(
                    "Exam ID is required.");
        }

        if (examRepository.existsById(id)) {
            examRepository.deleteById(id);
        }
    }
}
