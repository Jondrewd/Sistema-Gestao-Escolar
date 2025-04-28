package com.api.gestaoescolar.dtos;

import java.time.Instant;

public class AttendanceDTO {
    private Long id;
    private Instant date;
    private Boolean present;
    private String student;
    private Long groupId;

    public AttendanceDTO(Instant date, Long groupId, Long id, Boolean present, String student) {
        this.date = date;
        this.groupId = groupId;
        this.id = id;
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

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }
    
    
}
