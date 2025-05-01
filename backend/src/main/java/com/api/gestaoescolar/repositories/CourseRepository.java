package com.api.gestaoescolar.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.api.gestaoescolar.entities.Course;


public interface CourseRepository extends JpaRepository<Course, Long>{

}
