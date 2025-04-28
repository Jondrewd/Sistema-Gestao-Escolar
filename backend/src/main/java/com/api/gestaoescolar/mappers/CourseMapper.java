package com.api.gestaoescolar.mappers;

import java.util.List;
import java.util.stream.Collectors;

import com.api.gestaoescolar.dtos.CourseDTO;
import com.api.gestaoescolar.entities.Course;

public class CourseMapper {

    private CourseMapper() {}

    public static CourseDTO toDto(Course course) {
        if (course == null) {
            return null;
        }

        CourseDTO courseDto = new CourseDTO();
        courseDto.setId(course.getId());
        courseDto.setName(course.getName());
        courseDto.setDescription(course.getDescription());

        if (course.getGroups() != null) {
            courseDto.setGroup(GroupMapper.toDtoList(course.getGroups()));
        }

        if (course.getEvaluations() != null) {
            courseDto.setEvaluations(EvaluationMapper.toDtoList(course.getEvaluations()));
        }
        
        return courseDto;
    }

    public static Course toEntity(CourseDTO courseDto) {
        if (courseDto == null) {
            return null;
        }

        Course course = new Course();
        course.setId(courseDto.getId());
        course.setName(courseDto.getName());
        course.setDescription(courseDto.getDescription());

        // Nota: converter group e evaluations no service
        
        return course;
    }

    public static List<CourseDTO> toDtoList(List<Course> courses) {
        if (courses == null) {
            return List.of();
        }

        return courses.stream()
                .map(CourseMapper::toDto)
                .collect(Collectors.toList());
    }
}
