package com.api.gestaoescolar.mappers;

import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
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

        if (subject.getTeacher() != null) {
            subjectDto.setTeacher(subject.getTeacher().getFullName()); 
            subjectDto.setTeacherId(subject.getTeacher().getId());
        }

        subjectDto.setDayOfWeek(subject.getDayOfWeek());
        if (subject.getStartTime() != null) {
            subjectDto.setStartTime(subject.getStartTime().toString());
        }
        if (subject.getEndTime() != null) {
            subjectDto.setEndTime(subject.getEndTime().toString());
        }

        if (subject.getClasses() != null) {
            subjectDto.setClassId(subject.getClasses().getId());
        }

        subjectDto.setEvaluations(
            Optional.ofNullable(subject.getEvaluations())
                    .orElse(Collections.emptyList())
                    .stream()
                    .map(EvaluationMapper::toDto)
                    .collect(Collectors.toList())
        );

        return subjectDto;
    }

    public static Subject toEntity(SubjectDTO subjectDto) {
        if (subjectDto == null) return null;

        Subject subject = new Subject();
        subject.setId(subjectDto.getId());
        subject.setName(subjectDto.getName());
        subject.setDayOfWeek(subjectDto.getDayOfWeek());
        if (subjectDto.getStartTime() != null && !subjectDto.getStartTime().isEmpty()) {
            try {
                subject.setStartTime(LocalTime.parse(subjectDto.getStartTime()));
            } catch (DateTimeParseException e) {
                throw new IllegalArgumentException("Formato de horário inicial inválido. Use HH:mm");
            }
        }
    
         if (subjectDto.getEndTime() != null && !subjectDto.getEndTime().isEmpty()) {
            try {
                subject.setEndTime(LocalTime.parse(subjectDto.getEndTime()));
            } catch (DateTimeParseException e) {
                throw new IllegalArgumentException("Formato de horário inicial inválido. Use HH:mm");
        }
    }
        return subject;
    }

    public static List<SubjectDTO> toDtoList(List<Subject> subject) {
        if (subject == null || subject.isEmpty()) return Collections.emptyList();

        return subject.stream()
            .map(SubjectMapper::toDto)
            .collect(Collectors.toList());
    }
}
