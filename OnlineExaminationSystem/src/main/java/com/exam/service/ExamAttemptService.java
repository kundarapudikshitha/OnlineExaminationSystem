
package com.exam.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.exam.entity.Exam;
import com.exam.entity.ExamAttempt;
import com.exam.entity.Student;
import com.exam.repository.ExamAttemptRepository;

@Service
public class ExamAttemptService {

    private final ExamAttemptRepository attemptRepository;

    public ExamAttemptService(ExamAttemptRepository attemptRepository) {
        this.attemptRepository = attemptRepository;
    }

    // Start a new attempt or return the existing attempt
    @Transactional
    public ExamAttempt startExam(Student student, Exam exam) {

        if (student == null || exam == null
                || student.getId() == null || exam.getId() == null) {

            throw new IllegalArgumentException(
                    "Valid student and exam are required.");
        }

        if (exam.getDurationMinutes() <= 0) {
            throw new IllegalArgumentException(
                    "Exam duration must be greater than zero.");
        }

        // Check whether an attempt already exists
        Optional<ExamAttempt> existingAttempt =
                attemptRepository.findByStudentIdAndExamId(
                        student.getId(), exam.getId());

        if (existingAttempt.isPresent()) {
            return existingAttempt.get();
        }

        // Create a new attempt
        LocalDateTime startTime = LocalDateTime.now();

        ExamAttempt attempt = new ExamAttempt();
        attempt.setStudent(student);
        attempt.setExam(exam);
        attempt.setStartTime(startTime);
        attempt.setEndTime(
                startTime.plusMinutes(exam.getDurationMinutes()));
        attempt.setSubmitted(false);

        return attemptRepository.saveAndFlush(attempt);
    }

    // Get and lock an existing attempt
    @Transactional
    public ExamAttempt getAttempt(Long studentId, Long examId) {

        if (studentId == null || examId == null) {
            return null;
        }

        return attemptRepository
                .findByStudentIdAndExamIdForUpdate(studentId, examId)
                .orElse(null);
    }

    // Calculate remaining examination time
    public long getRemainingSeconds(ExamAttempt attempt) {

        if (attempt == null
                || attempt.isSubmitted()
                || attempt.getEndTime() == null) {

            return 0;
        }

        long remainingSeconds = Duration.between(
                LocalDateTime.now(),
                attempt.getEndTime()).getSeconds();

        return Math.max(remainingSeconds, 0);
    }

    // Mark an attempt as submitted only once
    @Transactional
    public void submitAttempt(Long studentId, Long examId) {

        if (studentId == null || examId == null) {
            return;
        }

        ExamAttempt attempt = attemptRepository
                .findByStudentIdAndExamIdForUpdate(studentId, examId)
                .orElse(null);

        if (attempt == null || attempt.isSubmitted()) {
            return;
        }

        attempt.setSubmitted(true);
        attempt.setSubmittedAt(LocalDateTime.now());

        attemptRepository.save(attempt);
    }
}
