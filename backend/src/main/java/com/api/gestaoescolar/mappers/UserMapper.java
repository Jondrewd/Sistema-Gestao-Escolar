package com.api.gestaoescolar.mappers;

import com.api.gestaoescolar.dtos.UserDTO;
import com.api.gestaoescolar.entities.Student;
import com.api.gestaoescolar.entities.Teacher;
import com.api.gestaoescolar.entities.User;
import com.api.gestaoescolar.entities.enums.SchoolRoles;

public class UserMapper {

    private UserMapper() {}

    public static UserDTO toDto(User user) {
        if (user == null) {
            return null;
        }

        UserDTO userDto = new UserDTO();
        userDto.setId(user.getId());
        userDto.setUsername(user.getUsername());
        userDto.setEmail(user.getEmail());
        userDto.setPassword(user.getPassword());
        userDto.setCreatedAt(user.getCreatedAt());
        userDto.setSchoolRole(user.getSchoolRole() != null ? user.getSchoolRole().name() : null);

        if (user instanceof Student) {
            Student student = (Student) user;
            userDto.setUserType("STUDENT");
            userDto.setRegistrationNumber(student.getRegistrationNumber());

            userDto.setStudentGroups(GroupMapper.toDtoList(student.getGroups()));
            userDto.setEvaluations(EvaluationMapper.toDtoList(student.getEvaluations()));
            userDto.setAttendances(AttendanceMapper.toDtoList(student.getAttendances()));

        } else if (user instanceof Teacher) {
            Teacher teacher = (Teacher) user;
            userDto.setUserType("TEACHER");
            userDto.setSpeciality(teacher.getSpeciality());

            userDto.setTeacherGroups(GroupMapper.toDtoList(teacher.getGroups()));
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
            user = new User() {};  // Lembrar de nao usar generico, preciso mudar
        }

        user.setId(userDto.getId());
        user.setUsername(userDto.getUsername());
        user.setEmail(userDto.getEmail());
        user.setPassword(userDto.getPassword());
        user.setCreatedAt(userDto.getCreatedAt());

        if (userDto.getSchoolRole() != null) {
            user.setSchoolRole(SchoolRoles.valueOf(userDto.getSchoolRole()));
        }

        // Tratar groups, evaluations, attendances no service

        return user;
    }
}
