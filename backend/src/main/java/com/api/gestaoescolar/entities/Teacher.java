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
    private List<Group> Groups = new ArrayList<>();

    public Teacher(Long id, String username, String email, String password, 
                  SchoolRoles schoolRoles, Instant createdAt, List<Roles> roles, 
                  String speciality, List<Group> Groups) {
        super(id, username, email, password, schoolRoles, createdAt, roles);
        this.speciality = speciality;
        this.Groups = Groups != null ? Groups : new ArrayList<>();
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

    public List<Group> getGroups() {
        return Groups;
    }

    public void setGroups(List<Group> Groups) {
        this.Groups = Groups != null ? Groups : new ArrayList<>();
    }

    public void addGroup(Group group) {
        if (group != null) {
            this.Groups.add(group);
            group.setTeacher(this);
        }
    }

    public void removeGroup(Group group) {
        if (group != null) {
            this.Groups.remove(group);
            group.setTeacher(null);
        }
    }
}