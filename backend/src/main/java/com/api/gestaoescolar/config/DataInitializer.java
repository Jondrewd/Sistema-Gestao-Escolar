package com.api.gestaoescolar.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.api.gestaoescolar.entities.Roles;
import com.api.gestaoescolar.repositories.RolesRepository;


@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initRoles(RolesRepository roleRepository) {
        return args -> {
            if (roleRepository.findByName("ROLE_ADMIN").isEmpty()) {
                Roles roleAdmin = new Roles();
                roleAdmin.setName("ROLE_ADMIN");
                roleRepository.save(roleAdmin);
            }
            if (roleRepository.findByName("ROLE_USER").isEmpty()) {
                Roles roleUser = new Roles();
                roleUser.setName("ROLE_USER");
                roleRepository.save(roleUser);
            }
        };
    }
}
