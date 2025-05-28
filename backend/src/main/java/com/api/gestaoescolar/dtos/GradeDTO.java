package com.api.gestaoescolar.dtos;

public class GradeDTO {

    private Long id;
    private EvaluationDTO evaluation;
    private String student;
    private Double score;
    
    public GradeDTO(Long id, EvaluationDTO evaluation, String student, Double score) {
        this.id = id;
        this.evaluation = evaluation;
        this.student = student;
        this.score = score;
    }

    public GradeDTO() {
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public EvaluationDTO getEvaluation() {
        return evaluation;
    }
    public void setEvaluation(EvaluationDTO evaluation) {
        this.evaluation = evaluation;
    }
    public String getStudent() {
        return student;
    }
    public void setStudent(String student) {
        this.student = student;
    }
    public Double getScore() {
        return score;
    }
    public void setScore(Double score) {
        this.score = score;
    }


}
