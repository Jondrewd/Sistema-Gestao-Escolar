package com.api.gestaoescolar.repositories;

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
}
