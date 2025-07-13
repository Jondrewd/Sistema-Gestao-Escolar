package com.api.gestaoescolar.dtos;

import org.hibernate.validator.constraints.br.CPF;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RegisterTeacherDTO extends RegisterDTO{
    
    @NotBlank(message = "A especialidade é obrigatória.")
    @Size(max = 50, message = "A especialidade deve ter no máximo 50 caracteres.")
    private String speciality;

    public RegisterTeacherDTO(
            @NotBlank(message = "A especialidade é obrigatória.") @Size(max = 50, message = "A especialidade deve ter no máximo 50 caracteres.") String speciality) {
        this.speciality = speciality;
    }

    public RegisterTeacherDTO(
            @NotBlank(message = "O nome completo é obrigatório.") @Size(min = 3, max = 30, message = "O nome completo deve ter entre 3 e 30 caracteres.") String fullName,
            @NotBlank(message = "O CPF é obrigatório.")  @CPF(message = "CPF inválido") String cpf,
            @NotBlank(message = "O email é obrigatório.") @Email(message = "O email deve ser válido.") String email,
            @NotBlank(message = "A senha é obrigatória.") @Size(min = 6, message = "A senha deve ter no mínimo 6 caracteres.") String password,
            String userType,
            @NotBlank(message = "A especialidade é obrigatória.") @Size(max = 50, message = "A especialidade deve ter no máximo 50 caracteres.") String speciality) {
        super(fullName, cpf, email, password, userType);
        this.speciality = speciality;
    }
    

    public RegisterTeacherDTO() {
    }

    public String getSpeciality() {
        return speciality;
    }

    public void setSpeciality(String speciality) {
        this.speciality = speciality;
    }
        
}
