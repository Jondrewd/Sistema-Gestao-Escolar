package com.api.gestaoescolar.mappers;

import java.util.List;
import java.util.stream.Collectors;

import com.api.gestaoescolar.dtos.AttendanceDTO;
import com.api.gestaoescolar.entities.Attendance;

public class AttendanceMapper {

    private AttendanceMapper() {}

    public static AttendanceDTO toDTO(Attendance attendance) {
        if (attendance == null) {
            return null;
        }

        AttendanceDTO dto = new AttendanceDTO();
        dto.setId(attendance.getId());
        dto.setDate(attendance.getDate());
        dto.setPresent(attendance.getPresent());

        if (attendance.getStudent() != null) {
            dto.setStudent(attendance.getStudent().getUsername()); 
        }

        if (attendance.getGroup() != null) {
            dto.setGroupId(attendance.getGroup().getId()); 
        }

        return dto;
    }

    public static Attendance toEntity(AttendanceDTO dto) {
        if (dto == null) {
            return null;
        }

        Attendance attendance = new Attendance();
        attendance.setId(dto.getId());
        attendance.setDate(dto.getDate());
        attendance.setPresent(dto.getPresent());

        return attendance;
    }

    public static List<AttendanceDTO> toDtoList(List<Attendance> attendances) {
        if (attendances == null) {
            return List.of(); 
        }

        return attendances.stream()
                .map(AttendanceMapper::toDTO)
                .collect(Collectors.toList());
    }
}
