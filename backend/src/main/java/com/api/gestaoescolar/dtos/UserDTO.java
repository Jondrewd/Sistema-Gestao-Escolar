package com.api.gestaoescolar.dtos;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class UserDTO {
    private String userType;
    private Long id;
    private String username;
    private String fullName;
    private String cpf;
    private String email;
    private String password;
    private Instant createdAt;
    
    /* Student */
    private String registrationNumber;
    private List<ClassesDTO> studentClasses = new ArrayList<>();
    private List<EvaluationDTO> evaluations = new ArrayList<>();
    private List<AttendanceDTO> attendances = new ArrayList<>();

    /*Teacher*/
    private String speciality;
    private List<ClassesDTO> teacherClasses = new ArrayList<>();

    public UserDTO() {}

    public UserDTO(String username, String fullName, String cpf, String email, String password,
            Instant createdAt, String registrationNumber, List<ClassesDTO> studentClasses,
            List<EvaluationDTO> evaluations, List<AttendanceDTO> attendances, String speciality,
            List<ClassesDTO> teacherClasses) {
        this.username = username;
        this.fullName = fullName;
        this.cpf = cpf;
        this.email = email;
        this.password = password;
        this.createdAt = createdAt;
        this.registrationNumber = registrationNumber;
        this.studentClasses = studentClasses;
        this.evaluations = evaluations;
        this.attendances = attendances;
        this.speciality = speciality;
        this.teacherClasses = teacherClasses;
    }

    public String getUserType() {
        return userType;
    }


    public void setUserType(String userType) {
        this.userType = userType;
    }
    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public List<ClassesDTO> getStudentClasses() {
        return studentClasses;
    }
 
public void setStudentClasses(List<ClassesDTO> studentClasses) {
    this.studentClasses = studentClasses;
}

public void setTeacherClasses(List<ClassesDTO> teacherClasses) {
    this.teacherClasses = teacherClasses;
}


    public List<EvaluationDTO> getEvaluations() {
        return evaluations;
    }

    public void setEvaluations(List<EvaluationDTO> evaluations) {
        this.evaluations = evaluations;
    }

    public List<AttendanceDTO> getAttendances() {
        return attendances;
    }

    public void setAttendances(List<AttendanceDTO> attendances) {
        this.attendances = attendances;
    }

    public String getSpeciality() {
        return speciality;
    }

    public void setSpeciality(String speciality) {
        this.speciality = speciality;
    }

    public List<ClassesDTO> getTeacherClasses() {
        return teacherClasses;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }


}
