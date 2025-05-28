package com.api.gestaoescolar.repositories;

import java.time.LocalTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.api.gestaoescolar.entities.Lesson;
import com.api.gestaoescolar.entities.Subject;
import com.api.gestaoescolar.entities.Teacher;
import com.api.gestaoescolar.entities.Enum.DayOfWeek;

public interface LessonRepository extends JpaRepository<Lesson, Long> {
    List<Lesson> findByTeacher(Teacher teacher);
    
    List<Lesson> findBySubject(Subject subject);
    
    @Query("SELECT l FROM Lesson l WHERE " +
           "l.dayOfWeek = :dayOfWeek AND " +
           "((l.startTime < :endTime AND l.endTime > :startTime)) AND " +
           "(:teacherId IS NULL OR l.teacher.id = :teacherId) AND " +
           "(:excludeId IS NULL OR l.id != :excludeId)")
    List<Lesson> findConflictingLessons(
            @Param("dayOfWeek") DayOfWeek dayOfWeek,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime,
            @Param("teacherId") Long teacherId,
            @Param("excludeId") Long excludeId);
}

