package com.api.gestaoescolar.dtos;

public class SubjectDTO {
    private Long id;
    private String name;

    public SubjectDTO(Long id, String name) {
        this.id = id;
        this.name = name;
    }
    
    public SubjectDTO() {
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
   
    
}
