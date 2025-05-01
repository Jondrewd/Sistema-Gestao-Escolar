package com.api.gestaoescolar.repositories;

import java.util.List;
import java.util.Optional;

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

    @Query("SELECT t FROM Teacher t")
    List<Teacher> findAllTeachers();
}
