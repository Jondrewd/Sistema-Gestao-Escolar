package com.api.gestaoescolar.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.api.gestaoescolar.entities.Subject;


public interface SubjectRepository extends JpaRepository<Subject, Long>{

}
