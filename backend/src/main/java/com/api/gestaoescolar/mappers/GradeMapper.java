package com.api.gestaoescolar.mappers;

import java.util.ArrayList;
import java.util.List;

import com.api.gestaoescolar.dtos.EvaluationDTO;
import com.api.gestaoescolar.dtos.GradeDTO;
import com.api.gestaoescolar.entities.Grade;

public class GradeMapper {

    public static GradeDTO toDTO(Grade grade) {
        if (grade == null) return null;

        EvaluationDTO evaluationDTO = EvaluationMapper.toDto(grade.getEvaluation());

        String studentName = grade.getStudent() != null
                ? grade.getStudent().getCpf()
                : null;

        return new GradeDTO(
                grade.getId(),
                evaluationDTO,
                studentName,
                grade.getScore()
        );
    }

    public static List<GradeDTO> toDtoList(List<Grade> grades) {
        if (grades == null) return new ArrayList<>();
        return grades.stream()
                .map(GradeMapper::toDTO)
                .toList();
    }

    // ⚠️ Requer EvaluationService e StudentService injetados em quem usar
    public static Grade toEntity(
            GradeDTO dto
    ) {
        if (dto == null) return null;

        Grade grade = new Grade();
        grade.setId(dto.getId());
        grade.setScore(dto.getScore());

        return grade;
    }
}
