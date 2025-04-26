package com.api.gestaoescolar.entities;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import com.api.gestaoescolar.entities.enums.SchoolRoles;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;

@Entity
@DiscriminatorValue("STUDENT")
public class Student extends User {

    private Long registrationNumber;

    @ManyToMany(mappedBy = "students")
    private List<Group> Groups = new ArrayList<>();
    
    @OneToMany(mappedBy = "student")
    private List<Evaluation> evaluations = new ArrayList<>();

    @OneToMany(mappedBy = "student")
    private List<Attendance> attendances = new ArrayList<>();

    public Student(Long id, String username, String email, String password, 
                  SchoolRoles schoolRoles, Instant createdAt, List<Roles> roles, 
                  Long registrationNumber, List<Group> Groups, 
                  List<Evaluation> evaluations, List<Attendance> attendances) {
        super(id, username, email, password, schoolRoles, createdAt, roles);
        this.registrationNumber = registrationNumber;
        this.Groups = Groups != null ? Groups : new ArrayList<>();
        this.evaluations = evaluations != null ? evaluations : new ArrayList<>();
        this.attendances = attendances != null ? attendances : new ArrayList<>();
    }

    // Construtor simplificado para criação
    public Student(String username, String email, String password, 
                  Long registrationNumber) {
        super(null, username, email, password, SchoolRoles.STUDENT, Instant.now(), null);
        this.registrationNumber = registrationNumber;
    }

    public Student() {
        super();
    }

    // Getters e Setters
    public Long getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(Long registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public List<Group> getGroups() {
        return Groups;
    }

    public void setGroups(List<Group> Groups) {
        this.Groups = Groups != null ? Groups : new ArrayList<>();
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

    public void enrollInGroup(Group group) {
        if (group != null && !this.Groups.contains(group)) {
            this.Groups.add(group);
            group.getStudents().add(this);
        }
    }

    public void unenrollFromGroup(Group group) {
        if (group != null) {
            this.Groups.remove(group);
            group.getStudents().remove(this);
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