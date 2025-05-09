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

import com.api.gestaoescolar.dtos.UserDTO;

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
        User student = userRepository.findByCpf(cpf)
            .orElseThrow(() -> new ResourceNotFoundException("Estudante não encontrado com CPF: " + cpf));
        return UserMapper.toDto(student);
    }
    @Transactional
    public void deleteByCpf(String cpf) {
        User student = userRepository.findByCpf(cpf)
            .orElseThrow(() -> new ResourceNotFoundException("Estudante não encontrado com CPF: " + cpf));
        userRepository.delete(student);
    }

    // Métodos para Students
    @Transactional(readOnly = true)
    public Page<UserDTO> findAllStudents(Pageable pageable) {
        Page<User> students = userRepository.findAllByUserType("STUDENT", pageable);
        return students.map(UserMapper::toDto);
    }

    @Transactional
    public UserDTO createStudent(UserDTO studentDTO) {
        studentDTO.setUserType("STUDENT");
        User student = UserMapper.toEntity(studentDTO);
        User savedStudent = userRepository.save(student);
        return UserMapper.toDto(savedStudent);
    }

    @Transactional
    public UserDTO updateStudent(String cpf, UserDTO studentDTO) {
        User existingStudent = userRepository.findByCpf(cpf)
            .orElseThrow(() -> new ResourceNotFoundException("Estudante não encontrado com CPF: " + cpf));

        UserMapper.updateFromDto(studentDTO, existingStudent);
        User updatedStudent = userRepository.save(existingStudent);
        return UserMapper.toDto(updatedStudent);
}

    // Métodos para Teachers
    @Transactional(readOnly = true)
    public Page<UserDTO> findAllTeachers(Pageable pageable) {
        Page<User> teachers = userRepository.findAllByUserType("TEACHER", pageable);
        return teachers.map(UserMapper::toDto);
    }

    @Transactional
    public UserDTO createTeacher(UserDTO teacherDTO) {
        teacherDTO.setUserType("TEACHER");
        User teacher = UserMapper.toEntity(teacherDTO);
        User savedTeacher = userRepository.save(teacher);
        return UserMapper.toDto(savedTeacher);
    }

    @Transactional
    public UserDTO updateTeacher(String cpf, UserDTO teacherDTO) {
        User existingTeacher = userRepository.findByCpf(cpf)
            .orElseThrow(() -> new ResourceNotFoundException("Professor não encontrado com CPF: " + cpf));

        UserMapper.updateFromDto(teacherDTO, existingTeacher);
        User updatedTeacher = userRepository.save(existingTeacher);
        return UserMapper.toDto(updatedTeacher);
    }


    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);

        List<GrantedAuthority> authorities = user.getRoles().stream()
            .map(role -> new SimpleGrantedAuthority(role.getAuthority()))
            .collect(Collectors.toList());

        return new org.springframework.security.core.userdetails.User(
            user.getUsername(),
            user.getPassword(),
            authorities
        );
    }

    @Transactional(readOnly = true)
    public boolean existsByCpf(String cpf) {
        return userRepository.existsByCpf(cpf);
    }
}