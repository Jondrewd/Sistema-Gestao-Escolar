package com.api.gestaoescolar.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.api.gestaoescolar.dtos.AttendanceDTO;
import com.api.gestaoescolar.services.AttendanceService;

import java.net.URI;

@Tag(
    name = "Gerenciamento de Presenças", 
    description = "Endpoint para operações de registros de presença"
)
@RestController
@RequestMapping("/api/v1/attendances")
@SecurityRequirement(name = "bearerAuth")
public class AttendanceController {

    private final AttendanceService service;

    public AttendanceController(AttendanceService service) {
        this.service = service;
    }

    @Operation(
        summary = "Listar registros de presença",
        description = "Retorna todos os registros de presença (ADMIN/TEACHER)"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso"),
        @ApiResponse(responseCode = "403", description = "Acesso negado"),
        @ApiResponse(responseCode = "400", description = "Parâmetros inválidos")
    })
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<Page<AttendanceDTO>> findAll(
            @Parameter(description = "Número da página (0-based)", example = "0") 
            @RequestParam(defaultValue = "0") int page,
            
            @Parameter(description = "Itens por página", example = "10") 
            @RequestParam(defaultValue = "10") int size,
            
            @Parameter(description = "Campo para ordenação", example = "date") 
            @RequestParam(defaultValue = "date") String sort,
            
            @Parameter(description = "Direção (asc/desc)", example = "asc") 
            @RequestParam(defaultValue = "asc") String direction) {
        
        Sort.Direction sortDirection = direction.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));
        return ResponseEntity.ok(service.findAll(pageable));
    }

    @Operation(
        summary = "Buscar registro por ID",
        description = "Recupera um registro específico (ADMIN/TEACHER)"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Registro encontrado"),
        @ApiResponse(responseCode = "403", description = "Acesso negado"),
        @ApiResponse(responseCode = "404", description = "Registro não encontrado")
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<AttendanceDTO> findById(
            @Parameter(description = "ID do registro", example = "1") 
            @PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @Operation(
        summary = "Registrar nova presença",
        description = "Cria um novo registro (ADMIN/TEACHER)"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Presença registrada"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @ApiResponse(responseCode = "403", description = "Acesso negado"),
        @ApiResponse(responseCode = "404", description = "Aluno ou turma não encontrados")
    })
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<AttendanceDTO> create(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Dados da presença",
                required = true,
                content = @Content(schema = @Schema(implementation = AttendanceDTO.class)))
            @Valid @RequestBody AttendanceDTO attendanceDTO) {
        
        AttendanceDTO createdAttendance = service.create(attendanceDTO);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdAttendance.getId())
                .toUri();
        return ResponseEntity.created(uri).body(createdAttendance);
    }

    @Operation(
        summary = "Atualizar registro",
        description = "Edita um registro existente (ADMIN/TEACHER)"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Registro atualizado"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @ApiResponse(responseCode = "403", description = "Acesso negado"),
        @ApiResponse(responseCode = "404", description = "Registro não encontrado")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<AttendanceDTO> update(
            @Parameter(description = "ID do registro", example = "1") 
            @PathVariable Long id,
            @Valid @RequestBody AttendanceDTO attendanceDTO) {
        return ResponseEntity.ok(service.update(id, attendanceDTO));
    }

    @Operation(
        summary = "Remover registro",
        description = "Exclui um registro (ADMIN only)"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Registro removido"),
        @ApiResponse(responseCode = "403", description = "Acesso negado"),
        @ApiResponse(responseCode = "404", description = "Registro não encontrado")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID do registro", example = "1") 
            @PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
        summary = "Buscar presenças por aluno",
        description = "Recupera as presenças de um estudante (ADMIN/TEACHER ou próprio aluno)"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Presenças encontradas"),
        @ApiResponse(responseCode = "403", description = "Acesso negado"),
        @ApiResponse(responseCode = "404", description = "Estudante não encontrado")
    })
    @GetMapping("/student/{cpf}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER') or (hasRole('STUDENT') and #cpf == principal.username)")
    public ResponseEntity<Page<AttendanceDTO>> findAttendancesByStudent(
            @Parameter(description = "Número da página (0-based)", example = "0") 
            @RequestParam(defaultValue = "0") int page,
            
            @Parameter(description = "Itens por página", example = "10") 
            @RequestParam(defaultValue = "10") int size,
            
            @Parameter(description = "Campo para ordenação", example = "date") 
            @RequestParam(defaultValue = "date") String sort,
            
            @Parameter(description = "Direção (asc/desc)", example = "asc") 
            @RequestParam(defaultValue = "asc") String direction,
            
            @Parameter(description = "CPF do estudante", example = "12345678901") 
            @PathVariable String cpf) {

        Sort.Direction sortDirection = direction.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));
        return ResponseEntity.ok(service.findByStudentCpf(cpf, pageable));
    }
}