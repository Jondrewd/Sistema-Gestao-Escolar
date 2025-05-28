package com.api.gestaoescolar.mappers;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.api.gestaoescolar.dtos.LessonDTO;
import com.api.gestaoescolar.dtos.SubjectDTO;
import com.api.gestaoescolar.entities.Lesson;
import com.api.gestaoescolar.entities.Subject;

public class LessonMapper {

    public static LessonDTO toDTO(Lesson lesson) {
        if (lesson == null) {
            return null;
        }

        Subject subject = lesson.getSubject();
        SubjectDTO subjectDTO = subject != null
                ? new SubjectDTO(subject.getId(), subject.getName())
                : null;

        return new LessonDTO(
                lesson.getId(),
                subjectDTO,
                lesson.getTeacher() != null ? lesson.getTeacher().getFullName() : null,
                lesson.getDayOfWeek(),
                lesson.getStartTime().toString(),
                lesson.getEndTime().toString()
        );
    }

    public static Lesson toEntity(LessonDTO dto) {
        if (dto == null) {
            return null;
        }

        Lesson lesson = new Lesson();
        lesson.setId(dto.getId());


        lesson.setDayOfWeek(dto.getDayOfWeek());
        lesson.setStartTime(LocalTime.parse(dto.getStartTime()));
        lesson.setEndTime(LocalTime.parse(dto.getEndTime()));

        return lesson;
    }
    public static List<LessonDTO> toDtoList(List<Lesson> lessons) {
        if (lessons == null) {
            return new ArrayList<>();
        }

        return lessons.stream()
                .map(LessonMapper::toDTO)
                .collect(Collectors.toList());
    }

}

