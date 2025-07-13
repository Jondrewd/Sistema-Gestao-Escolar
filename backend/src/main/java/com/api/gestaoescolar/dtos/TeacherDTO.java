package com.api.gestaoescolar.dtos;

import jakarta.validation.constraints.*;
import java.time.Instant;
import java.util.List;

public class TeacherDTO extends UserDTO {

    @NotBlank(message = "A especialidade é obrigatória")
    @Size(min = 2, max = 50, message = "A especialidade deve ter entre 2 e 50 caracteres")
    private String speciality;

    private List<LessonDTO> lessons;

    public TeacherDTO() {
        super();
        setUserType("TEACHER");
    }

    public TeacherDTO(Long id, String fullName, String cpf, String email, String password,
                      String speciality, Instant createdAt, List<LessonDTO> lessons) {
        super("TEACHER", id, fullName, cpf, email, password, createdAt);
        this.speciality = speciality;
        this.lessons = lessons;
    }

    public String getSpeciality() {
        return speciality;
    }

    public void setSpeciality(String speciality) {
        this.speciality = speciality;
    }

    public List<LessonDTO> getLessons() {
        return lessons;
    }

    public void setLessons(List<LessonDTO> lessons) {
        this.lessons = lessons;
    }
}
