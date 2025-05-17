package com.api.gestaoescolar.dtos;

import java.time.Instant;
import java.util.List;

public class TeacherDTO {
    private final String usertype = "TEACHER";
    private Long id;
    private String fullName;
    private String cpf;
    private String email;
    private String speciality;
    private Instant createdAt;
    private List<ClassesDTO> classes;
    
    public TeacherDTO() {
    }

    public TeacherDTO(Long id, String fullName, String cpf, String email, String speciality,
            Instant createdAt, List<ClassesDTO> classes) {
        this.id = id;
        this.fullName = fullName;
        this.cpf = cpf;
        this.email = email;
        this.speciality = speciality;
        this.createdAt = createdAt;
        this.classes = classes;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getSpeciality() {
        return speciality;
    }

    public void setSpeciality(String speciality) {
        this.speciality = speciality;
    }

    public List<ClassesDTO> getClasses() {
        return classes;
    }

    public void setClasses(List<ClassesDTO> classes) {
        this.classes = classes;
    }

    public String getUsertype() {
        return usertype;
    }

    
}

