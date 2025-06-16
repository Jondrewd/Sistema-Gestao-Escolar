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
            createRoleIfNotExists(roleRepository, "ROLE_ADMIN");
            createRoleIfNotExists(roleRepository, "ROLE_TEACHER");
            createRoleIfNotExists(roleRepository, "ROLE_STUDENT");
        };
    }

    private void createRoleIfNotExists(RolesRepository repository, String roleName) {
        if (repository.findByName(roleName).isEmpty()) {
            Roles role = new Roles();
            role.setName(roleName);
            repository.save(role);
        }
    }
}
