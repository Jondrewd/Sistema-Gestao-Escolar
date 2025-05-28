package com.api.gestaoescolar.entities;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

@Entity
@DiscriminatorValue("STUDENT")
public class Student extends User {

    private String registrationNumber;

    @ManyToOne
    @JoinColumn(name = "classes_id")
    private Classes classes;
    
    @OneToMany(mappedBy = "student")
    private List<Grade> grades = new ArrayList<>();

    @OneToMany(mappedBy = "student")
    private List<Attendance> attendances = new ArrayList<>();

    public Student(Long id, String fullName, String cpf, String email, String password, Instant createdAt,
            String registrationNumber, Classes classes, List<Grade> grades, List<Attendance> attendances) {
        super(id, fullName, cpf, email, password, createdAt);
        this.registrationNumber = registrationNumber;
        this.classes = classes;
        this.grades = grades;
        this.attendances = attendances;
    }

    public Student(String registrationNumber, Classes classes, List<Grade> grades,
            List<Attendance> attendances) {
        this.registrationNumber = registrationNumber;
        this.classes = classes;
        this.grades = grades;
        this.attendances = attendances;
    }

    public Student() {
        super();
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public Classes getClasses() {
        return classes;
    }

    public void setClasses(Classes classes) {
        this.classes = classes;
    }

    public List<Grade> getGrades() {
        return grades;
    }

    public void setGrades(List<Grade> grades) {
        this.grades = grades != null ? grades : new ArrayList<>();
    }

    public List<Attendance> getAttendances() {
        return attendances;
    }

    public void setAttendances(List<Attendance> attendances) {
        this.attendances = attendances != null ? attendances : new ArrayList<>();
    }

    public void addGrade(Grade grades) {
        if (grades != null) {
            this.grades.add(grades);
            grades.setStudent(this);
        }
    }

    public void addAttendance(Attendance attendance) {
        if (attendance != null) {
            this.attendances.add(attendance);
            attendance.setStudent(this);
        }
    }
}