package com.api.gestaoescolar.dtos;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

public class EvaluationDTO {

    private Long id;

    @NotNull(message = "A data da avaliação é obrigatória")
    @FutureOrPresent(message = "A data da avaliação não pode ser no passado")
    private LocalDate date;

    @NotNull(message = "O ID da matéria (subjectId) é obrigatório")
    @Positive(message = "O ID da matéria deve ser um número positivo")
    private Long subjectId;

    private String subjectName;  

    @NotNull(message = "O ID da turma (classId) é obrigatório")
    @Positive(message = "O ID da turma deve ser um número positivo")
    private Long classId;

    public EvaluationDTO() {}

    public EvaluationDTO(Long id, LocalDate date, Long subjectId, String subjectName, Long classId) {
        this.id = id;
        this.date = date;
        this.subjectId = subjectId;
        this.subjectName = subjectName;
        this.classId = classId;
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return date;
    }
    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Long getSubjectId() {
        return subjectId;
    }
    public void setSubjectId(Long subjectId) {
        this.subjectId = subjectId;
    }

    public String getSubjectName() {
        return subjectName;
    }
    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public Long getClassId() {
        return classId;
    }
    public void setClassId(Long classId) {
        this.classId = classId;
    }
}
