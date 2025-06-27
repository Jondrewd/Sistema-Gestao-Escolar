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
import com.api.gestaoescolar.dtos.VerifyEmailDTO;
import com.api.gestaoescolar.services.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "Endpoint de Autenticação")
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }
    
    @Operation(
        summary = "Registra um Estudante",
        description = "Cria um novo estudante no sistema. O CPF e e-mail devem ser únicos. Um código de verificação será enviado por e-mail para ativação da conta."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Estudante registrado com sucesso"),
        @ApiResponse(responseCode = "403", description = "Requisição inválida ou parâmetros ausentes")
    })
    @PostMapping("/register/student")
    public ResponseEntity<String> registerStudent(@Valid @RequestBody RegisterStudentDTO data) {
        if (checkParamIsNotNull(data))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid client request.");
        authService.registerStudent(data);
        return ResponseEntity.ok("Aluno Registrado.");
    }

    @Operation(
        summary = "Registra um Professor",
        description = "Cria um novo professor no sistema. O CPF e e-mail devem ser únicos. Um código de verificação será enviado por e-mail para ativação da conta."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Professor registrado com sucesso"),
        @ApiResponse(responseCode = "403", description = "Requisição inválida ou parâmetros ausentes")
    })
    @PostMapping("/register/teacher")
    public ResponseEntity<String> registerTeacher(@Valid @RequestBody RegisterTeacherDTO data) {
        if (checkParamIsNotNull(data))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid client request.");
        authService.registerTeacher(data);
        return ResponseEntity.ok("Professor Registrado.");
    }

    @Operation(
        summary = "Realiza login de um usuário",
        description = "Autentica um usuário (Aluno, Professor ou Admin) com CPF e senha. Retorna um token JWT para autenticação nas próximas requisições."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Login realizado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @ApiResponse(responseCode = "403", description = "Credenciais inválidas")
    })
    @PostMapping("/login")
    public ResponseEntity<TokenDTO> login(@Valid @RequestBody AccountCredentialsDTO data) {
        if (data == null || data.getCpf() == null || data.getPassword() == null) {
            return ResponseEntity.badRequest().build();
        }
        TokenDTO token = authService.signin(data).getBody();
        return ResponseEntity.ok(token);
    }

    @Operation(
        summary = "Verificar e-mail do usuário",
        description = "Confirma o endereço de e-mail de um usuário usando um código enviado previamente. O e-mail precisa ter sido cadastrado e ainda não verificado."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "E-mail verificado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Código inválido, expirado ou e-mail já verificado"),
        @ApiResponse(responseCode = "404", description = "Usuário não encontrado com esse e-mail")
    })
    @PostMapping("/verify-email")
    public ResponseEntity<String> verifyEmail(@RequestBody VerifyEmailDTO verifyEmailDTO) {
        if (verifyEmailDTO.getEmail() == null || verifyEmailDTO.getCode() == null) {
            return ResponseEntity.badRequest().build();
        }
        authService.verifyEmail(verifyEmailDTO);
        return ResponseEntity.ok("Email verificado.");
    }

    @Operation(
        summary = "Atualiza o token do usuário",
        description = "Gera um novo token de acesso a partir de um refresh token válido. O CPF e o token devem ser válidos e correspondentes."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Token atualizado com sucesso"),
        @ApiResponse(responseCode = "403", description = "Requisição inválida ou refresh token inválido")
    })
    @PutMapping(value = "/refresh/{cpf}")
    public ResponseEntity<?> refreshToken(
            @PathVariable("cpf") String cpf,
            @RequestHeader("Authorization") String refreshToken) {
        if (checkParamIsNotNull(cpf, refreshToken))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid client request.");
        var token = authService.refreshToken(cpf, refreshToken);
        if (token == null)
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid client request.");
        return token;
    }

    private boolean checkParamIsNotNull(String cpf, String refreshToken) {
        return refreshToken == null || refreshToken.isBlank() || cpf == null || cpf.isBlank();
    }

    private boolean checkParamIsNotNull(RegisterStudentDTO data) {
        return data == null || data.getCpf() == null || data.getCpf().isBlank() ||
                data.getPassword() == null || data.getPassword().isBlank();
    }

    private boolean checkParamIsNotNull(RegisterTeacherDTO data) {
        return data == null || data.getCpf() == null || data.getCpf().isBlank() ||
                data.getPassword() == null || data.getPassword().isBlank();
    }
}
