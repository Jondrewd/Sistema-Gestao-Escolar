package com.api.gestaoescolar.mappers;

import com.api.gestaoescolar.dtos.UserDTO;
import com.api.gestaoescolar.entities.Student;
import com.api.gestaoescolar.entities.Teacher;
import com.api.gestaoescolar.entities.User;

public class UserMapper {

    private UserMapper() {}

    public static UserDTO toDto(User user) {
        if (user == null) {
            return null;
        }

        UserDTO userDto = new UserDTO();
        userDto.setId(user.getId());
        userDto.setUsername(user.getUsername());
        userDto.setCpf(user.getCpf());
        userDto.setEmail(user.getEmail());
        userDto.setPassword(user.getPassword());
        userDto.setCreatedAt(user.getCreatedAt());
        
        if (user instanceof Student) {
            userDto.setUserType("STUDENT");
        } else if (user instanceof Teacher) {
            userDto.setUserType("TEACHER");
        }

        if (user instanceof Student student) {
            userDto.setRegistrationNumber(student.getRegistrationNumber());
            userDto.setStudentClasses(ClassesMapper.toDtoList(student.getClasses()));
            userDto.setEvaluations(EvaluationMapper.toDtoList(student.getEvaluations()));
            userDto.setAttendances(AttendanceMapper.toDtoList(student.getAttendances()));
        } 
        else if (user instanceof Teacher teacher) {
            userDto.setSpeciality(teacher.getSpeciality());
            userDto.setTeacherClasses(ClassesMapper.toDtoList(teacher.getClasses()));
        }

        return userDto;
    }

    public static User toEntity(UserDTO userDto) {
        if (userDto == null) {
            return null;
        }

        User user;

        if ("STUDENT".equalsIgnoreCase(userDto.getUserType())) {
            Student student = new Student();
            student.setRegistrationNumber(userDto.getRegistrationNumber());
            user = student;
        } 
        else if ("TEACHER".equalsIgnoreCase(userDto.getUserType())) {
            Teacher teacher = new Teacher();
            teacher.setSpeciality(userDto.getSpeciality());
            user = teacher;
        } 
        else {
            user = new User() {};
        }

        // Campos comuns
        user.setId(userDto.getId());
        user.setUsername(userDto.getUsername());
        user.setCpf(userDto.getCpf());
        user.setEmail(userDto.getEmail());
        user.setPassword(userDto.getPassword());
        user.setCreatedAt(userDto.getCreatedAt());

        return user;
    }

    public static void updateFromDto(UserDTO userDto, User user) {
        if (userDto == null || user == null) {
            return;
        }

        if (userDto.getUsername() != null) {
            user.setUsername(userDto.getUsername());
        }
        if (userDto.getEmail() != null) {
            user.setEmail(userDto.getEmail());
        }
        if (userDto.getPassword() != null) {
            user.setPassword(userDto.getPassword());
        }

        if (user instanceof Student student && "STUDENT".equalsIgnoreCase(userDto.getUserType())) {
            if (userDto.getRegistrationNumber() != null) {
                student.setRegistrationNumber(userDto.getRegistrationNumber());
            }
        } 
        else if (user instanceof Teacher teacher && "TEACHER".equalsIgnoreCase(userDto.getUserType())) {
            if (userDto.getSpeciality() != null) {
                teacher.setSpeciality(userDto.getSpeciality());
            }
        }
    }
}