package com.api.gestaoescolar.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RegisterTeacherDTO {

    private final String userType = "TEACHER";
    @NotBlank(message = "O nome de usuário é obrigatório.")
    @Size(min = 3, max = 30, message = "O nome de usuário deve ter entre 3 e 30 caracteres.")
    private String username;

    @NotBlank(message = "O email é obrigatório.")
    @Email(message = "O email deve ser válido.")
    private String email;

    @NotBlank(message = "A senha é obrigatória.")
    @Size(min = 6, message = "A senha deve ter no mínimo 6 caracteres.")
    private String password;

    @NotBlank(message = "A especialidade é obrigatória.")
    @Size(max = 50, message = "A especialidade deve ter no máximo 50 caracteres.")
    private String speciality;

    public RegisterTeacherDTO(
            @NotBlank(message = "O nome de usuário é obrigatório.") @Size(min = 3, max = 30, message = "O nome de usuário deve ter entre 3 e 30 caracteres.") String username,
            @NotBlank(message = "O email é obrigatório.") @Email(message = "O email deve ser válido.") String email,
            @NotBlank(message = "A senha é obrigatória.") @Size(min = 6, message = "A senha deve ter no mínimo 6 caracteres.") String password,
            @NotBlank(message = "A especialidade é obrigatória.") @Size(max = 50, message = "A especialidade deve ter no máximo 50 caracteres.") String speciality) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.speciality = speciality;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSpeciality() {
        return speciality;
    }

    public void setSpeciality(String speciality) {
        this.speciality = speciality;
    }

    public String getUserType() {
        return userType;
    }

        
}
