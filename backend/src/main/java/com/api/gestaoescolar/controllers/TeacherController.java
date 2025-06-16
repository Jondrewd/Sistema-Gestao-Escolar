package com.api.gestaoescolar.controllers;

import java.net.URI;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
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
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "Gerenciamento de Professores", description = "Endpoint para operações relacionadas a professores")
@RestController
@RequestMapping("/api/v1/teachers")
@SecurityRequirement(name = "bearerAuth")
public class TeacherController {

    private final UserService userService;

    public TeacherController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Listar professores paginados", description = "Retorna todos os professores com paginação. Acesso: ADMIN ou TEACHER")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Professores listados com sucesso"),
        @ApiResponse(responseCode = "403", description = "Acesso negado", content = @Content)
    })
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<Page<TeacherDTO>> findAll(
            @Parameter(description = "Número da página (0-based)", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Itens por página", example = "10") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Campo de ordenação", example = "name") @RequestParam(defaultValue = "name") String sort) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(sort));
        return ResponseEntity.ok(userService.findAllTeachers(pageable));
    }

    @Operation(summary = "Buscar professores por especialidade", description = "Filtra professores por área de atuação. Acesso: ADMIN ou TEACHER")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Professores encontrados"),
        @ApiResponse(responseCode = "404", description = "Nenhum professor encontrado", content = @Content)
    })
    @GetMapping("/speciality/{speciality}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<Page<TeacherDTO>> findBySpeciality(
            @Parameter(description = "Especialidade (ex: Matemática)", required = true) @PathVariable String speciality,
            Pageable pageable) {

        return ResponseEntity.ok(userService.findBySpeciality(speciality, pageable));
    }

    @Operation(summary = "Buscar professor por CPF", description = "Recupera um professor específico. Acesso: ADMIN ou o próprio professor")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Professor encontrado"),
        @ApiResponse(responseCode = "404", description = "Professor não encontrado", content = @Content)
    })
    @GetMapping("/{cpf}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('TEACHER') and #cpf == principal.username)")
    public ResponseEntity<TeacherDTO> findByCpf(
            @Parameter(description = "CPF do professor (somente números)", example = "12345678901") @PathVariable String cpf) {

        return ResponseEntity.ok(userService.findTeacherByCpf(cpf));
    }

    @Operation(summary = "Criar professor", description = "Cadastra um novo professor. Acesso exclusivo para ADMIN")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Professor criado com sucesso"),
        @ApiResponse(responseCode = "409", description = "CPF já cadastrado", content = @Content)
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TeacherDTO> create(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Dados do professor",
                required = true,
                content = @Content(schema = @Schema(implementation = TeacherDTO.class))
            ) @Valid @RequestBody UserDTO teacherDTO) {

        TeacherDTO createdTeacher = userService.createTeacher(teacherDTO);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{cpf}")
                .buildAndExpand(createdTeacher.getCpf())
                .toUri();
        return ResponseEntity.created(uri).body(createdTeacher);
    }

    @Operation(summary = "Atualizar professor", description = "Atualiza dados do professor. Acesso: ADMIN ou o próprio professor")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Professor atualizado"),
        @ApiResponse(responseCode = "404", description = "Professor não encontrado", content = @Content)
    })
    @PutMapping("/{cpf}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('TEACHER') and #cpf == principal.username)")
    public ResponseEntity<TeacherDTO> update(
            @Parameter(description = "CPF do professor", example = "12345678901") @PathVariable String cpf,
            @Valid @RequestBody TeacherDTO teacherDTO) {

        return ResponseEntity.ok(userService.updateTeacher(cpf, teacherDTO));
    }

    @Operation(summary = "Remover professor", description = "Remove um professor do sistema. Acesso exclusivo para ADMIN")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Professor removido"),
        @ApiResponse(responseCode = "404", description = "Professor não encontrado", content = @Content)
    })
    @DeleteMapping("/{cpf}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(
            @Parameter(description = "CPF do professor", example = "12345678901") @PathVariable String cpf) {

        userService.deleteByCpf(cpf);
        return ResponseEntity.noContent().build();
    }
}
