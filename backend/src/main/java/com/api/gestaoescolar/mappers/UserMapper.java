package com.api.gestaoescolar.mappers;

import com.api.gestaoescolar.dtos.UserDTO;
import com.api.gestaoescolar.dtos.StudentDTO;
import com.api.gestaoescolar.dtos.TeacherDTO;
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
        userDto.setCpf(user.getCpf());
        userDto.setEmail(user.getEmail());
        userDto.setPassword(user.getPassword());
        userDto.setCreatedAt(user.getCreatedAt());
        userDto.setFullName(user.getFullName());

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
        } else if (user instanceof Teacher teacher) {
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
        } else if ("TEACHER".equalsIgnoreCase(userDto.getUserType())) {
            Teacher teacher = new Teacher();
            teacher.setSpeciality(userDto.getSpeciality());
            user = teacher;
        } else {
            user = new User() {};
        }

        user.setId(userDto.getId());
        user.setCpf(userDto.getCpf());
        user.setEmail(userDto.getEmail());
        user.setPassword(userDto.getPassword());
        user.setCreatedAt(userDto.getCreatedAt());
        user.setFullName(userDto.getFullName());

        return user;
    }

    public static void updateFromDto(UserDTO userDto, User user) {
        if (userDto == null || user == null) {
            return;
        }

        if (userDto.getEmail() != null) {
            user.setEmail(userDto.getEmail());
        }
        if (userDto.getPassword() != null) {
            user.setPassword(userDto.getPassword());
        }
        if (userDto.getFullName() != null) {
            user.setFullName(userDto.getFullName());
        }

        if (user instanceof Student student && "STUDENT".equalsIgnoreCase(userDto.getUserType())) {
            if (userDto.getRegistrationNumber() != null) {
                student.setRegistrationNumber(userDto.getRegistrationNumber());
            }
        } else if (user instanceof Teacher teacher && "TEACHER".equalsIgnoreCase(userDto.getUserType())) {
            if (userDto.getSpeciality() != null) {
                teacher.setSpeciality(userDto.getSpeciality());
            }
        }
    }

    public static StudentDTO toStudentDTO(Student student) {
        if (student == null) return null;

        StudentDTO dto = new StudentDTO();
        dto.setId(student.getId());
        dto.setCpf(student.getCpf());
        dto.setEmail(student.getEmail());
        dto.setCreatedAt(student.getCreatedAt());
        dto.setRegistrationNumber(student.getRegistrationNumber());
        dto.setClasses(ClassesMapper.toDtoList(student.getClasses()));
        dto.setEvaluations(EvaluationMapper.toDtoList(student.getEvaluations()));
        dto.setAttendances(AttendanceMapper.toDtoList(student.getAttendances()));
        dto.setFullName(student.getFullName());

        return dto;
    }

    public static TeacherDTO toTeacherDTO(Teacher teacher) {
        if (teacher == null) return null;

        TeacherDTO dto = new TeacherDTO();
        dto.setId(teacher.getId());
        dto.setCpf(teacher.getCpf());
        dto.setEmail(teacher.getEmail());
        dto.setCreatedAt(teacher.getCreatedAt());
        dto.setSpeciality(teacher.getSpeciality());
        dto.setClasses(ClassesMapper.toDtoList(teacher.getClasses()));
        dto.setFullName(teacher.getFullName());

        return dto;
    }
}
