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
    private List<Lesson> lessons = new ArrayList<>();

    public Teacher(Long id, String fullName, String cpf, String email, String password,
            Instant createdAt, String speciality, List<Lesson> lessons) {
        super(id, fullName, cpf, email, password, createdAt);
        this.speciality = speciality;
        this.lessons = lessons;
    }

    public Teacher(String speciality, List<Lesson> lessons) {
        this.speciality = speciality;
        this.lessons = lessons;
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

    public List<Lesson> getLessons() {
        return lessons;
    }

    public void setLessons(List<Lesson> lessons) {
        this.lessons = lessons != null ? lessons : new ArrayList<>();
    }

    public void addLessons(Lesson lessons) {
        if (lessons != null) {
            this.lessons.add(lessons);
            lessons.setTeacher(this);
        }
    }

    public void removeLessons(Lesson lessons) {
        if (lessons != null) {
            this.lessons.remove(lessons);
            lessons.setTeacher(null);
        }
    }
}