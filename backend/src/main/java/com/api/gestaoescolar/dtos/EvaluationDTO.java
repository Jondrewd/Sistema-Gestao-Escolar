package com.api.gestaoescolar.dtos;

import java.time.LocalDate;

public class EvaluationDTO {

    private Long id;
    private LocalDate date;   
    private Long subjectId;      
    private String subjectName;  
    private Long classId;

    public EvaluationDTO() {}

    public EvaluationDTO(Long id, LocalDate date, Long subjectId, String subjectName, Long classId) {
        this.id = id;
        this.date = date;
        this.subjectId = subjectId;
        this.subjectName = subjectName;
        this.classId = classId;
    }




    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
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




    public Long getClassId() {
        return classId;
    }




    public void setClassId(Long classId) {
        this.classId = classId;
    }

   

}
