package com.api.gestaoescolar.dtos;

import java.util.List;

public class CourseDTO {
    private Long id;
    private String name;
    private String description;
    private List<GroupDTO> group;
    private List<EvaluationDTO> evaluations;

    public CourseDTO() {
    }

    public CourseDTO(String description, List<EvaluationDTO> evaluations, List<GroupDTO> group, Long id, String name) {
        this.description = description;
        this.evaluations = evaluations;
        this.group = group;
        this.id = id;
        this.name = name;
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
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public List<GroupDTO> getGroup() {
        return group;
    }
    public void setGroup(List<GroupDTO> group) {
        this.group = group;
    }
    public List<EvaluationDTO> getEvaluations() {
        return evaluations;
    }
    public void setEvaluations(List<EvaluationDTO> evaluations) {
        this.evaluations = evaluations;
    }

    
}