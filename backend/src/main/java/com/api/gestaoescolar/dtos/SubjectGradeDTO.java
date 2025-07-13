package com.api.gestaoescolar.dtos;

import java.util.List;

public class SubjectGradeDTO {
    private String subjectName;
    private double average;
    private List<StudentGradeDTO> grades;
    
    public SubjectGradeDTO(String subjectName, double average, List<StudentGradeDTO> grades) {
        this.subjectName = subjectName;
        this.average = average;
        this.grades = grades;
    }

    public SubjectGradeDTO() {}

    public String getSubjectName() {
        return subjectName;
    }
    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }
    public double getAverage() {
        return average;
    }
    public void setAverage(double average) {
        this.average = average;
    }
    public List<StudentGradeDTO> getGrades() {
        return grades;
    }
    public void setGrades(List<StudentGradeDTO> grades) {
        this.grades = grades;
    }

    
}