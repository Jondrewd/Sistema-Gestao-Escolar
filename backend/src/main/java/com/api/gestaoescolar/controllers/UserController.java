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

import com.api.gestaoescolar.dtos.StudentDTO;
import com.api.gestaoescolar.dtos.TeacherDTO;
import com.api.gestaoescolar.dtos.TypedUserDTO;
import com.api.gestaoescolar.dtos.UserDTO;
import com.api.gestaoescolar.services.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "Gerenciamento de Usuários", description = "API unificada para gerenciamento de estudantes, professores e admins")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(
        summary = "Listar todos os usuários",
        description = "Retorna uma lista paginada de todos os usuários do sistema. Acesso restrito a ADMINs."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de usuários retornada com sucesso"),
        @ApiResponse(responseCode = "403", description = "Acesso negado", content = @Content)
    })
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<UserDTO>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sort) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sort));
        return ResponseEntity.ok(userService.findAll(pageable));
    }

    @Operation(
        summary = "Buscar usuário por ID",
        description = "Recupera os detalhes de um usuário específico pelo ID. Apenas ADMINs podem acessar."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Usuário encontrado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Usuário não encontrado"),
        @ApiResponse(responseCode = "403", description = "Acesso negado", content = @Content)
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.findById(id));
    }

    @Operation(
        summary = "Buscar usuário por CPF",
        description = "Retorna um usuário específico com base no CPF informado. Apenas ADMINs podem acessar."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Usuário encontrado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Usuário não encontrado"),
        @ApiResponse(responseCode = "400", description = "CPF inválido")
    })
    @GetMapping("/cpf/{cpf}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDTO> findByCpf(@PathVariable String cpf) {
        return ResponseEntity.ok(userService.findByCpf(cpf));
    }
    
    @Operation(
    summary = "Buscar usuário por CPF (tipado)",
    description = "Retorna os dados de um usuário com base no CPF informado, incluindo o tipo (STUDENT, TEACHER ou GENERIC). Apenas ADMINs podem acessar."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Usuário encontrado com sucesso", content = @Content(schema = @Schema(implementation = TypedUserDTO.class))),
        @ApiResponse(responseCode = "404", description = "Usuário não encontrado", content = @Content),
        @ApiResponse(responseCode = "400", description = "CPF inválido", content = @Content),
        @ApiResponse(responseCode = "403", description = "Acesso negado", content = @Content)
    })
    @GetMapping("/typed/cpf/{cpf}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TypedUserDTO> findTypedByCpf(@PathVariable String cpf) {
        return ResponseEntity.ok(userService.findTypedDTOByCpf(cpf));
    }


    @Operation(
        summary = "Filtrar usuários por tipo",
        description = "Lista usuários filtrados por tipo: STUDENT, TEACHER ou ADMIN. Apenas ADMINs podem acessar."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Usuários listados com sucesso"),
        @ApiResponse(responseCode = "400", description = "Tipo de usuário inválido")
    })
    @GetMapping("/type/{userType}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<UserDTO>> getUsersByType(
            @PathVariable String userType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sort) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sort));
        return ResponseEntity.ok(userService.getUsersByType(userType, pageable));
    }

    @Operation(
        summary = "Criar um novo Admin",
        description = "Cria um novo Administrador. Apenas ADMINs podem criar."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Usuário criado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @ApiResponse(responseCode = "409", description = "CPF já cadastrado")
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDTO> createAdmin(@Valid @RequestBody UserDTO userDTO) {
        UserDTO createdUser = userService.createAdmin(userDTO);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(userService.extractIdFromCreatedUser(createdUser))
                .toUri();
        return ResponseEntity.created(uri).body(createdUser);
    }

    @Operation(
        summary = "Atualizar um usuário",
        description = "Atualiza os dados de um usuário existente com base no CPF. ADMINs podem atualizar qualquer usuário; o próprio usuário também pode atualizar seus dados."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Usuário atualizado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    @PutMapping("/{cpf}")
    @PreAuthorize("hasRole('ADMIN') or #cpf == principal.username")
    public ResponseEntity<Object> update(
            @PathVariable String cpf,
            @Valid @RequestBody UserDTO userDTO) {
        Object updatedUser = userService.updateUserByType(cpf, userDTO);
        return ResponseEntity.ok(updatedUser);
    }

    @Operation(
        summary = "Deletar usuário por ID",
        description = "Remove permanentemente um usuário do sistema pelo ID. Apenas ADMINs podem deletar."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Usuário deletado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
        summary = "Deletar usuário por CPF",
        description = "Remove permanentemente um usuário do sistema pelo CPF. Apenas ADMINs podem deletar."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Usuário deletado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    @DeleteMapping("/cpf/{cpf}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteByCpf(@PathVariable String cpf) {
        userService.deleteByCpf(cpf);
        return ResponseEntity.noContent().build();
    }

    @Operation(
        summary = "Listar professores por especialidade",
        description = "Lista todos os professores com uma determinada especialidade. Acesso: ADMIN ou TEACHER."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Professores encontrados"),
        @ApiResponse(responseCode = "404", description = "Nenhum professor encontrado")
    })
    @GetMapping("/teachers/speciality/{speciality}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<Page<TeacherDTO>> findTeachersBySpeciality(
            @PathVariable String speciality,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(userService.findBySpeciality(speciality, pageable));
    }

    @Operation(
        summary = "Buscar estudante por número de registro",
        description = "Recupera um estudante a partir do número de registro acadêmico. Acesso: ADMIN ou TEACHER."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Estudante encontrado"),
        @ApiResponse(responseCode = "404", description = "Estudante não encontrado")
    })
    @GetMapping("/students/registration/{registrationNumber}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<StudentDTO> findByRegistrationNumber(@PathVariable String registrationNumber) {
        return ResponseEntity.ok(userService.findByRegistrationNumber(registrationNumber));
    }
}
