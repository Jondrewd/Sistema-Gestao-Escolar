package com.api.gestaoescolar.dtos;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.Instant;

public class AttendanceDTO {

    private Long id;

    @NotNull(message = "A data da presença é obrigatória")
    @FutureOrPresent(message = "A data da presença não pode ser no passado")
    private Instant date;

    @NotNull(message = "O campo 'presente' é obrigatório")
    private Boolean present;

    @NotNull(message = "O CPF do estudante é obrigatório")
    private String student;

    private String subjectName;  // Campo usado só em response, por enquanto sem validação

    @NotNull(message = "O ID da matéria é obrigatório")
    @Positive(message = "O ID da matéria deve ser um número positivo")
    private Long subjectId;

    public AttendanceDTO() {}

    public AttendanceDTO(Instant date, Long subjectId, String subjectName, Long id, Boolean present, String student) {
        this.date = date;
        this.subjectId = subjectId;
        this.id = id;
        this.subjectName = subjectName;
        this.present = present;
        this.student = student;
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public Instant getDate() {
        return date;
    }
    public void setDate(Instant date) {
        this.date = date;
    }

    public Boolean getPresent() {
        return present;
    }
    public void setPresent(Boolean present) {
        this.present = present;
    }

    public String getStudent() {
        return student;
    }
    public void setStudent(String student) {
        this.student = student;
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
}
