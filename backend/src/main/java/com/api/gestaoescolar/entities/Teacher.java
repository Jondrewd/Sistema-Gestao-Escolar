package com.api.gestaoescolar.entities;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import com.api.gestaoescolar.entities.enums.SchoolRoles;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;

@Entity
@DiscriminatorValue("TEACHER")
public class Teacher extends User {

    private String speciality;

    @OneToMany(mappedBy = "teacher")
    private List<Classes> Classess = new ArrayList<>();

    public Teacher(Long id, String username, String email, String password, 
                  SchoolRoles schoolRoles, Instant createdAt, List<Roles> roles, 
                  String speciality, List<Classes> Classess) {
        super(id, username, email, password, schoolRoles, createdAt, roles);
        this.speciality = speciality;
        this.Classess = Classess != null ? Classess : new ArrayList<>();
    }

    public Teacher(String username, String email, String password, 
                  String speciality) {
        super(null, username, email, password, SchoolRoles.TEACHER, Instant.now(), null);
        this.speciality = speciality;
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

    public List<Classes> getClassess() {
        return Classess;
    }

    public void setClassess(List<Classes> Classess) {
        this.Classess = Classess != null ? Classess : new ArrayList<>();
    }

    public void addClasses(Classes classes) {
        if (classes != null) {
            this.Classess.add(classes);
            classes.setTeacher(this);
        }
    }

    public void removeClasses(Classes classes) {
        if (classes != null) {
            this.Classess.remove(classes);
            classes.setTeacher(null);
        }
    }
}