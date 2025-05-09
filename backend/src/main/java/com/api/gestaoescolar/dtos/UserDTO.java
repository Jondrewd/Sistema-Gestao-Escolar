package com.api.gestaoescolar.dtos;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class UserDTO {
    private String userType;
    private Long id;
    private String username;
    private String cpf;
    private String email;
    private String password;
    private Instant createdAt;
    private String schoolRole;
    
    /* Student */
    private Long registrationNumber;
    private List<ClassesDTO> studentClasses = new ArrayList<>();
    private List<EvaluationDTO> evaluations = new ArrayList<>();
    private List<AttendanceDTO> attendances = new ArrayList<>();

    /*Teacher*/
    private String speciality;
    private List<ClassesDTO> teacherClasses = new ArrayList<>();

    public UserDTO() {}

    public UserDTO(Instant createdAt, String cpf, String email, Long id, String password, Long registrationNumber, String schoolRole, String speciality, String userType, String username) {
        this.createdAt = createdAt;
        this.email = email;
        this.id = id;
        this.cpf = cpf;
        this.password = password;
        this.registrationNumber = registrationNumber;
        this.schoolRole = schoolRole;
        this.speciality = speciality;
        this.userType = userType;
        this.username = username;
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

    public String getSchoolRole() {
        return schoolRole;
    }

    public void setSchoolRole(String schoolRole) {
        this.schoolRole = schoolRole;
    }

    public Long getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(Long registrationNumber) {
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


}
