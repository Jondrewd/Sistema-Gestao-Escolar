package com.api.gestaoescolar.entities;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;

@Entity
@DiscriminatorValue("TEACHER")
public class Teacher extends User {

    private String speciality;

    @OneToMany(mappedBy = "teacher")
    private List<Subject> subject = new ArrayList<>();

    public Teacher(Long id, String fullName, String cpf, String email, String password,
            Instant createdAt, String speciality, List<Subject> subject) {
        super(id, fullName, cpf, email, password, createdAt);
        this.speciality = speciality;
        this.subject = subject;
    }

    public Teacher(String speciality, List<Subject> subject) {
        this.speciality = speciality;
        this.subject = subject;
    }

    public Teacher() {
        super();
    }

    public String getSpeciality() {
        return speciality;
    }

    public void setSpeciality(String speciality) {
        this.speciality = speciality;
    }

    public List<Subject> getSubjects() {
        return subject;
    }

    public void setSubjects(List<Subject> subject) {
        this.subject = subject != null ? subject : new ArrayList<>();
    }

    public void addSubjects(Subject subject) {
        if (subject != null) {
            this.subject.add(subject);
            subject.setTeacher(this);
        }
    }

    public void removeSubjects(Subject subject) {
        if (subject != null) {
            this.subject.remove(subject);
            subject.setTeacher(null);
        }
    }
}