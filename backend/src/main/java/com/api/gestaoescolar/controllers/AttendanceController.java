package com.api.gestaoescolar.controllers;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.api.gestaoescolar.dtos.AttendanceDTO;
import com.api.gestaoescolar.services.AttendanceService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.net.URI;

@Tag(
    name = "Gerenciamento de Presenças", 
    description = "Endpoint para operações de registros de presença dos alunos nas aulas"
)
@RestController
@RequestMapping("/api/v1/attendances")
public class AttendanceController {

    private final AttendanceService service;

    public AttendanceController(AttendanceService service) {
        this.service = service;
    }

    @Operation(
        summary = "Listar registros de presença",
        description = "Retorna uma lista paginada de todos os registros de presença cadastrados no sistema"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de presenças retornada com sucesso"),
        @ApiResponse(responseCode = "400", description = "Parâmetros de paginação inválidos")
    })
    @GetMapping
    public ResponseEntity<Page<AttendanceDTO>> findAll(
            @Parameter(description = "Número da página (0-based)", example = "0") 
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            
            @Parameter(description = "Quantidade de itens por página", example = "10") 
            @RequestParam(value = "size", defaultValue = "12") Integer size,
            
            @Parameter(description = "Direção da ordenação (asc/desc)", example = "asc") 
            @RequestParam(value = "direction", defaultValue = "asc") String direction,
            
            @Parameter(description = "Campo para ordenação (id, date)", example = "date") 
            @RequestParam(value = "sort", defaultValue = "id") String sort) {
        
        Sort.Direction sortDirection = "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));
        return ResponseEntity.ok(service.findAll(pageable));
    }

    @Operation(
        summary = "Buscar registro de presença por ID",
        description = "Recupera um registro específico de presença pelo seu ID único"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Registro de presença encontrado"),
        @ApiResponse(responseCode = "404", description = "Registro não encontrado"),
        @ApiResponse(responseCode = "400", description = "ID inválido")
    })
    @GetMapping("/{id}")
    public ResponseEntity<AttendanceDTO> findById(
            @Parameter(description = "ID do registro de presença", example = "1") 
            @PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @Operation(
        summary = "Registrar nova presença",
        description = "Cria um novo registro de presença para um aluno em uma aula específica"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Presença registrada com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos na requisição"),
        @ApiResponse(responseCode = "404", description = "Aluno ou turma não encontrados")
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
        description = "Dados do registro de presença",
        required = true,
        content = @Content(
            schema = @Schema(implementation = AttendanceDTO.class)
        )
    )
    @PostMapping
    public ResponseEntity<AttendanceDTO> create(@RequestBody AttendanceDTO attendanceDTO) {
        AttendanceDTO createdAttendance = service.create(attendanceDTO);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdAttendance.getId())
                .toUri();
        return ResponseEntity.created(uri).body(createdAttendance);
    }

    @Operation(
        summary = "Atualizar registro de presença",
        description = "Atualiza um registro existente de presença (ex: alterar status de presença)"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Presença atualizada com sucesso"),
        @ApiResponse(responseCode = "404", description = "Registro não encontrado"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    @PutMapping("/{id}")
    public ResponseEntity<AttendanceDTO> update(
            @Parameter(description = "ID do registro a ser atualizado", example = "1") 
            @PathVariable Long id, 
            
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Dados atualizados da presença",
                required = true,
                content = @Content(
                    schema = @Schema(implementation = AttendanceDTO.class)
                )
            )
            @RequestBody AttendanceDTO attendanceDTO) {
        return ResponseEntity.ok(service.update(id, attendanceDTO));
    }

    @Operation(
        summary = "Remover registro de presença",
        description = "Exclui permanentemente um registro de presença do sistema"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Presença removida com sucesso"),
        @ApiResponse(responseCode = "404", description = "Registro não encontrado"),
        @ApiResponse(responseCode = "400", description = "ID inválido")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID do registro a ser removido", example = "1") 
            @PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}