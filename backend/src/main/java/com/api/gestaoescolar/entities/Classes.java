package com.api.gestaoescolar.entities;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "class_classes")  
public class Classes {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToOne
    @JoinColumn(name = "teacher_id") 
    private Teacher teacher;

    @ManyToOne
    @JoinColumn(name = "course_id") 
    private Course course;

    @ManyToMany
    private List<Student> students = new ArrayList<>();

    @OneToMany(mappedBy = "classes")
    private List<Attendance> attendances = new ArrayList<>();

    public Classes(Long id, String name, Teacher teacher, Course course, 
                List<Student> students, List<Attendance> attendances) {
        this.id = id;
        this.name = name;
        this.teacher = teacher;
        this.course = course;
        this.students = students != null ? students : new ArrayList<>();
        this.attendances = attendances != null ? attendances : new ArrayList<>();
    }

    public Classes(String name, Teacher teacher, Course course) {
        this(null, name, teacher, course, null, null);
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

    public Teacher getTeacher() {
        return teacher;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public List<Student> getStudents() {
        return students;
    }

    public void setStudents(List<Student> students) {
        this.students = students != null ? students : new ArrayList<>();
    }

    public List<Attendance> getAttendances() {
        return attendances;
    }

    public void setAttendances(List<Attendance> attendances) {
        this.attendances = attendances != null ? attendances : new ArrayList<>();
    }

    public void addStudent(Student student) {
        if (student != null && !this.students.contains(student)) {
            this.students.add(student);
            student.getClassess().add(this);
        }
    }

    public void removeStudent(Student student) {
        if (student != null) {
            this.students.remove(student);
            student.getClassess().remove(this);
        }
    }

    public void addAttendance(Attendance attendance) {
        if (attendance != null) {
            this.attendances.add(attendance);
            attendance.setClasses(this);
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
}