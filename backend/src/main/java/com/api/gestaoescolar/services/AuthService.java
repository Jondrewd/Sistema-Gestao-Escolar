package com.api.gestaoescolar.services;

import com.api.gestaoescolar.config.SecurityConfig;
import com.api.gestaoescolar.dtos.AccountCredentialsDTO;
import com.api.gestaoescolar.dtos.RegisterStudentDTO;
import com.api.gestaoescolar.dtos.RegisterTeacherDTO;
import com.api.gestaoescolar.dtos.TokenDTO;
import com.api.gestaoescolar.dtos.VerifyEmailDTO;
import com.api.gestaoescolar.entities.Roles;
import com.api.gestaoescolar.entities.Student;
import com.api.gestaoescolar.entities.Teacher;
import com.api.gestaoescolar.entities.User;
import com.api.gestaoescolar.exceptions.ResourceNotFoundException;
import com.api.gestaoescolar.repositories.RolesRepository;
import com.api.gestaoescolar.repositories.UserRepository;
import com.api.gestaoescolar.security.jwt.JwtTokenProvider;

import java.time.Duration;
import java.time.Instant;
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
    private final EmailService emailService;

    
    public AuthService(AuthenticationManager authenticationManager, JwtTokenProvider tokenProvider,
            UserRepository repository, RolesRepository roleRepository, EmailService emailService) {
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
        this.repository = repository;
        this.roleRepository = roleRepository;
        this.emailService = emailService;
    }
    
    @Autowired
    private SecurityConfig config;

   public ResponseEntity<String> registerTeacher(RegisterTeacherDTO data) {
    if (checkCpf(data.getCpf())) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body("Já existe um usuário cadastrado com esse nome de usuário.");
    }
    String verificationCode = emailService.generateVerificationCode();
    User newUser;
    Roles roles = roleRepository.findByName("ROLE_TEACHER").get();
        Teacher teacher = new Teacher();
        teacher.setCpf(data.getCpf());
        teacher.setFullName(data.getFullName());
        teacher.setEmail(data.getEmail());
        teacher.setPassword(config.passwordEncoder().encode(data.getPassword())); 
        teacher.setSpeciality(data.getSpeciality());
        teacher.setRoles(Collections.singletonList(roles));
        teacher.setVerificationCode(verificationCode);
        teacher.setVerificationCodeExpiration(Instant.now().plus(Duration.ofMinutes(15)));
        newUser = teacher;
        repository.save(newUser); 

        emailService.sendVerificationEmail(teacher.getEmail(), verificationCode);

    return ResponseEntity.status(HttpStatus.CREATED)
            .body("Usuário registrado com sucesso.");
}

    public ResponseEntity<String> registerStudent(RegisterStudentDTO data) {
        if (checkCpf(data.getCpf())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Já existe um usuário cadastrado com esse nome de CPF.");
        }
        String verificationCode = emailService.generateVerificationCode();
        User newUser;
        Roles roles = roleRepository.findByName("ROLE_STUDENT").get();
                Student student = new Student();
                student.setCpf(data.getCpf());
                student.setFullName(data.getFullName());
                student.setEmail(data.getEmail());
                student.setRoles(Collections.singletonList(roles));
                student.setRegistrationNumber(data.getRegistrationNumber());
                student.setPassword(config.passwordEncoder().encode(data.getPassword())); 
                student.setVerificationCode(verificationCode);
                student.setVerificationCodeExpiration(Instant.now().plus(Duration.ofMinutes(15)));
                newUser = student;
        repository.save(newUser); 
        emailService.sendVerificationEmail(student.getEmail(), verificationCode);

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
            String userType;

            if (isStundentByCpf(cpf)) {
                userType = "STUDENT";
            } else if (isTeacherByCpf(cpf)) {
                userType = "TEACHER";
            } else {
                userType = "ADMIN";
            }

            TokenDTO tokenResponse = tokenProvider.createAcessToken(cpf, user.get().getRoleNames());
            tokenResponse.setUserType(userType);
            return ResponseEntity.ok(tokenResponse);
            
        } catch (UsernameNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new BadCredentialsException("Cpf e/ou senha inválidos.");
        }
    }
    
    public String verifyEmail(VerifyEmailDTO verifyEmailDto) {
        User user = repository.findByEmail(verifyEmailDto.getEmail())
            .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com esse e-mail"));

        if (user.isEnabled()) {
            throw new BadCredentialsException("Usúario já verificado.");
        }

        if (!verifyEmailDto.getCode().equals(user.getVerificationCode())) {
            throw new BadCredentialsException("Código inválido.");
        }

        if (user.getVerificationCodeExpiration().isBefore(Instant.now())) {
            throw new BadCredentialsException("Código expirado.");
        }

        user.setEnabled(true);
        user.setVerificationCode(null);
        user.setVerificationCodeExpiration(null);
        repository.save(user);

        return "E-mail verificado com sucesso!";
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
    private boolean isStundentByCpf(String cpf) {
        return repository.findStudentByCpf(cpf).isPresent();
    }

}