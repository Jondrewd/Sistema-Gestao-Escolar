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

    @Query("SELECT u FROM User u WHERE u.username = :username")
    User findByUsername(@Param("username") String username);
    
    @Query("SELECT u FROM Teacher u WHERE u.username = :username")
    Optional<Teacher> findTeacherByUsername(@Param("username") String username);

    @Query("SELECT s FROM Student s WHERE s.username IN :usernames")
    Optional<List<Student>> findStudentsByUsernames(@Param("usernames") List<String> usernames);

    @Query("SELECT s FROM Student s WHERE s.username = :username")
    Optional<Student> findStudentByUsername(@Param("username") String username);

    @Query(value = "SELECT * FROM users WHERE user_type = :userType", nativeQuery = true)
    Page<User> findAllByUserType(@Param("userType") String userType, Pageable pageable);

    @Query("SELECT u FROM User u WHERE u.cpf = :cpf")
    Optional<User> findByCpf(@Param("cpf") String cpf);

    boolean existsByUsername(String username);
    boolean existsByCpf(String cpf);
    boolean existsByEmail(String email);
}
