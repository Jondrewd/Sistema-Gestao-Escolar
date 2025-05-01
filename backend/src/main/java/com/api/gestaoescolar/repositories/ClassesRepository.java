package com.api.gestaoescolar.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.api.gestaoescolar.entities.Classes;


public interface ClassesRepository extends JpaRepository<Classes, Long>{

    @EntityGraph(attributePaths = {"teacher", "course", "students", "attendances"})
    @Query("SELECT c FROM Classes c WHERE c.id = :id")
    Optional<Classes> findByIdWithRelations(@Param("id") Long id);

    @EntityGraph(attributePaths = {"teacher", "course"})
    @Query("SELECT c FROM Classes c")
    List<Classes> findAllWithRelations();

    @Query("SELECT DISTINCT c FROM Classes c " +
           "LEFT JOIN FETCH c.teacher " +
           "LEFT JOIN FETCH c.course " +
           "LEFT JOIN FETCH c.students")
    List<Classes> findAllWithRelationsDetailed();
}
