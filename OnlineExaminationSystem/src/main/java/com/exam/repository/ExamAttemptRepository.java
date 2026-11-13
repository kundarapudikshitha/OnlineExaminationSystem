
package com.exam.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.persistence.LockModeType;

import com.exam.entity.ExamAttempt;

@Repository
public interface ExamAttemptRepository
        extends JpaRepository<ExamAttempt, Long> {

    // Find an existing attempt
    Optional<ExamAttempt> findByStudentIdAndExamId(
            Long studentId, Long examId);

    // Lock the attempt while submission is being processed
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT a FROM ExamAttempt a " +
           "WHERE a.student.id = :studentId " +
           "AND a.exam.id = :examId")
    Optional<ExamAttempt> findByStudentIdAndExamIdForUpdate(
            @Param("studentId") Long studentId,
            @Param("examId") Long examId);
}
