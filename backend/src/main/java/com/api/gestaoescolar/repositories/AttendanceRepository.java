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
    @Query("DELETE FROM Attendance a WHERE a.classes.id = :classesId")
    void deleteByClassesId(@Param("classesId") Long classesId);
    
    @Transactional
    @Modifying
    void deleteAllByClassesId(Long classesId); 

    @Query("SELECT new com.api.gestaoescolar.dtos.AttendanceDTO(a.id, a.date, a.status, a.className) " +
       "FROM Attendance a " +
       "JOIN a.student s " +
       "WHERE s.cpf = :cpf")
    Page<Attendance> findAttendanceByStudent(@Param("cpf") String cpf, Pageable pageable);

}
