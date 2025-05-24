package com.api.gestaoescolar.dtos;

import java.time.Instant;

public class UserDTO {
    private String userType;
    private Long id;
    private String fullName;
    private String cpf;
    private String email;
    private String password;
    private Instant createdAt;

    public UserDTO() {
    }
    public UserDTO(String userType, Long id, String fullName, String cpf, String email, String password,
            Instant createdAt) {
        this.userType = userType;
        this.id = id;
        this.fullName = fullName;
        this.cpf = cpf;
        this.email = email;
        this.password = password;
        this.createdAt = createdAt;
    }
    public String getUserType() {
        return userType;
    }
    public void setUserType(String userType) {
        this.userType = userType;
    }
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
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
    public Instant getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
    

}
