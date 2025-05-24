package com.api.gestaoescolar.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.api.gestaoescolar.entities.Attendance;

import jakarta.transaction.Transactional;

public interface AttendanceRepository extends JpaRepository<Attendance, Long>{

    @Transactional
    @Modifying
    @Query("DELETE FROM Attendance a WHERE a.subject.id = :subjectId")
    void deleteBySubjectId(@Param("subjectId") Long subjectId);
    
    @Transactional
    @Modifying
    void deleteAllBySubjectId(Long subjsctId); 


    Page<Attendance> findByStudentCpf(String cpf, Pageable pageable);

}
