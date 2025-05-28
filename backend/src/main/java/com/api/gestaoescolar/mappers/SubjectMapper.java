package com.api.gestaoescolar.mappers;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.api.gestaoescolar.dtos.SubjectDTO;
import com.api.gestaoescolar.entities.Subject;

public class SubjectMapper {

    private SubjectMapper() {}

    public static SubjectDTO toDto(Subject subject) {
        if (subject == null) return null;

        SubjectDTO subjectDto = new SubjectDTO();
        subjectDto.setId(subject.getId());
        subjectDto.setName(subject.getName());

        return subjectDto;
    }

    public static Subject toEntity(SubjectDTO subjectDto) {
        if (subjectDto == null) return null;

        Subject subject = new Subject();
        subject.setId(subjectDto.getId());
        subject.setName(subjectDto.getName());
        return subject;
    }

    public static List<SubjectDTO> toDtoList(List<Subject> subject) {
        if (subject == null || subject.isEmpty()) return Collections.emptyList();

        return subject.stream()
            .map(SubjectMapper::toDto)
            .collect(Collectors.toList());
    }
}
