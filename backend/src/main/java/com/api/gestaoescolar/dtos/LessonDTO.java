package com.api.gestaoescolar.dtos;

import com.api.gestaoescolar.entities.Enum.DayOfWeek;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;

public class LessonDTO {

    private final Long id;
    private final SubjectDTO subject;
    private final Long classId;
    private final String teacher;
    private final DayOfWeek dayOfWeek;
    
    @Schema(type = "string", pattern = "HH:mm", example = "08:30")
    @Pattern(regexp = "^([01]\\d|2[0-3]):[0-5]\\d$", message = "Formato de hora inválido. Use HH:mm (24 horas)")
    private final String startTime;
    
    @Schema(type = "string", pattern = "HH:mm", example = "10:30")
    @Pattern(regexp = "^([01]\\d|2[0-3]):[0-5]\\d$", message = "Formato de hora inválido. Use HH:mm (24 horas)")
    private final String endTime;

    public LessonDTO(Long id, SubjectDTO subject, Long classId, String teacher, DayOfWeek dayOfWeek,
            @Pattern(regexp = "^([01]\\d|2[0-3]):[0-5]\\d$", message = "Formato de hora inválido. Use HH:mm (24 horas)") String startTime,
            @Pattern(regexp = "^([01]\\d|2[0-3]):[0-5]\\d$", message = "Formato de hora inválido. Use HH:mm (24 horas)") String endTime) {
        this.id = id;
        this.subject = subject;
        this.classId = classId;
        this.teacher = teacher;
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public Long getId() {
        return id;
    }

    public SubjectDTO getSubject() {
        return subject;
    }

    public String getTeacher() {
        return teacher;
    }

    public DayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    @Override
    public String toString() {
        return "LessonDTO{" +
                "id=" + id +
                ", subject=" + subject +
                ", teacher='" + teacher + '\'' +
                ", dayOfWeek=" + dayOfWeek +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                '}';
    }

    public Long getClassId() {
        return classId;
    }
}
