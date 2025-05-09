package com.api.gestaoescolar.dtos;

import java.util.ArrayList;
import java.util.List;

public class ClassesDTO {
    
    private Long id;
    private String name;
    private String teacher;
    private Long courseId;
    private List<String> students = new ArrayList<>();
    private List<AttendanceDTO> attendances = new ArrayList<>();

    public ClassesDTO(Long courseId, Long id, String name, String teacher) {
        this.courseId = courseId;
        this.id = id;
        this.name = name;
        this.teacher = teacher;
    }

    public ClassesDTO() {}

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

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public Long getCourse() {
        return courseId;
    }

    public void setCourse(Long courseId) {
        this.courseId = courseId;
    }

    public List<String> getStudents() {
        return students;
    }

    public void setStudents(List<String> students) {
        this.students = students;
    }

    public List<AttendanceDTO> getAttendances() {
        return attendances;
    }

    public void setAttendances(List<AttendanceDTO> attendances) {
        this.attendances = attendances;
    }
    
}
