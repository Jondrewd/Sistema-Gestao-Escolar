package com.api.gestaoescolar.entities;

import java.time.Instant;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("ADMIN")
public class Admin extends User {

    public Admin() {
    }

    public Admin(Long id, String fullName, String cpf, String email, String password, Instant createdAt) {
        super(id, fullName, cpf, email, password, createdAt);
    }

}
