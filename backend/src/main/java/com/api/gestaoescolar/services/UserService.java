package com.api.gestaoescolar.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.api.gestaoescolar.dtos.StudentDTO;
import com.api.gestaoescolar.dtos.TeacherDTO;
import com.api.gestaoescolar.dtos.UserDTO;
import com.api.gestaoescolar.entities.Student;
import com.api.gestaoescolar.entities.Teacher;
import com.api.gestaoescolar.entities.User;
import com.api.gestaoescolar.exceptions.ResourceNotFoundException;
import com.api.gestaoescolar.mappers.UserMapper;
import com.api.gestaoescolar.repositories.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
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

    @Transactional
    public UserDTO create(UserDTO userDTO) {
        User user = UserMapper.toEntity(userDTO);
        User savedUser = userRepository.save(user);
        return UserMapper.toDto(savedUser);
    }

    @Transactional
    public UserDTO update(Long id, UserDTO userDTO) {
        User existingUser = userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com ID: " + id));

        UserMapper.updateFromDto(userDTO, existingUser);
        User updatedUser = userRepository.save(existingUser);
        return UserMapper.toDto(updatedUser);
    }

    @Transactional
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
    @Transactional
    public void deleteByCpf(String cpf) {
        User user = userRepository.findByCpf(cpf)
            .orElseThrow(() -> new ResourceNotFoundException("Estudante não encontrado com CPF: " + cpf));
        userRepository.delete(user);
    }

    // Métodos para Students
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

    @Transactional
    public StudentDTO createStudent(UserDTO studentDTO) {
        studentDTO.setUserType("STUDENT");
        User student = UserMapper.toEntity(studentDTO);
        User savedStudent = userRepository.save(student);
        return UserMapper.toStudentDTO((Student) savedStudent);
    }
    @Transactional
    public StudentDTO findByRegistrationNumber(String registrationNumber) {
        Student student = userRepository.findByRegistrationNumber(registrationNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Estudante não encontrado com número de matrícula: " + registrationNumber));
        
        return UserMapper.toStudentDTO(student);
    }

    @Transactional
    public StudentDTO updateStudent(String cpf, UserDTO studentDTO) {
        User existingStudent = userRepository.findByCpf(cpf)
            .orElseThrow(() -> new ResourceNotFoundException("Estudante não encontrado com CPF: " + cpf));

        UserMapper.updateFromDto(studentDTO, existingStudent);
        User updatedStudent = userRepository.save(existingStudent);
        return UserMapper.toStudentDTO((Student) updatedStudent);
    }


    // Métodos para Teachers
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

    @Transactional
    public TeacherDTO createTeacher(UserDTO teacherDTO) {
        teacherDTO.setUserType("TEACHER");
        User teacher = UserMapper.toEntity(teacherDTO);
        User savedTeacher = userRepository.save(teacher);
        return UserMapper.toTeacherDTO((Teacher) savedTeacher);
    }

    @Transactional
    public TeacherDTO updateTeacher(String cpf, UserDTO teacherDTO) {
        User existingTeacher = userRepository.findByCpf(cpf)
            .orElseThrow(() -> new ResourceNotFoundException("Professor não encontrado com CPF: " + cpf));

        UserMapper.updateFromDto(teacherDTO, existingTeacher);
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


    @Transactional(readOnly = true)
    public boolean existsByCpf(String cpf) {
        return userRepository.existsByCpf(cpf);
    }
}