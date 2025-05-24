package com.api.gestaoescolar.entities;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "class_classes")  
public class Classes {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @OneToMany(mappedBy="classes",fetch = FetchType.LAZY)
    private List<Subject> subject = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY)
    private Set<Student> students = new HashSet<>();


     public Classes(Long id, String name, List<Subject> subject, Set<Student> students) {
        this.id = id;
        this.name = name;
        this.subject = subject;
        this.students = students;
    }


     public Classes(String name, List<Subject> subject) {
        this(null, name, subject, null);
    }

    public Classes() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
  
    public Set<Student> getStudents() {
        return students;
    }

    public void setStudents(Set<Student> students) {
        this.students = students != null ? students : new HashSet<>();
    }


    public void addStudent(Student student) {
        if (student != null && !this.students.contains(student)) {
            this.students.add(student);
        }
    }

    public void removeStudent(Student student) {
        if (student != null) {
            this.students.remove(student);
        }
    }

    public void addSubject(Subject subject) {
        if (subject != null && !this.subject.contains(subject)) {
            this.subject.add(subject);
        }
    }

    public void removeSubject(Subject subject) {
        if (subject != null) {
            this.subject.remove(subject);
        }
    }
   
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Classes other = (Classes) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }


    public List<Subject> getSubjects() {
        return subject;
    }


    public void setSubjects(List<Subject> subject) {
        this.subject = subject;
    }

    public Object stream() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}