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
            dto.setStudent(attendance.getStudent().getCpf());
        }

        if (attendance.getSubject() != null) {
            dto.setSubjectId(attendance.getSubject().getId());
            dto.setSubjectName(attendance.getSubject().getName());
            dto.setSubjectName(attendance.getSubject().getTeacher().getFullName());
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
    public static void updateFromDto(AttendanceDTO dto, Attendance entity) {
        if (dto == null || entity == null) return;
        if (dto.getDate() != null) {
            entity.setDate(dto.getDate());
        }     
        if (dto.getPresent() != null) {
            entity.setPresent(dto.getPresent());
        }

    }
}
