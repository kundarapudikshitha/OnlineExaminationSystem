
package com.exam.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.exam.entity.Result;

@Repository
public interface ResultRepository extends JpaRepository<Result, Long> {

    // Get results of a particular student
    List<Result> findByStudentId(Long studentId);

    // Get results of a particular exam
    List<Result> findByExamId(Long examId);

}
