package com.api.gestaoescolar.mappers;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.api.gestaoescolar.dtos.ClassesDTO;
import com.api.gestaoescolar.entities.Classes;
import com.api.gestaoescolar.entities.Student;
import com.api.gestaoescolar.entities.Subject;

public class ClassesMapper {

    private ClassesMapper() {}

    public static ClassesDTO toDto(Classes classes) {
        if (classes == null) {
            return null;
        }

        ClassesDTO dto = new ClassesDTO();
        dto.setId(classes.getId());
        dto.setName(classes.getName());

        
        if (classes.getSubjects() != null) {
            dto.setSubjectIds(classes.getSubjects().stream()
                    .map(Subject::getId)
                    .collect(Collectors.toList()));
        }

        if (classes.getStudents() != null) {
            dto.setStudentCpfs(classes.getStudents().stream()
                    .map(Student::getCpf)
                    .collect(Collectors.toList()));
        } else {
            dto.setStudentCpfs(Collections.emptyList());
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

    public static List<ClassesDTO> toDtoList(List<Classes> classesList) {
        if (classesList == null) {
            return Collections.emptyList();
        }

        return classesList.stream()
                .map(ClassesMapper::toDto)
                .collect(Collectors.toList());
    }
}