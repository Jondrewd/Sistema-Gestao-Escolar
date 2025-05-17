package com.api.gestaoescolar.mappers;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.api.gestaoescolar.dtos.EvaluationDTO;
import com.api.gestaoescolar.entities.Evaluation;

public class EvaluationMapper {

    private EvaluationMapper() {}

    public static EvaluationDTO toDto(Evaluation evaluation) {
        if (evaluation == null) {
            return null;
        }

        EvaluationDTO dto = new EvaluationDTO();
        dto.setId(evaluation.getId());
        dto.setDate(evaluation.getDate());
        dto.setScore(evaluation.getScore());

        if (evaluation.getStudent() != null) {
            dto.setStudent(evaluation.getStudent().getCpf());
        }

        if (evaluation.getCourse() != null) {
            dto.setCourse(CourseMapper.toDto(evaluation.getCourse()));
        }

        return dto;
    }

    public static Evaluation toEntity(EvaluationDTO dto) {
        if (dto == null) {
            return null;
        }

        Evaluation entity = new Evaluation();
        entity.setId(dto.getId());
        entity.setDate(dto.getDate());
        entity.setScore(dto.getScore());

        return entity;
    }

    public static List<EvaluationDTO> toDtoList(List<Evaluation> evaluations) {
        if (evaluations == null) {
            return Collections.emptyList();
        }
        return evaluations.stream()
                .map(EvaluationMapper::toDto)
                .collect(Collectors.toList());
    }
}
