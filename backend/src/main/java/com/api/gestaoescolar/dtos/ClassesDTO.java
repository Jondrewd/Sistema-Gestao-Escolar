package com.api.gestaoescolar.dtos;

import java.util.ArrayList;
import java.util.List;

public class ClassesDTO {

    private Long id;
    private String name;
    private List<Long> subjectIds = new ArrayList<>();
    private List<String> studentCpfs = new ArrayList<>();

    public ClassesDTO() {}

    public ClassesDTO(Long id, String name, List<Long> subjectIds, List<String> studentCpfs) {
        this.id = id;
        this.name = name;
        this.subjectIds = subjectIds != null ? subjectIds : new ArrayList<>();
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

    public List<Long> getSubjectIds() {
        return subjectIds;
    }

    public void setSubjectIds(List<Long> subjectIds) {
        this.subjectIds = subjectIds != null ? subjectIds : new ArrayList<>();
    }

    public List<String> getStudentCpfs() {
        return studentCpfs;
    }

    public void setStudentCpfs(List<String> studentCpfs) {
        this.studentCpfs = studentCpfs != null ? studentCpfs : new ArrayList<>();
    }
}
