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
@Table(name = "classes")  
public class Classes {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @OneToMany(fetch = FetchType.LAZY)
    private List<Lesson> lessons = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY)
    private Set<Student> students = new HashSet<>();


     public Classes(Long id, String name, List<Lesson> lessons, Set<Student> students) {
        this.id = id;
        this.name = name;
        this.lessons = lessons;
        this.students = students;
    }


     public Classes(String name, List<Lesson> lessons) {
        this(null, name, lessons, null);
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

    public void addLesson(Lesson lessons) {
        if (lessons != null && !this.lessons.contains(lessons)) {
            this.lessons.add(lessons);
        }
    }

    public void removeLesson(Lesson lessons) {
        if (lessons != null) {
            this.lessons.remove(lessons);
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


    public List<Lesson> getLessons() {
        return lessons;
    }


    public void setLessons(List<Lesson> lessons) {
        this.lessons = lessons;
    }

    public Object stream() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}