package com.api.gestaoescolar.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.api.gestaoescolar.entities.Evaluation;


public interface EvaluationRepository extends JpaRepository<Evaluation, Long>{

}
