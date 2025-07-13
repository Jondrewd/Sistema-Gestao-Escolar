package com.api.gestaoescolar.dtos;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.List;

public class ClassesDTO {

    private Long id;

    @NotBlank(message = "O nome da turma é obrigatório")
    @Size(max = 100, message = "O nome da turma não pode ter mais que 100 caracteres")
    private String name;

    @Valid
    private List<LessonDTO> lessons = new ArrayList<>();

    private List<String> studentCpfs = new ArrayList<>();

    public ClassesDTO() {}

    public ClassesDTO(Long id, String name, List<LessonDTO> lessons, List<String> studentCpfs) {
        this.id = id;
        this.name = name;
        this.lessons = lessons != null ? lessons : new ArrayList<>();
        this.studentCpfs = studentCpfs != null ? studentCpfs : new ArrayList<>();
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

    public List<LessonDTO> getLessons() {
        return lessons;
    }
    public void setLessons(List<LessonDTO> lessons) {
        this.lessons = lessons != null ? lessons : new ArrayList<>();
    }

    public List<String> getStudentCpfs() {
        return studentCpfs;
    }
    public void setStudentCpfs(List<String> studentCpfs) {
        this.studentCpfs = studentCpfs != null ? studentCpfs : new ArrayList<>();
    }
}
