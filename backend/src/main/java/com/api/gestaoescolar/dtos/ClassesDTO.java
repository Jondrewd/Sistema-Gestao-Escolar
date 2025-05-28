package com.api.gestaoescolar.dtos;

import java.util.ArrayList;
import java.util.List;

public class ClassesDTO {

    private Long id;
    private String name;
    private List<Long> lessonIds = new ArrayList<>();
    private List<String> studentCpfs = new ArrayList<>();

    public ClassesDTO() {}

    public ClassesDTO(Long id, String name, List<Long> lessonIds, List<String> studentCpfs) {
        this.id = id;
        this.name = name;
        this.lessonIds = lessonIds != null ? lessonIds : new ArrayList<>();
        this.studentCpfs = studentCpfs != null ? studentCpfs : new ArrayList<>();
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<Long> getLessonIds() {
        return lessonIds;
    }

    public List<String> getStudentCpfs() {
        return studentCpfs;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLessonIds(List<Long> lessonIds) {
        this.lessonIds = lessonIds != null ? lessonIds : new ArrayList<>();
    }

    public void setStudentCpfs(List<String> studentCpfs) {
        this.studentCpfs = studentCpfs != null ? studentCpfs : new ArrayList<>();
    }
}
