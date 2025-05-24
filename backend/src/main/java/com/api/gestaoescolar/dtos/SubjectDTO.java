package com.api.gestaoescolar.dtos;

import java.util.ArrayList;
import java.util.List;

import com.api.gestaoescolar.entities.Enum.DayOfWeek;

public class SubjectDTO {
    private Long id;
    private String name;
    private String teacher;
    private Long teacherId;
    private DayOfWeek dayOfWeek;
    private String startTime;
    private String endTime;  
    private Long classId; 
    private List<EvaluationDTO> evaluations = new ArrayList<>();

    public SubjectDTO() {}

    public SubjectDTO(Long id, String name, String teacher, Long teacherId, DayOfWeek dayOfWeek,
                      String startTime, String endTime, Long classId,
                      List<EvaluationDTO> evaluations) {
        this.id = id;
        this.name = name;
        this.teacher = teacher;
        this.teacherId = teacherId;
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
        this.classId = classId;
        this.evaluations = evaluations != null ? evaluations : new ArrayList<>();
    }

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

    public DayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }
    public void setDayOfWeek(DayOfWeek dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public String getStartTime() {
        return startTime;
    }
    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }
    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public Long getClassId() {
        return classId;
    }
    public void setClassId(Long classId) {
        this.classId = classId;
    }

    public List<EvaluationDTO> getEvaluations() {
        return evaluations;
    }
    public void setEvaluations(List<EvaluationDTO> evaluations) {
        this.evaluations = evaluations;
    }

    public Long getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(Long teacherId) {
        this.teacherId = teacherId;
    }
}
