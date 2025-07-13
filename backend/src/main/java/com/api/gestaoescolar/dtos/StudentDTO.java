package com.api.gestaoescolar.dtos;

import jakarta.validation.constraints.*;
import java.time.Instant;
import java.util.List;

public class StudentDTO extends UserDTO {

    @NotBlank(message = "O número de matrícula é obrigatório")
    @Size(min = 5, max = 20, message = "A matrícula deve ter entre 5 e 20 caracteres")
    private String registrationNumber;

    @NotNull(message = "O ID da turma (classeId) é obrigatório")
    private Long classeId;

    private List<GradeDTO> grades;

    private List<AttendanceDTO> attendances;

    public StudentDTO() {
        super();
        setUserType("STUDENT");
    }

    public StudentDTO(Long id, String fullName, String cpf, String email, String password,
                      String registrationNumber, Instant createdAt, Long classeId,
                      List<GradeDTO> grades, List<AttendanceDTO> attendances) {
        super("STUDENT", id, fullName, cpf, email, password, createdAt);
        this.registrationNumber = registrationNumber;
        this.classeId = classeId;
        this.grades = grades;
        this.attendances = attendances;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public Long getClasseId() {
        return classeId;
    }

    public void setClasseId(Long classeId) {
        this.classeId = classeId;
    }

    public List<GradeDTO> getGrades() {
        return grades;
    }

    public void setGrades(List<GradeDTO> grades) {
        this.grades = grades;
    }

    public List<AttendanceDTO> getAttendances() {
        return attendances;
    }

    public void setAttendances(List<AttendanceDTO> attendances) {
        this.attendances = attendances;
    }
}
