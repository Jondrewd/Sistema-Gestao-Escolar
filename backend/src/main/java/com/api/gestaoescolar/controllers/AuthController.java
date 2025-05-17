package com.api.gestaoescolar.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.api.gestaoescolar.dtos.AccountCredentialsDTO;
import com.api.gestaoescolar.dtos.RegisterStudentDTO;
import com.api.gestaoescolar.dtos.RegisterTeacherDTO;
import com.api.gestaoescolar.dtos.TokenDTO;
import com.api.gestaoescolar.services.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name="Endpoint de Autenticação")
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(summary= "Registra um Estudante")
    @PostMapping("/register/student")
    public ResponseEntity<String> registerStudent(@RequestBody RegisterStudentDTO data){
        if(checkParamIsNotNull(data))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid client request.");
        authService.registerStudent(data);
        return ResponseEntity.ok("Aluno Registrado.");
    }

    @Operation(summary= "Registra um Professor")
    @PostMapping("/register/teacher")
    public ResponseEntity<String> registerTeacher(@RequestBody RegisterTeacherDTO data){
        if(checkParamIsNotNull(data))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid client request.");
        authService.registerTeacher(data);
        return ResponseEntity.ok("Professor Registrado.");
    }

    @Operation(summary = "Realiza login de um usuário")
    @PostMapping("/login")
    public ResponseEntity<TokenDTO> login(@RequestBody AccountCredentialsDTO data) {
        if (data == null || data.getCpf() == null || data.getPassword() == null) {
            return ResponseEntity.badRequest().build();
        }

        TokenDTO token = authService.signin(data).getBody();
        return ResponseEntity.ok(token);
    }

    @Operation(summary = "Atualiza o Token do usuario.")
    @PutMapping(value = "/refresh/{cpf}")
    public ResponseEntity refreshToken(@PathVariable("cpf") String cpf,
    @RequestHeader("Authorization") String refreshToken){
        if (checkParamIsNotNull(cpf, refreshToken)) 
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid client request.");
        var token = authService.refreshToken(cpf, refreshToken);
        if (token == null) 
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid client request.");
        return token;
    }

    private boolean checkParamIsNotNull(String cpf, String refreshToken){
        return refreshToken == null || refreshToken.isBlank() || cpf == null || cpf.isBlank();
    }
    
    private boolean checkParamIsNotNull(RegisterStudentDTO data){
        return data == null || data.getCpf() == null || data.getCpf().isBlank() ||
        data.getPassword() == null || data.getPassword().isBlank();
    }
    private boolean checkParamIsNotNull(RegisterTeacherDTO data){
        return data == null || data.getCpf() == null || data.getCpf().isBlank() ||
        data.getPassword() == null || data.getPassword().isBlank();
    }
}
