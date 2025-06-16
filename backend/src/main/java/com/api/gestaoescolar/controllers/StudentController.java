package com.api.gestaoescolar.controllers;

import java.net.URI;
import java.util.List;

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

import com.api.gestaoescolar.dtos.ClassScheduleDTO;
import com.api.gestaoescolar.dtos.StudentDTO;
import com.api.gestaoescolar.dtos.UserDTO;
import com.api.gestaoescolar.services.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

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

    @Operation(summary = "Listar estudantes com paginação", description = "Retorna todos os estudantes com paginação. Acesso: ADMIN ou TEACHER")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de estudantes retornada com sucesso"),
        @ApiResponse(responseCode = "400", description = "Parâmetros de paginação inválidos")
    })
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<Page<StudentDTO>> findAll(
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
        return ResponseEntity.ok(service.findAllStudents(pageable));
    }

    @Operation(summary = "Buscar estudante por CPF", description = "Recupera um estudante. Acesso: ADMIN ou o próprio STUDENT")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Estudante encontrado"),
        @ApiResponse(responseCode = "404", description = "Estudante não encontrado"),
        @ApiResponse(responseCode = "400", description = "Cpf inválido")
    })
    @GetMapping("/{cpf}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('STUDENT') and #cpf == principal.username)")
    public ResponseEntity<StudentDTO> findByCpf(
            @Parameter(description = "Cpf do estudante", example = "12345678901") 
            @PathVariable String cpf) {
        return ResponseEntity.ok(service.findStudentByCpf(cpf));
    }

    @Operation(summary = "Buscar estudante por Número de Registro", description = "Recupera um estudante por número de registro. Acesso: ADMIN ou TEACHER")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Estudante encontrado"),
        @ApiResponse(responseCode = "404", description = "Estudante não encontrado"),
        @ApiResponse(responseCode = "400", description = "Número de registro inválido")
    })
    @GetMapping("/rn/{registrationNumber}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<StudentDTO> findByRegisterNumber(
            @Parameter(description = "Número de registro do estudante", example = "12345678901") 
            @PathVariable String registrationNumber) {
        return ResponseEntity.ok(service.findByRegistrationNumber(registrationNumber));
    }

    @Operation(summary = "Criar novo estudante", description = "Cadastro de estudante. Acesso: Apenas ADMIN")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Estudante criado"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @ApiResponse(responseCode = "409", description = "Estudante já existe")
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StudentDTO> insert(@RequestBody UserDTO student) {
        StudentDTO createdUser = service.createStudent(student);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{cpf}")
                .buildAndExpand(createdUser.getCpf())
                .toUri();
        return ResponseEntity.created(uri).body(createdUser);
    }

    @Operation(summary = "Atualizar estudante", description = "Atualiza um estudante existente. Acesso: ADMIN ou o próprio STUDENT")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Estudante atualizado"),
        @ApiResponse(responseCode = "404", description = "Estudante não encontrado"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    @PutMapping("/{cpf}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('STUDENT') and #cpf == principal.username)")
    public ResponseEntity<StudentDTO> update(
            @Parameter(description = "CPF do estudante", example = "12345678901") 
            @PathVariable String cpf, 
            @RequestBody StudentDTO student) {
        return ResponseEntity.ok(service.updateStudent(cpf, student));
    }

    @Operation(summary = "Remover estudante", description = "Exclui permanentemente um estudante. Acesso: Apenas ADMIN")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Estudante removido"),
        @ApiResponse(responseCode = "404", description = "Estudante não encontrado"),
        @ApiResponse(responseCode = "400", description = "CPF inválido")
    })
    @DeleteMapping("/{cpf}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(
            @Parameter(description = "CPF do estudante", example = "12345678901") 
            @PathVariable String cpf) {
        service.deleteByCpf(cpf);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Obter horários das aulas do estudante", description = "Lista de disciplinas com os horários de aula. Acesso: ADMIN, TEACHER ou o próprio STUDENT")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Horários encontrados"),
        @ApiResponse(responseCode = "404", description = "Estudante não encontrado"),
        @ApiResponse(responseCode = "400", description = "CPF inválido")
    })
    @GetMapping("/{cpf}/schedule")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER') or (hasRole('STUDENT') and #cpf == principal.username)")
    public ResponseEntity<List<ClassScheduleDTO>> getStudentSchedule(
            @Parameter(description = "CPF do estudante", example = "12345678901") 
            @PathVariable String cpf) {
        return ResponseEntity.ok(service.getClassScheduleForStudent(cpf));
    }
}
