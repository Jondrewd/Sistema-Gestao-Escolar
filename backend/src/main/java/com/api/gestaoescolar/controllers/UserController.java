package com.api.gestaoescolar.controllers;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.api.gestaoescolar.dtos.UserDTO;
import com.api.gestaoescolar.services.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "Users", description = "API para gerenciamento de usuários - Acesso restrito a administradores")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('ADMIN')") 
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Listar todos os usuários", description = "Retorna uma lista paginada de todos os usuários")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuários listados com sucesso"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor", content = @Content)
    })
    @GetMapping
    public ResponseEntity<Page<UserDTO>> findAll(
            @Parameter(description = "Parâmetros de paginação (page, size, sort)")
            @PageableDefault(sort = "id") Pageable pageable) {
        return ResponseEntity.ok(userService.findAll(pageable));
    }

    @Operation(summary = "Buscar usuário por ID", description = "Retorna um usuário específico pelo seu ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuário encontrado"),
        @ApiResponse(responseCode = "404", description = "Usuário não encontrado", content = @Content),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> findById(
            @Parameter(description = "ID do usuário a ser buscado", required = true)
            @PathVariable Long id) {
        return ResponseEntity.ok(userService.findById(id));
    }

    @Operation(summary = "Buscar usuário por CPF", description = "Retorna um usuário específico pelo seu CPF")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuário encontrado"),
        @ApiResponse(responseCode = "404", description = "Usuário não encontrado", content = @Content),
        @ApiResponse(responseCode = "400", description = "CPF inválido", content = @Content),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor", content = @Content)
    })
    @GetMapping("/cpf/{cpf}")
    public ResponseEntity<UserDTO> findByCpf(
            @Parameter(description = "CPF do usuário a ser buscado", required = true)
            @PathVariable String cpf) {
        return ResponseEntity.ok(userService.findByCpf(cpf));
    }

    @Operation(summary = "Criar novo usuário", description = "Cria um novo usuário no sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Usuário criado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos", content = @Content),
        @ApiResponse(responseCode = "409", description = "Conflito (usuário já existe)", content = @Content),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor", content = @Content)
    })
    @PostMapping
    public ResponseEntity<UserDTO> create(
            @Parameter(description = "Dados do usuário a ser criado", required = true)
            @Valid @RequestBody UserDTO userDTO) {
        UserDTO createdUser = userService.create(userDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    @Operation(summary = "Atualizar usuário", description = "Atualiza os dados de um usuário existente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuário atualizado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos", content = @Content),
        @ApiResponse(responseCode = "404", description = "Usuário não encontrado", content = @Content),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> update(
            @Parameter(description = "ID do usuário a ser atualizado", required = true)
            @PathVariable Long id,
            @Parameter(description = "Dados atualizados do usuário", required = true)
            @Valid @RequestBody UserDTO userDTO) {
        return ResponseEntity.ok(userService.update(id, userDTO));
    }

    @Operation(summary = "Listar usuários por tipo", description = "Retorna usuários filtrados por tipo (ex: STUDENT, TEACHER)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuários listados com sucesso"),
        @ApiResponse(responseCode = "400", description = "Tipo de usuário inválido", content = @Content),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor", content = @Content)
    })
    @GetMapping("/type/{userType}")
    public ResponseEntity<Page<UserDTO>> getUsersByType(
            @Parameter(description = "Tipo do usuário (ex: STUDENT, TEACHER)", required = true)
            @PathVariable String userType,
            @Parameter(description = "Parâmetros de paginação (page, size, sort)")
            @PageableDefault(sort = "id") Pageable pageable) {
        return ResponseEntity.ok(userService.getUsersByType(userType, pageable));
    }

    @Operation(summary = "Deletar usuário por ID", description = "Remove um usuário do sistema pelo seu ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Usuário deletado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Usuário não encontrado", content = @Content),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID do usuário a ser removido", required = true)
            @PathVariable Long id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Deletar usuário por CPF", description = "Remove um usuário do sistema pelo seu CPF")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Usuário deletado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Usuário não encontrado", content = @Content),
        @ApiResponse(responseCode = "400", description = "CPF inválido", content = @Content),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor", content = @Content)
    })
    @DeleteMapping("/cpf/{cpf}")
    public ResponseEntity<Void> deleteByCpf(
            @Parameter(description = "CPF do usuário a ser removido", required = true)
            @PathVariable String cpf) {
        userService.deleteByCpf(cpf);
        return ResponseEntity.noContent().build();
    }
}
