
package com.exam.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.exam.entity.Result;
import com.exam.repository.ResultRepository;

@Service
public class ResultService {

    private final ResultRepository resultRepository;

    public ResultService(ResultRepository resultRepository) {
        this.resultRepository = resultRepository;
    }

    // Save examination result
    @Transactional
    public Result saveResult(Result result) {

        if (result == null
                || result.getStudent() == null
                || result.getExam() == null) {

            throw new IllegalArgumentException(
                    "Valid student and exam are required.");
        }

        if (result.getScore() < 0
                || result.getTotalMarks() < 0) {

            throw new IllegalArgumentException(
                    "Marks cannot be negative.");
        }

        if (result.getScore() > result.getTotalMarks()) {

            throw new IllegalArgumentException(
                    "Obtained marks cannot exceed total marks.");
        }

        if (result.getTotalMarks() == 0) {

            throw new IllegalArgumentException(
                    "Total marks must be greater than zero.");
        }

        return resultRepository.saveAndFlush(result);
    }

    // Get results of a particular student
    @Transactional(readOnly = true)
    public List<Result> getResultsByStudentId(Long studentId) {

        if (studentId == null) {
            throw new IllegalArgumentException(
                    "Student ID is required.");
        }

        return resultRepository.findByStudentId(studentId);
    }

    // Get all examination results (Admin)
    @Transactional(readOnly = true)
    public List<Result> getAllResults() {

        return resultRepository.findAll();
    }

    // Get results of a particular exam
    @Transactional(readOnly = true)
    public List<Result> getResultsByExamId(Long examId) {

        if (examId == null) {
            throw new IllegalArgumentException(
                    "Exam ID is required.");
        }

        return resultRepository.findByExamId(examId);
    }

    // Count all examination results
    @Transactional(readOnly = true)
    public long getTotalResultsCount() {

        return resultRepository.count();
    }
}
