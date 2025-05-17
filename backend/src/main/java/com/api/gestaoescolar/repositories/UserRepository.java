package com.api.gestaoescolar.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.api.gestaoescolar.entities.Student;
import com.api.gestaoescolar.entities.Teacher;
import com.api.gestaoescolar.entities.User;

public interface UserRepository extends JpaRepository<User, Long>{

    @Query(value = "SELECT * FROM users WHERE user_type = :userType", nativeQuery = true)
    Page<User> findAllByUserType(@Param("userType") String userType, Pageable pageable);

    @Query("SELECT s FROM Student s WHERE s.cpf IN :cpfs")
    Optional<List<Student>> findStudentsByCpf(@Param("cpfs") List<String> cpfs);

    Optional<User> findByCpf(@Param("cpf") String cpf);

    @Query(value = "SELECT * FROM users WHERE cpf = :cpf AND user_type = 'STUDENT'", nativeQuery = true)
    Optional<Student> findStudentByCpf(@Param("cpf") String cpf);

    @Query(value = "SELECT * FROM users WHERE cpf = :cpf AND user_type = 'TEACHER'", nativeQuery = true)
    Optional<Teacher> findTeacherByCpf(@Param("cpf") String cpf);

    Optional<Student> findByRegistrationNumber(String registrationNumber);
    Page<Teacher> findBySpecialityIgnoreCaseContaining(String speciality, Pageable pageable);

    boolean existsByCpf(String cpf);
    boolean existsByEmail(String email);
}
