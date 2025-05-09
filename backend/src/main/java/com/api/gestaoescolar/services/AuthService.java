package com.api.gestaoescolar.services;

import com.api.gestaoescolar.config.SecurityConfig;
import com.api.gestaoescolar.dtos.AccountCredentialsDTO;
import com.api.gestaoescolar.dtos.RegisterStudentDTO;
import com.api.gestaoescolar.dtos.RegisterTeacherDTO;
import com.api.gestaoescolar.dtos.TokenDTO;
import com.api.gestaoescolar.entities.Roles;
import com.api.gestaoescolar.entities.Student;
import com.api.gestaoescolar.entities.Teacher;
import com.api.gestaoescolar.entities.User;
import com.api.gestaoescolar.repositories.RolesRepository;
import com.api.gestaoescolar.repositories.UserRepository;
import com.api.gestaoescolar.security.jwt.JwtTokenProvider;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final UserRepository repository;
    private final RolesRepository roleRepository;

    
    public AuthService(AuthenticationManager authenticationManager, JwtTokenProvider tokenProvider,
            UserRepository repository, RolesRepository roleRepository) {
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
        this.repository = repository;
        this.roleRepository = roleRepository;
    }
    
    @Autowired
    private SecurityConfig config;

   public ResponseEntity<String> registerTeacher(RegisterTeacherDTO data) {
    if (checkUsername(data.getUsername())) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body("Já existe um usuário cadastrado com esse nome de usuário.");
    }
    User newUser;
    Roles roles = roleRepository.findByName("ROLE_USER").get();
            Teacher teacher = new Teacher();
            teacher.setUsername(data.getUsername());
            teacher.setCpf(data.getCpf());
            teacher.setEmail(data.getEmail());
            teacher.setPassword(config.passwordEncoder().encode(data.getPassword())); 
            teacher.setRoles(Collections.singletonList(roles));
            newUser = teacher;

        repository.save(newUser); 

    return ResponseEntity.status(HttpStatus.CREATED)
            .body("Usuário registrado com sucesso.");
}

    public ResponseEntity<String> registerStudent(RegisterStudentDTO data) {
        if (checkUsername(data.getUsername())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Já existe um usuário cadastrado com esse nome de usuário.");
        }

        User newUser;
        Roles roles = roleRepository.findByName("ROLE_USER").get();
                Student student = new Student();
                student.setUsername(data.getUsername());
                student.setCpf(data.getCpf());
                student.setEmail(data.getEmail());
                student.setRoles(Collections.singletonList(roles));
                student.setPassword(config.passwordEncoder().encode(data.getPassword())); 
                newUser = student;
            repository.save(newUser); 

        return ResponseEntity.status(HttpStatus.CREATED)
                .body("Usuário registrado com sucesso.");
    }

    public ResponseEntity<TokenDTO> signin(AccountCredentialsDTO data) {
        try {
            String username = data.getUsername();
            String password = data.getPassword();

            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));

            var user = repository.findByUsername(username);

            if (user == null) {
                throw new UsernameNotFoundException("Username não encontrado.");
            }

            TokenDTO tokenResponse = tokenProvider.createAcessToken(username, user.getRoleNames());
            return ResponseEntity.ok(tokenResponse);
            
        } catch (UsernameNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new BadCredentialsException("Username e/ou senha inválidos.");
        }
    }

    public ResponseEntity<TokenDTO> refreshToken(String username, String refreshToken) {
        var user = repository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("Username não encontrado.");
        }
        TokenDTO tokenResponse = tokenProvider.refreshToken(refreshToken);
        return ResponseEntity.ok(tokenResponse);
    }
    

    private boolean checkUsername(String name){
        return repository.findByUsername(name) != null;
    }
}