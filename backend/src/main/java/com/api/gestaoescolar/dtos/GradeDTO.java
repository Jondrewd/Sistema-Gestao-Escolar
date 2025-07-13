package com.api.gestaoescolar.dtos;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public class GradeDTO {

    private Long id;

    @NotNull(message = "A avaliação (evaluation) é obrigatória")
    @Valid
    private EvaluationDTO evaluation;

    @NotNull(message = "O nome do estudante é obrigatório")
    private String student;

    @NotNull(message = "A nota (score) é obrigatória")
    @DecimalMin(value = "0.0", inclusive = true, message = "A nota mínima permitida é 0.0")
    @DecimalMax(value = "10.0", inclusive = true, message = "A nota máxima permitida é 10.0")
    private Double score;

    public GradeDTO() {
    }

    public GradeDTO(Long id, EvaluationDTO evaluation, String student, Double score) {
        this.id = id;
        this.evaluation = evaluation;
        this.student = student;
        this.score = score;
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public EvaluationDTO getEvaluation() {
        return evaluation;
    }
    public void setEvaluation(EvaluationDTO evaluation) {
        this.evaluation = evaluation;
    }

    public String getStudent() {
        return student;
    }
    public void setStudent(String student) {
        this.student = student;
    }

    public Double getScore() {
        return score;
    }
    public void setScore(Double score) {
        this.score = score;
    }
}
