package com.api.gestaoescolar.dtos;

import java.time.Instant;

public class EvaluationDTO {

    private Long id;
    private Instant date;
    private Double score;
    private String student;      
    private Long subjectId;      
    private String subjectName;  

    public EvaluationDTO() {}

    public EvaluationDTO(Long id, Instant date, Double score, String student, Long subjectId, String subjectName) {
        this.id = id;
        this.date = date;
        this.score = score;
        this.student = student;
        this.subjectId = subjectId;
        this.subjectName = subjectName;
    }


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

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
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

   

}
