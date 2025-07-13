package com.api.gestaoescolar.dtos;

public class StudentGradeDTO {
    private String student;
    private double score;
    
    public StudentGradeDTO(String student, double score) {
        this.student = student;
        this.score = score;
    }
    public StudentGradeDTO() {
    }
    public String getStudent() {
        return student;
    }
    public void setStudent(String student) {
        this.student = student;
    }
    public double getScore() {
        return score;
    }
    public void setScore(double score) {
        this.score = score;
    }

    
}