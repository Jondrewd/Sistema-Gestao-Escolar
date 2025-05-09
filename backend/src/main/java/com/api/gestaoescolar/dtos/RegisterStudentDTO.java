package com.api.gestaoescolar.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class RegisterStudentDTO {
    @JsonIgnore
    private final String userType = "STUDENT";

    @NotBlank(message = "O nome de usuário é obrigatório.")
    @Size(min = 3, max = 30, message = "O nome de usuário deve ter entre 3 e 30 caracteres.")
    private String username;

    @NotBlank(message = "O CPF é obrigatório.")
    @Pattern(regexp = "^\\d{3}\\.?\\d{3}\\.?\\d{3}\\-?\\d{2}$", 
         message = "CPF deve seguir o padrão 000.000.000-00")
    private String cpf;

    @NotBlank(message = "O email é obrigatório.")
    @Email(message = "O email deve ser válido.")
    private String email;

    @NotBlank(message = "A senha é obrigatória.")
    @Size(min = 6, message = "A senha deve ter no mínimo 6 caracteres.")
    private String password;

    @NotBlank(message = "O número de matrícula é obrigatório.")
    @Size(min = 3, max = 20, message = "O número de matrícula deve ter entre 3 e 20 caracteres.")
    private String registrationNumber;

    public RegisterStudentDTO(
            @NotBlank(message = "O nome de usuário é obrigatório.") @Size(min = 3, max = 30, message = "O nome de usuário deve ter entre 3 e 30 caracteres.") String username,
            @NotBlank(message = "O email é obrigatório.") @Email(message = "O email deve ser válido.") String email,
            @NotBlank(message = "A senha é obrigatória.") @Size(min = 6, message = "A senha deve ter no mínimo 6 caracteres.") String password,
            @NotBlank(message = "O número de matrícula é obrigatório.") @Size(min = 3, max = 20, message = "O número de matrícula deve ter entre 3 e 20 caracteres.") String registrationNumber) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.registrationNumber = registrationNumber;
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

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public String getUserType() {
        return userType;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    
}
