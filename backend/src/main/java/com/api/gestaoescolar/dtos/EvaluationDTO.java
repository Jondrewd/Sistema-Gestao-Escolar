package com.api.gestaoescolar.dtos;

import java.time.Instant;


public class EvaluationDTO {

    private Long id;
    private Instant date;
    private Double score;
    private String student;
    private CourseDTO course;

    public EvaluationDTO(CourseDTO course, Instant date, Long id, Double score, String student) {
        this.course = course;
        this.date = date;
        this.id = id;
        this.score = score;
        this.student = student;
    }

    public EvaluationDTO() {
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

    public CourseDTO getCourse() {
        return course;
    }

    public void setCourse(CourseDTO course) {
        this.course = course;
    }

}
