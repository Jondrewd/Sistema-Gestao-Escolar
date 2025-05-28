package com.api.gestaoescolar.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.api.gestaoescolar.entities.Classes;


public interface ClassesRepository extends JpaRepository<Classes, Long>{

    @EntityGraph(attributePaths = {"lessons", "students"})
    @Query("SELECT c FROM Classes c WHERE c.id = :id")
    Optional<Classes> findByIdWithRelations(@Param("id") Long id);

    @EntityGraph(attributePaths = {"students", "lessons"})
    @Query("SELECT c FROM Classes c")
    Page<Classes> findAllWithRelations(Pageable pageable);

    @Query("SELECT DISTINCT c FROM Classes c " +
        "JOIN FETCH c.lessons " +
        "LEFT JOIN FETCH c.students")
    List<Classes> findAllWithRelationsDetailed();

}
