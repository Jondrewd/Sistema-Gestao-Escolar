package com.api.gestaoescolar.mappers;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.api.gestaoescolar.dtos.GroupDTO;
import com.api.gestaoescolar.entities.Group;
import com.api.gestaoescolar.entities.Student;

public class GroupMapper {

    private GroupMapper() {}

    public static GroupDTO toDto(Group group) {
        if (group == null) {
            return null;
        }

        GroupDTO dto = new GroupDTO();
        dto.setId(group.getId());
        dto.setName(group.getName());

        if (group.getTeacher() != null) {
            dto.setTeacher(group.getTeacher().getUsername());
        }

        if (group.getCourse() != null) {
            dto.setCourse(CourseMapper.toDto(group.getCourse()));
        }

        if (group.getStudents() != null) {
            dto.setStudents(group.getStudents().stream()
                    .map(Student::getUsername)
                    .collect(Collectors.toList()));
        } else {
            dto.setStudents(Collections.emptyList());
        }

        if (group.getAttendances() != null) {
            dto.setAttendances(AttendanceMapper.toDtoList(group.getAttendances()));
        } else {
            dto.setAttendances(Collections.emptyList());
        }

        return dto;
    }

    public static Group toEntity(GroupDTO dto) {
        if (dto == null) {
            return null;
        }

        Group group = new Group();
        group.setId(dto.getId());
        group.setName(dto.getName());
        
        // Teacher, Students e Course devem ser setados no service
        
        return group;
    }

    public static List<GroupDTO> toDtoList(List<Group> groups) {
        if (groups == null) {
            return Collections.emptyList();
        }

        return groups.stream()
                .map(GroupMapper::toDto)
                .collect(Collectors.toList());
    }
}
