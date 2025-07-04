package com.api.gestaoescolar.services;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.api.gestaoescolar.dtos.StudentDTO;
import com.api.gestaoescolar.dtos.TeacherDTO;
import com.api.gestaoescolar.dtos.TypedUserDTO;
import com.api.gestaoescolar.dtos.UserDTO;
import com.api.gestaoescolar.entities.Admin;
import com.api.gestaoescolar.entities.Classes;
import com.api.gestaoescolar.entities.Roles;
import com.api.gestaoescolar.entities.Student;
import com.api.gestaoescolar.entities.Teacher;
import com.api.gestaoescolar.entities.User;
import com.api.gestaoescolar.exceptions.ResourceNotFoundException;
import com.api.gestaoescolar.mappers.UserMapper;
import com.api.gestaoescolar.repositories.ClassesRepository;
import com.api.gestaoescolar.repositories.RolesRepository;
import com.api.gestaoescolar.repositories.UserRepository;

@Service
@Transactional
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final ClassesRepository classesRepository;
    private final PasswordEncoder passwordEncoder;
    private final RolesRepository roleRepository;

    public UserService(UserRepository userRepository, ClassesRepository classesRepository, RolesRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.classesRepository = classesRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public Page<UserDTO> findAll(Pageable pageable) {
        return userRepository.findAll(pageable)
            .map(UserMapper::toDto);
    }

    @Transactional(readOnly = true)
    public UserDTO findById(Long id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com ID: " + id));
        return UserMapper.toDto(user);
    }

    public UserDTO createAdmin(UserDTO userDTO) {
        if (!"ADMIN".equalsIgnoreCase(userDTO.getUserType())) {
            throw new IllegalArgumentException("Tipo de usuário deve ser ADMIN");
        }

        if (userRepository.existsByCpf(userDTO.getCpf())) {
            throw new DataIntegrityViolationException("CPF já cadastrado");
        }

        if (userRepository.existsByEmail(userDTO.getEmail())) {
            throw new DataIntegrityViolationException("Email já cadastrado");
        }

        Roles roles = roleRepository.findByName("ROLE_TEACHER").get();
        Admin admin = new Admin();

        admin.setFullName(userDTO.getFullName());
        admin.setCpf(userDTO.getCpf());
        admin.setEmail(userDTO.getEmail());
        admin.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        admin.setCreatedAt(Instant.now());
        admin.setRoles(Collections.singletonList(roles));
        userRepository.save(admin);
        return UserMapper.toDto(admin);
    }

    public UserDTO update(Long id, UserDTO userDTO) {
        User existingUser = userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com ID: " + id));

        UserMapper.updateUserFromDto(userDTO, existingUser);
        User updatedUser = userRepository.save(existingUser);
        return UserMapper.toDto(updatedUser);
    }

    public Page<UserDTO> getUsersByType(String userType, Pageable pageable) {
        Page<User> users = userRepository.findAllByUserType(userType, pageable);
        return users.map(user -> UserMapper.toDto(user));
    }

    public void delete(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("Usuário não encontrado com ID: " + id);
        }
        userRepository.deleteById(id);
    }
    
    @Transactional(readOnly = true)
    public UserDTO findByCpf(String cpf) {
        User user = userRepository.findByCpf(cpf)
            .orElseThrow(() -> new ResourceNotFoundException("Usuario não encontrado com CPF: " + cpf));
        return UserMapper.toDto(user);
    }

   public TypedUserDTO findTypedDTOByCpf(String cpf) {
    String type = userRepository.findTypeByCpf(cpf)
        .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

    return switch (type.toUpperCase()) {
        case "STUDENT" -> {
            Student student = userRepository.findStudentByCpf(cpf)
                .orElseThrow(() -> new ResourceNotFoundException("Aluno não encontrado"));
            StudentDTO dto = UserMapper.toStudentDTO(student);
            yield new TypedUserDTO("STUDENT", dto);
        }
        case "TEACHER" -> {
            Teacher teacher = userRepository.findTeacherByCpf(cpf)
                .orElseThrow(() -> new ResourceNotFoundException("Professor não encontrado"));
            TeacherDTO dto = UserMapper.toTeacherDTO(teacher);
            yield new TypedUserDTO("TEACHER", dto);
        }
        default -> {
            User user = userRepository.findByCpf(cpf)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário genérico não encontrado"));
            UserDTO dto = UserMapper.toDto(user);
            yield new TypedUserDTO("ADMIN", dto);
        }
    };
}


    public void deleteByCpf(String cpf) {
        User user = userRepository.findByCpf(cpf)
            .orElseThrow(() -> new ResourceNotFoundException("Estudante não encontrado com CPF: " + cpf));
        userRepository.delete(user);
    }

    @Transactional(readOnly = true)
    public Page<StudentDTO> findAllStudents(Pageable pageable) {
        Page<User> students = userRepository.findAllByUserType("STUDENT", pageable);
        return students.map(user -> UserMapper.toStudentDTO((Student) user));
    }

    @Transactional(readOnly = true)
    public StudentDTO findStudentByCpf(String cpf) {
        Student user = userRepository.findStudentByCpf(cpf)
            .orElseThrow(() -> new ResourceNotFoundException("Usuario não encontrado com CPF: " + cpf));
        return UserMapper.toStudentDTO(user);
    }

    public StudentDTO findByRegistrationNumber(String registrationNumber) {
        Student student = userRepository.findByRegistrationNumber(registrationNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Estudante não encontrado com número de matrícula: " + registrationNumber));
        
        return UserMapper.toStudentDTO(student);
    }

    public StudentDTO updateStudent(String cpf, StudentDTO studentDTO) {
    Student existingStudent = userRepository.findStudentByCpf(cpf)
        .orElseThrow(() -> new ResourceNotFoundException("Estudante não encontrado com CPF: " + cpf));

    UserMapper.updateStudentFromDto(studentDTO, existingStudent);

    if (studentDTO.getClasseId() != null) {
        Classes classe = classesRepository.findById(studentDTO.getClasseId())
            .orElseThrow(() -> new ResourceNotFoundException("Classe não encontrada com ID: " + studentDTO.getClasseId()));
        existingStudent.setClasses(classe);
    }

    User updatedStudent = userRepository.save(existingStudent);
    return UserMapper.toStudentDTO((Student) updatedStudent);
    }

    @Transactional(readOnly = true)
    public Page<TeacherDTO> findAllTeachers(Pageable pageable) {
        Page<User> teachers = userRepository.findAllByUserType("TEACHER", pageable);
        return teachers.map(user -> UserMapper.toTeacherDTO((Teacher) user));
    }

    @Transactional(readOnly = true)
    public TeacherDTO findTeacherByCpf(String cpf) {
        Teacher user = userRepository.findTeacherByCpf(cpf)
            .orElseThrow(() -> new ResourceNotFoundException("Usuario não encontrado com CPF: " + cpf));
        return UserMapper.toTeacherDTO(user);
    }
    
    @Transactional(readOnly = true)
    public Page<TeacherDTO> findBySpeciality(String speciality, Pageable pageable) {
        Page<Teacher> teachers = userRepository.findBySpecialityIgnoreCaseContaining(speciality, pageable);
        return teachers.map(UserMapper::toTeacherDTO);
    }

    public TeacherDTO updateTeacher(String cpf, TeacherDTO teacherDTO) {
        Teacher existingTeacher = (Teacher) userRepository.findByCpf(cpf)
            .orElseThrow(() -> new ResourceNotFoundException("Professor não encontrado com CPF: " + cpf));

        UserMapper.updateTeacherFromDto(teacherDTO, existingTeacher);
        User updatedTeacher = userRepository.save(existingTeacher);

        return UserMapper.toTeacherDTO((Teacher) updatedTeacher);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String cpf) throws UsernameNotFoundException {
        User user = userRepository.findByCpf(cpf)
            .orElseThrow(() -> new ResourceNotFoundException("Estudante não encontrado com CPF: " + cpf));

        List<GrantedAuthority> authorities = user.getRoles().stream()
            .map(role -> new SimpleGrantedAuthority(role.getAuthority()))
            .collect(Collectors.toList());

        return new org.springframework.security.core.userdetails.User(
            user.getCpf(),
            user.getPassword(),
            authorities
        );
    }
    
    public UserDTO updateUserByType(String cpf, UserDTO userDTO) {
        if (cpf == null || cpf.isBlank()) {
            throw new IllegalArgumentException("CPF não pode ser nulo ou vazio");
        }
        if (userDTO == null) {
            throw new IllegalArgumentException("UserDTO não pode ser nulo");
        }

        User user = userRepository.findByCpf(cpf)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com CPF: " + cpf));

        switch (userDTO.getUserType().toUpperCase()) {
            case "STUDENT" -> {
                if (!(user instanceof Student)) {
                    throw new IllegalStateException("Usuário encontrado não é do tipo Student");
                }
            }
            case "TEACHER" -> {
                if (!(user instanceof Teacher)) {
                    throw new IllegalStateException("Usuário encontrado não é do tipo Teacher");
                }
            }
            case "ADMIN" -> {
            }
            default -> throw new IllegalArgumentException(
                        "Tipo de usuário inválido: " + userDTO.getUserType() + ". Tipos válidos: STUDENT, TEACHER, ADMIN.");
        }

        UserMapper.updateUserByType(userDTO, user);

        User updatedUser = userRepository.save(user);
        return UserMapper.toDto(updatedUser);
    }

   public Long extractIdFromCreatedUser(Object createdUser) {
        if (createdUser == null) {
            throw new IllegalArgumentException("O usuário criado não pode ser nulo");
        }

        if (createdUser instanceof User userEntity) {
            return userEntity.getId();
        }

        if (createdUser instanceof UserDTO userDTO) {
            return userDTO.getId();
        }

        throw new IllegalArgumentException("Tipo de objeto inválido para extração de ID: " + createdUser.getClass().getName());
    }

    @Transactional(readOnly = true)
    public boolean existsByCpf(String cpf) {
        return userRepository.existsByCpf(cpf);
    }
}