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
    private List<Classes> Classes = new ArrayList<>();

    public Teacher(Long id, String fullName, String cpf, String email, String password,
            Instant createdAt, String speciality, List<com.api.gestaoescolar.entities.Classes> classes) {
        super(id, fullName, cpf, email, password, createdAt);
        this.speciality = speciality;
        Classes = classes;
    }

    public Teacher(String speciality, List<Classes> classes) {
        this.speciality = speciality;
        Classes = classes;
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

    public List<Classes> getClasses() {
        return Classes;
    }

    public void setClasses(List<Classes> Classes) {
        this.Classes = Classes != null ? Classes : new ArrayList<>();
    }

    public void addClasses(Classes classes) {
        if (classes != null) {
            this.Classes.add(classes);
            classes.setTeacher(this);
        }
    }

    public void removeClasses(Classes classes) {
        if (classes != null) {
            this.Classes.remove(classes);
            classes.setTeacher(null);
        }
    }
}