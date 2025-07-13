package com.api.gestaoescolar.dtos;

import org.hibernate.validator.constraints.br.CPF;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class RegisterDTO {
    @NotBlank(message = "O nome completo é obrigatório.")
    @Size(min = 3, max = 30, message = "O nome completo deve ter entre 3 e 30 caracteres.")
    private String fullName;

    @NotBlank(message = "O CPF é obrigatório.")
    @CPF(message = "CPF inválido")
    private String cpf;

    @NotBlank(message = "O email é obrigatório.")
    @Email(message = "O email deve ser válido.")
    private String email;

    @NotBlank(message = "A senha é obrigatória.")
    @Size(min = 6, message = "A senha deve ter no mínimo 6 caracteres.")
    private String password;

    @JsonIgnore
    private String userType;

    public RegisterDTO() {}

    public RegisterDTO(
            @NotBlank(message = "O nome completo é obrigatório.") @Size(min = 3, max = 30, message = "O nome completo deve ter entre 3 e 30 caracteres.") String fullName,
            @NotBlank(message = "O CPF é obrigatório.") @Pattern(regexp = "^\\d{3}\\.?\\d{3}\\.?\\d{3}\\-?\\d{2}$", message = "CPF deve seguir o padrão 000.000.000-00") String cpf,
            @NotBlank(message = "O email é obrigatório.") @Email(message = "O email deve ser válido.") String email,
            @NotBlank(message = "A senha é obrigatória.") @Size(min = 6, message = "A senha deve ter no mínimo 6 caracteres.") String password,
            String userType) {
        this.fullName = fullName;
        this.cpf = cpf;
        this.email = email;
        this.password = password;
        this.userType = userType;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
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

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }
    
    
}
