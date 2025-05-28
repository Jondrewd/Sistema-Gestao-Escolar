package com.api.gestaoescolar.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.api.gestaoescolar.entities.Evaluation;
import com.api.gestaoescolar.entities.Grade;
import com.api.gestaoescolar.entities.Student;

public interface GradeRepository extends JpaRepository<Grade, Long>  {
      
    List<Grade> findByEvaluation(Evaluation evaluation);
    
    List<Grade> findByStudent(Student student);
    
    @Modifying
    @Query("DELETE FROM Grade g WHERE g.evaluation = :evaluation")
    void deleteByEvaluation(@Param("evaluation") Evaluation evaluation);
}
