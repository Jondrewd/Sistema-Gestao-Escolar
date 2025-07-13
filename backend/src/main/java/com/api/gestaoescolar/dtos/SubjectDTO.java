package com.api.gestaoescolar.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class SubjectDTO {

    private Long id;

    @NotBlank(message = "O nome da matéria é obrigatório")
    @Size(min = 2, max = 100, message = "O nome da matéria deve ter entre 2 e 100 caracteres")
    private String name;

    public SubjectDTO() {
    }

    public SubjectDTO(Long id, String name) {
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
}
