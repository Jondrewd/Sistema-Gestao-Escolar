package com.api.gestaoescolar.controllers;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.api.gestaoescolar.dtos.TeacherDTO;
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
    name = "Gerenciamento de Professores", 
    description = "Endpoint para operações específicas de professores do sistema"
)
@RestController
@RequestMapping("/api/v1/teachers")
public class TeacherController {

    private final UserService service;

    public TeacherController(UserService service) {
        this.service = service;
    }

    @Operation(
        summary = "Listar professores com paginação",
        description = "Retorna uma lista paginada de todos os professores cadastrados no sistema."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de professores retornada com sucesso"),
        @ApiResponse(responseCode = "400", description = "Parâmetros de paginação inválidos")
    })
    @GetMapping
    public ResponseEntity<Page<TeacherDTO>> findAll(
            @Parameter(description = "Número da página (0-based)", example = "0") 
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            
            @Parameter(description = "Quantidade de itens por página", example = "10") 
            @RequestParam(value = "size", defaultValue = "12") Integer size,
            
            @Parameter(description = "Direção da ordenação (asc/desc)", example = "asc") 
            @RequestParam(value = "direction", defaultValue = "asc") String direction,
            
            @Parameter(description = "Campo para ordenação", example = "cpf") 
            @RequestParam(value = "sort", defaultValue = "cpf") String sort) {
        
        Sort.Direction sortDirection = "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));
        return ResponseEntity.ok(service.findAllTeachers(pageable));
    }

    @Operation(
        summary = "Buscar professores por especialidade",
        description = "Recupera a lista de professores com base na sua especialidade."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Professores encontrados com sucesso"),
            @ApiResponse(responseCode = "404", description = "Nenhum professor encontrado com a especialidade informada"),
            @ApiResponse(responseCode = "400", description = "Especialidade inválida")
    })
    @GetMapping("/speciality/{speciality}")
    public ResponseEntity<Page<TeacherDTO>> findBySpeciality(
            @Parameter(description = "Especialidade do professor", example = "Matemática") 
            @PathVariable String speciality,
            Pageable pageable) {
        Page<TeacherDTO> teachers = service.findBySpeciality(speciality, pageable);
        return ResponseEntity.ok(teachers);
    }
    @Operation(
        summary = "Buscar professor por Cpf",
        description = "Recupera os detalhes de um professor específico com base no seu Cpf."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Estudante encontrado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Estudante não encontrado"),
        @ApiResponse(responseCode = "400", description = "Cpf inválido")
    })
    @GetMapping("/{cpf}")
    public ResponseEntity<TeacherDTO> findByCpf(
            @Parameter(description = "Cpf do professor (apenas números)", example = "123.456.789-01") 
            @PathVariable String cpf) {
        return ResponseEntity.ok(service.findTeacherByCpf(cpf));
    }

    @Operation(
        summary = "Criar novo professor",
        description = "Cadastra um novo professor no sistema com os dados fornecidos."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Professor criado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados do professor inválidos"),
        @ApiResponse(responseCode = "409", description = "Professor já existe")
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
        description = "Dados do novo professor",
        required = true,
        content = @Content(
            schema = @Schema(implementation = TeacherDTO.class)
        )
    )
    @PostMapping
    public ResponseEntity<TeacherDTO> insert(@RequestBody UserDTO teacher) {
        TeacherDTO createdTeacher = service.createTeacher(teacher);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{cpf}")
                .buildAndExpand(createdTeacher.getCpf())
                .toUri();
        return ResponseEntity.created(uri).body(createdTeacher);
    }

    @Operation(
        summary = "Atualizar professor",
        description = "Atualiza os dados de um professor existente com base no CPF fornecido."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Professor atualizado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Professor não encontrado"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    @PutMapping("/{cpf}")
    public ResponseEntity<TeacherDTO> update(
            @Parameter(description = "CPF do professor a ser atualizado", example = "98765432109") 
            @PathVariable String cpf, 
            
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Dados atualizados do professor",
                required = true,
                content = @Content(
                    schema = @Schema(implementation = TeacherDTO.class)
                )
            )
            @RequestBody TeacherDTO teacher) {
        return ResponseEntity.ok(service.updateTeacher(cpf, teacher));
    }

    @Operation(
        summary = "Remover professor",
        description = "Remove permanentemente um professor do sistema com base no CPF fornecido."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Professor removido com sucesso"),
        @ApiResponse(responseCode = "404", description = "Professor não encontrado"),
        @ApiResponse(responseCode = "400", description = "CPF inválido")
    })
    @DeleteMapping("/{cpf}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "CPF do professor a ser removido", example = "98765432109") 
            @PathVariable String cpf) {
        service.deleteByCpf(cpf);
        return ResponseEntity.noContent().build();
    }
}