package com.api.gestaoescolar.dtos;

import java.time.Instant;

public class AttendanceDTO {
    private Long id;
    private Instant date;
    private Boolean present;
    private String student;
    private String subjectName;
    private String teacherName;
    private Long subjectId;

    public AttendanceDTO(Instant date, Long subjectId, String subjectName,String teacherName, Long id, Boolean present, String student) {
        this.date = date;
        this.subjectId = subjectId;
        this.id = id;
        this.subjectName = subjectName;
        this.teacherName = teacherName;
        this.present = present;
        this.student = student;
    }

    public AttendanceDTO() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getDate() {
        return date;
    }

    public void setDate(Instant date) {
        this.date = date;
    }

    public Boolean getPresent() {
        return present;
    }

    public void setPresent(Boolean present) {
        this.present = present;
    }

    public String getStudent() {
        return student;
    }

    public void setStudent(String student) {
        this.student = student;
    }

    public Long getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Long subjectId) {
        this.subjectId = subjectId;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    } 
    
}

    

