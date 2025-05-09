package com.api.gestaoescolar.controllers;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.api.gestaoescolar.dtos.UserDTO;
import com.api.gestaoescolar.services.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.net.URI;

@Tag(
    name = "Gerenciamento de Estudantes", 
    description = "Endpoint para operações específicas de estudantes do sistema"
)
@RestController
@RequestMapping("/api/v1/students")
public class StudentController {

    private final UserService service;

    public StudentController(UserService service) {
        this.service = service;
    }

    @Operation(
        summary = "Listar estudantes com paginação",
        description = "Retorna uma lista paginada de todos os estudantes cadastrados no sistema."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de estudantes retornada com sucesso"),
        @ApiResponse(responseCode = "400", description = "Parâmetros de paginação inválidos")
    })
    @GetMapping
    public ResponseEntity<Page<UserDTO>> findAll(
            @Parameter(description = "Número da página (0-based)", example = "0") 
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            
            @Parameter(description = "Quantidade de itens por página", example = "10") 
            @RequestParam(value = "size", defaultValue = "12") Integer size,
            
            @Parameter(description = "Direção da ordenação (asc/desc)", example = "asc") 
            @RequestParam(value = "direction", defaultValue = "asc") String direction,
            
            @Parameter(description = "Campo para ordenação", example = "username") 
            @RequestParam(value = "sort", defaultValue = "username") String sort) {
        
        Sort.Direction sortDirection = "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));
        return ResponseEntity.ok(service.findAllStudents(pageable));
    }

    @Operation(
        summary = "Buscar estudante por CPF",
        description = "Recupera os detalhes de um estudante específico com base no seu CPF."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Estudante encontrado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Estudante não encontrado"),
        @ApiResponse(responseCode = "400", description = "CPF inválido")
    })
    @GetMapping("/{cpf}")
    public ResponseEntity<UserDTO> findByCpf(
            @Parameter(description = "CPF do estudante (apenas números)", example = "12345678901") 
            @PathVariable String cpf) {
        return ResponseEntity.ok(service.findByCpf(cpf));
    }

    @Operation(
        summary = "Criar novo estudante",
        description = "Cadastra um novo estudante no sistema com os dados fornecidos."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Estudante criado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados do estudante inválidos"),
        @ApiResponse(responseCode = "409", description = "Estudante já existe")
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
        description = "Dados do novo estudante",
        required = true,
        content = @Content(
            schema = @Schema(implementation = UserDTO.class)
        )
    )
    @PostMapping
    public ResponseEntity<UserDTO> insert(@RequestBody UserDTO student) {
        UserDTO createdUser = service.create(student);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{cpf}")
                .buildAndExpand(createdUser.getCpf())
                .toUri();
        return ResponseEntity.created(uri).body(createdUser);
    }

    @Operation(
        summary = "Atualizar estudante",
        description = "Atualiza os dados de um estudante existente com base no CPF fornecido."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Estudante atualizado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Estudante não encontrado"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    @PutMapping("/{cpf}")
    public ResponseEntity<UserDTO> update(
            @Parameter(description = "CPF do estudante a ser atualizado", example = "12345678901") 
            @PathVariable String cpf, 
            
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Dados atualizados do estudante",
                required = true,
                content = @Content(
                    schema = @Schema(implementation = UserDTO.class)
                )
            )
            @RequestBody UserDTO student) {
        return ResponseEntity.ok(service.updateStudent(cpf, student));
    }

    @Operation(
        summary = "Remover estudante",
        description = "Remove permanentemente um estudante do sistema com base no CPF fornecido."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Estudante removido com sucesso"),
        @ApiResponse(responseCode = "404", description = "Estudante não encontrado"),
        @ApiResponse(responseCode = "400", description = "CPF inválido")
    })
    @DeleteMapping("/{cpf}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "CPF do estudante a ser removido", example = "12345678901") 
            @PathVariable String cpf) {
        service.deleteByCpf(cpf);
        return ResponseEntity.noContent().build();
    }
}