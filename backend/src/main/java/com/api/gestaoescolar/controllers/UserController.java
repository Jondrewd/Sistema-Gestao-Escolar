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
    name = "Gerenciamento de Usuários", 
    description = "Endpoint para operações de usuários do sistema, incluindo estudantes e professores"
)
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @Operation(
        summary = "Listar usuários com paginação",
        description = "Retorna uma lista paginada de todos os usuários cadastrados no sistema. "
                    + "Permite ordenação por diferentes campos e direção (asc/desc)."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de usuários retornada com sucesso"),
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
        return ResponseEntity.ok(service.findAll(pageable));
    }

    @Operation(
        summary = "Buscar usuário por ID",
        description = "Recupera os detalhes de um usuário específico com base no seu ID único."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuário encontrado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Usuário não encontrado"),
        @ApiResponse(responseCode = "400", description = "ID inválido")
    })
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> findById(
            @Parameter(description = "ID do usuário a ser buscado", example = "1") 
            @PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @Operation(
        summary = "Filtrar usuários por tipo",
        description = "Lista paginada de usuários filtrados por tipo (STUDENT ou TEACHER)"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista filtrada retornada com sucesso"),
        @ApiResponse(responseCode = "404", description = "Nenhum usuário encontrado para o tipo especificado"),
        @ApiResponse(responseCode = "400", description = "Tipo de usuário inválido")
    })
    @GetMapping("/type/{userType}")
    public ResponseEntity<Page<UserDTO>> findAllByType(
            @Parameter(description = "Tipo de usuário para filtro", example = "STUDENT") 
            @PathVariable String userType,
            
            @Parameter(description = "Número da página (0-based)", example = "0") 
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            
            @Parameter(description = "Quantidade de itens por página", example = "10") 
            @RequestParam(value = "size", defaultValue = "12") Integer size) {
        
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(service.findAllByType(userType, pageable));
    }

    @Operation(
        summary = "Criar novo usuário",
        description = "Cadastra um novo usuário no sistema com os dados fornecidos."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Usuário criado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados do usuário inválidos"),
        @ApiResponse(responseCode = "409", description = "Usuário já existe")
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
        description = "Dados do novo usuário",
        required = true,
        content = @Content(
            schema = @Schema(implementation = UserDTO.class)
        )
    )
    @PostMapping
    public ResponseEntity<UserDTO> insert(@RequestBody UserDTO user) {
        UserDTO createdUser = service.create(user);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdUser.getId())
                .toUri();
        return ResponseEntity.created(uri).body(createdUser);
    }

    @Operation(
        summary = "Atualizar usuário",
        description = "Atualiza os dados de um usuário existente com base no ID fornecido."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuário atualizado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Usuário não encontrado"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> update(
            @Parameter(description = "ID do usuário a ser atualizado", example = "1") 
            @PathVariable Long id, 
            
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Dados atualizados do usuário",
                required = true,
                content = @Content(
                    schema = @Schema(implementation = UserDTO.class)
                )
            )
            @RequestBody UserDTO user) {
        return ResponseEntity.ok(service.update(id, user));
    }

    @Operation(
        summary = "Remover usuário",
        description = "Remove permanentemente um usuário do sistema com base no ID fornecido."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Usuário removido com sucesso"),
        @ApiResponse(responseCode = "404", description = "Usuário não encontrado"),
        @ApiResponse(responseCode = "400", description = "ID inválido")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID do usuário a ser removido", example = "1") 
            @PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}