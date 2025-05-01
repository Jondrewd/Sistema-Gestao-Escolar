package com.api.gestaoescolar.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.api.gestaoescolar.dtos.CourseDTO;
import com.api.gestaoescolar.entities.Course;
import com.api.gestaoescolar.exceptions.ResourceNotFoundException;
import com.api.gestaoescolar.mappers.CourseMapper;
import com.api.gestaoescolar.repositories.CourseRepository;

@Service
@Transactional
public class CourseService {

    private final CourseRepository courseRepository;

    public CourseService(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    @Transactional(readOnly = true)
    public CourseDTO findById(Long id) {
        Course course = courseRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Curso não encontrado com ID: " + id));
        return CourseMapper.toDto(course);
    }

    @Transactional(readOnly = true)
    public Page<CourseDTO> findAll(Pageable pageable) {
        return courseRepository.findAll(pageable)
            .map(CourseMapper::toDto);
    }

    @Transactional
    public CourseDTO create(CourseDTO courseDTO) {
        Course course = CourseMapper.toEntity(courseDTO);
        Course savedCourse = courseRepository.save(course);
        return CourseMapper.toDto(savedCourse);
    }

    @Transactional
    public CourseDTO update(Long id, CourseDTO courseDTO) {
        Course existingCourse = courseRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Curso não encontrado com ID: " + id));

        if (courseDTO.getName() != null) {
            existingCourse.setName(courseDTO.getName());
        }
        if (courseDTO.getDescription() != null) {
            existingCourse.setDescription(courseDTO.getDescription());
        }

        Course updatedCourse = courseRepository.save(existingCourse);
        return CourseMapper.toDto(updatedCourse);
    }

    @Transactional
    public void delete(Long id) {
        if (!courseRepository.existsById(id)) {
            throw new ResourceNotFoundException("Curso não encontrado com ID: " + id);
        }
        courseRepository.deleteById(id);
    }
}