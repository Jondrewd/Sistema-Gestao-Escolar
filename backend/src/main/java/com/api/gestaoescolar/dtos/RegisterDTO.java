package com.api.gestaoescolar.dtos;

public class RegisterDTO {
    private String cpf;
    private String email;
    private String password;
    private String userType;

    public RegisterDTO() {}
    public RegisterDTO(String cpf, String email, String password, String userType) {
        this.cpf = cpf;
        this.email = email;
        this.password = password;
        this.userType = userType;
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
