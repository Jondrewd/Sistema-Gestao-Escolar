package com.api.gestaoescolar.entities;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;

@Entity
@DiscriminatorValue("STUDENT")
public class Student extends User {

    private Long registrationNumber;

    @ManyToMany(mappedBy = "students")
    private List<Classes> Classes = new ArrayList<>();
    
    @OneToMany(mappedBy = "student")
    private List<Evaluation> evaluations = new ArrayList<>();

    @OneToMany(mappedBy = "student")
    private List<Attendance> attendances = new ArrayList<>();


    public Student(Long id, String username, String cpf, String email, String password,
            Instant createdAt, List<Roles> roles, Long registrationNumber, List<Classes> classes,
            List<Evaluation> evaluations, List<Attendance> attendances) {
        super(id, username, cpf, email, password, createdAt, roles);
        this.registrationNumber = registrationNumber;
        Classes = classes;
        this.evaluations = evaluations;
        this.attendances = attendances;
    }

    public Student(Long registrationNumber, List<Classes> classes, List<Evaluation> evaluations,
            List<Attendance> attendances) {
        this.registrationNumber = registrationNumber;
        Classes = classes;
        this.evaluations = evaluations;
        this.attendances = attendances;
    }

    public Student() {
        super();
    }

    public Long getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(Long registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public List<Classes> getClasses() {
        return Classes;
    }

    public void setClasses(List<Classes> Classes) {
        this.Classes = Classes != null ? Classes : new ArrayList<>();
    }

    public List<Evaluation> getEvaluations() {
        return evaluations;
    }

    public void setEvaluations(List<Evaluation> evaluations) {
        this.evaluations = evaluations != null ? evaluations : new ArrayList<>();
    }

    public List<Attendance> getAttendances() {
        return attendances;
    }

    public void setAttendances(List<Attendance> attendances) {
        this.attendances = attendances != null ? attendances : new ArrayList<>();
    }

    public void enrollInClasses(Classes classes) {
        if (classes != null && !this.Classes.contains(classes)) {
            this.Classes.add(classes);
            classes.getStudents().add(this);
        }
    }

    public void unenrollFromClasses(Classes classes) {
        if (classes != null) {
            this.Classes.remove(classes);
            classes.getStudents().remove(this);
        }
    }

    public void addEvaluation(Evaluation evaluation) {
        if (evaluation != null) {
            this.evaluations.add(evaluation);
            evaluation.setStudent(this);
        }
    }

    public void addAttendance(Attendance attendance) {
        if (attendance != null) {
            this.attendances.add(attendance);
            attendance.setStudent(this);
        }
    }
}