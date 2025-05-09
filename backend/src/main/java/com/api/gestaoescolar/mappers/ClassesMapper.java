package com.api.gestaoescolar.mappers;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.api.gestaoescolar.dtos.ClassesDTO;
import com.api.gestaoescolar.entities.Classes;
import com.api.gestaoescolar.entities.Student;

public class ClassesMapper {

    private ClassesMapper() {}

    public static ClassesDTO toDto(Classes classes) {
        if (classes == null) {
            return null;
        }

        ClassesDTO dto = new ClassesDTO();
        dto.setId(classes.getId());
        dto.setName(classes.getName());
        dto.setCourse(classes.getCourse().getId());
        if (classes.getTeacher() != null) {
            dto.setTeacher(classes.getTeacher().getUsername());
        }

        if (classes.getStudents() != null) {
            dto.setStudents(classes.getStudents().stream()
                    .map(Student::getUsername)
                    .collect(Collectors.toList()));
        } else {
            dto.setStudents(Collections.emptyList());
        }

        if (classes.getAttendances() != null) {
            dto.setAttendances(AttendanceMapper.toDtoList(classes.getAttendances()));
        } else {
            dto.setAttendances(Collections.emptyList());
        }

        return dto;
    }

    public static Classes toEntity(ClassesDTO dto) {
        if (dto == null) {
            return null;
        }

        Classes classes = new Classes();
        classes.setId(dto.getId());
        classes.setName(dto.getName());
        return classes;
    }

    public static List<ClassesDTO> toDtoList(List<Classes> classes) {
        if (classes == null) {
            return Collections.emptyList();
        }

        return classes.stream()
                .map(ClassesMapper::toDto)
                .collect(Collectors.toList());
    }
}
