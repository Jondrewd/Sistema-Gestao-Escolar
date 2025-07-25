package com.api.gestaoescolar.mappers;

import com.api.gestaoescolar.dtos.StudentDTO;
import com.api.gestaoescolar.dtos.TeacherDTO;
import com.api.gestaoescolar.dtos.UserDTO;
import com.api.gestaoescolar.entities.Admin;
import com.api.gestaoescolar.entities.Student;
import com.api.gestaoescolar.entities.Teacher;
import com.api.gestaoescolar.entities.User;

public class UserMapper {

    public static UserDTO toDto(User user) {
        if (user == null) return null;

        UserDTO userDto = new UserDTO();
        userDto.setId(user.getId());
        userDto.setCpf(user.getCpf());
        userDto.setEmail(user.getEmail());
        userDto.setCreatedAt(user.getCreatedAt());
        userDto.setFullName(user.getFullName());
        userDto.setPassword(user.getPassword());

        if (user instanceof Student) {
            userDto.setUserType("STUDENT");
        } else if (user instanceof Teacher) {
            userDto.setUserType("TEACHER");
        } else {
            userDto.setUserType("ADMIN");
        }

        return userDto;
    }

    public static User toEntity(UserDTO userDto) {
        if (userDto == null) return null;

        User user;

        if ("ADMIN".equalsIgnoreCase(userDto.getUserType())) {
            user = new Admin();
        } else if ("TEACHER".equalsIgnoreCase(userDto.getUserType())) {
            user = new Teacher();
        } else {
            user = new Student();
        }

        user.setId(userDto.getId());
        user.setCpf(userDto.getCpf());
        user.setEmail(userDto.getEmail());
        user.setCreatedAt(userDto.getCreatedAt());
        user.setFullName(userDto.getFullName());
        user.setPassword(userDto.getPassword());
        return user;
    }

    public static void updateUserFromDto(UserDTO userDto, User user) {
        if (userDto == null || user == null) return;

        if (userDto.getEmail() != null) {
            user.setEmail(userDto.getEmail());
        }
        if (userDto.getFullName() != null) {
            user.setFullName(userDto.getFullName());
        }
        if (userDto.getPassword() != null) {
            user.setPassword(userDto.getPassword());
        }
    }
    public static void updateStudentFromDto(StudentDTO dto, Student student) {
        if (dto == null || student == null) return;

        if (dto.getEmail() != null) {
            student.setEmail(dto.getEmail());
        }
        if (dto.getFullName() != null) {
            student.setFullName(dto.getFullName());
        }
        if (dto.getCpf() != null) {
            student.setCpf(dto.getCpf());
        }
        if (dto.getRegistrationNumber() != null) {
            student.setRegistrationNumber(dto.getRegistrationNumber());
        }


        if (dto.getAttendances() != null) {
            student.setAttendances(
                dto.getAttendances().stream()
                    .map(AttendanceMapper::toEntity)
                    .toList()
            );
        }
    }

    public static void updateTeacherFromDto(TeacherDTO dto, Teacher teacher) {
        if (dto == null || teacher == null) return;

        if (dto.getEmail() != null) {
            teacher.setEmail(dto.getEmail());
        }
        if (dto.getFullName() != null) {
            teacher.setFullName(dto.getFullName());
        }
        if (dto.getCpf() != null) {
            teacher.setCpf(dto.getCpf());
        }
        if (dto.getSpeciality() != null) {
            teacher.setSpeciality(dto.getSpeciality());
        }

        if (dto.getLessons() != null) {
            teacher.setLessons(
                dto.getLessons().stream()
                    .map(LessonMapper::toEntity)
                    .toList()
            );
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
        if (student.getClasses() != null) {
            dto.setClasseId(student.getClasses().getId());
        }
        dto.setGrades(GradeMapper.toDtoList(student.getGrades()));
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
        dto.setLessons(LessonMapper.toDtoList(teacher.getLessons()));
        dto.setFullName(teacher.getFullName());

        return dto;
    }

    public static void updateUserByType(UserDTO userDTO, Object targetDto) {
        switch (targetDto) {
            case StudentDTO studentDTO -> {
                studentDTO.setFullName(userDTO.getFullName());
                studentDTO.setCpf(userDTO.getCpf());
                studentDTO.setEmail(userDTO.getEmail());
            }
            case TeacherDTO teacherDTO -> {
                teacherDTO.setFullName(userDTO.getFullName());
                teacherDTO.setCpf(userDTO.getCpf());
                teacherDTO.setEmail(userDTO.getEmail());
            }
            default -> {
            }
        }
    }
}
