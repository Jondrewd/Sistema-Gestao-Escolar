package com.api.gestaoescolar.dtos;

import java.time.Instant;
import java.util.List;

public class StudentDTO {
    private final String userType = "Student";
    private Long id;
    private String fullName;
    private String cpf;
    private String email;
    private String registrationNumber;
    private Instant createdAt;

    private ClassesDTO classes;
    private List<GradeDTO> grades;
    private List<AttendanceDTO> attendances;

    public StudentDTO() {
    }

    public StudentDTO(
        Long id,
        String fullName,
        String cpf,
        String email,
        String registrationNumber,
        Instant createdAt,
        ClassesDTO classes,
        List<GradeDTO> grades,
        List<AttendanceDTO> attendances
    ) {
        this.id = id;
        this.fullName = fullName;
        this.cpf = cpf;
        this.email = email;
        this.registrationNumber = registrationNumber;
        this.createdAt = createdAt;
        this.classes = classes;
        this.grades = grades;
        this.attendances = attendances;
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getCpf() {
        return cpf;
    }
    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }
    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public ClassesDTO getClasses() {
        return classes;
    }
    public void setClasses(ClassesDTO classes) {
        this.classes = classes;
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

    public String getUserType() {
        return userType;
    }
}
