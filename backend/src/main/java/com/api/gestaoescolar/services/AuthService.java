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
    if (checkCpf(data.getCpf())) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body("Já existe um usuário cadastrado com esse nome de usuário.");
    }
    User newUser;
    Roles roles = roleRepository.findByName("ROLE_USER").get();
            Teacher teacher = new Teacher();
            teacher.setCpf(data.getCpf());
            teacher.setFullName(data.getFullName());
            teacher.setEmail(data.getEmail());
            teacher.setPassword(config.passwordEncoder().encode(data.getPassword())); 
            teacher.setSpeciality(data.getSpeciality());
            teacher.setRoles(Collections.singletonList(roles));
            newUser = teacher;

        repository.save(newUser); 

    return ResponseEntity.status(HttpStatus.CREATED)
            .body("Usuário registrado com sucesso.");
}

    public ResponseEntity<String> registerStudent(RegisterStudentDTO data) {
        if (checkCpf(data.getCpf())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Já existe um usuário cadastrado com esse nome de CPF.");
        }
        User newUser;
        Roles roles = roleRepository.findByName("ROLE_USER").get();
                Student student = new Student();
                student.setCpf(data.getCpf());
                student.setFullName(data.getFullName());
                student.setEmail(data.getEmail());
                student.setRoles(Collections.singletonList(roles));
                student.setRegistrationNumber(data.getRegistrationNumber());
                student.setPassword(config.passwordEncoder().encode(data.getPassword())); 
                newUser = student;
        repository.save(newUser); 
        System.out.println(newUser);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body("Usuário registrado com sucesso.");
    }

    public ResponseEntity<TokenDTO> signin(AccountCredentialsDTO data) {
        try {
            String cpf = data.getCpf();
            String password = data.getPassword();

            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(cpf, password));

            var user = repository.findByCpf(cpf);
            if (user == null) {
                throw new UsernameNotFoundException("Cpf não encontrado.");
            }
            String userType = isTeacherByCpf(cpf) ? "TEACHER" : "STUDENT";

            TokenDTO tokenResponse = tokenProvider.createAcessToken(cpf, user.get().getRoleNames());
            tokenResponse.setUserType(userType);
            return ResponseEntity.ok(tokenResponse);
            
        } catch (UsernameNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new BadCredentialsException("Cpf e/ou senha inválidos.");
        }
    }

    public ResponseEntity<TokenDTO> refreshToken(String cpf, String refreshToken) {
        var user = repository.findByCpf(cpf);
        if (user == null) {
            throw new UsernameNotFoundException("Username não encontrado.");
        }
        TokenDTO tokenResponse = tokenProvider.refreshToken(refreshToken);
        return ResponseEntity.ok(tokenResponse);
    }
    

    private boolean checkCpf(String cpf){
        return repository.existsByCpf(cpf);
    }

    private boolean isTeacherByCpf(String cpf) {
    return repository.findTeacherByCpf(cpf).isPresent();
}

}