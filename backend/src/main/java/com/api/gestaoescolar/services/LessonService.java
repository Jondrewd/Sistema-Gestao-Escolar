package com.api.gestaoescolar.services;

import java.time.LocalTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.api.gestaoescolar.dtos.LessonDTO;
import com.api.gestaoescolar.entities.Lesson;
import com.api.gestaoescolar.entities.Subject;
import com.api.gestaoescolar.entities.Teacher;
import com.api.gestaoescolar.exceptions.ResourceNotFoundException;
import com.api.gestaoescolar.mappers.LessonMapper;
import com.api.gestaoescolar.repositories.LessonRepository;
import com.api.gestaoescolar.repositories.SubjectRepository;
import com.api.gestaoescolar.repositories.UserRepository;


@Service
@Transactional
public class LessonService {

    private final LessonRepository lessonRepository;
    private final SubjectRepository subjectRepository;
    private final UserRepository teacherRepository;

    public LessonService(LessonRepository lessonRepository,
                       SubjectRepository subjectRepository,
                       UserRepository teacherRepository) {
        this.lessonRepository = lessonRepository;
        this.subjectRepository = subjectRepository;
        this.teacherRepository = teacherRepository;
    }

    @Transactional(readOnly = true)
    public LessonDTO findById(Long id) {
        Lesson lesson = lessonRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Aula não encontrada com ID: " + id));
        return LessonMapper.toDTO(lesson);
    }

    @Transactional(readOnly = true)
    public List<LessonDTO> findAll() {
        return LessonMapper.toDtoList(lessonRepository.findAll());
    }

    @Transactional(readOnly = true)
    public Page<LessonDTO> findAll(Pageable pageable) {
        return lessonRepository.findAll(pageable)
                .map(LessonMapper::toDTO);
    }

    @Transactional
    public LessonDTO create(LessonDTO lessonDTO) {

        validateLessonTimes(lessonDTO);

        Lesson lesson = new Lesson();

        Subject subject = subjectRepository.findById(lessonDTO.getSubject().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Matéria não encontrada com ID: " + lessonDTO.getSubject().getId()));
        lesson.setSubject(subject);

        if (lessonDTO.getTeacher() != null) {
            Teacher teacher = teacherRepository.findTeacherByCpf(lessonDTO.getTeacher())
                    .orElseThrow(() -> new ResourceNotFoundException("Professor não encontrado: " + lessonDTO.getTeacher()));
            lesson.setTeacher(teacher);
        }

        lesson.setDayOfWeek(lessonDTO.getDayOfWeek());
        lesson.setStartTime(LocalTime.parse(lessonDTO.getStartTime()));
        lesson.setEndTime(LocalTime.parse(lessonDTO.getEndTime()));

        checkForScheduleConflicts(lesson);

        Lesson savedLesson = lessonRepository.save(lesson);
        return LessonMapper.toDTO(savedLesson);
    }

    @Transactional
    public LessonDTO update(Long id, LessonDTO lessonDTO) {
        Lesson existingLesson = lessonRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Aula não encontrada com ID: " + id));

        validateLessonTimes(lessonDTO);

        if (lessonDTO.getSubject() != null) {
            Subject subject = subjectRepository.findById(lessonDTO.getSubject().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Matéria não encontrada com ID: " + lessonDTO.getSubject().getId()));
            existingLesson.setSubject(subject);
        }

        if (lessonDTO.getTeacher() != null) {
            Teacher teacher = teacherRepository.findTeacherByCpf(lessonDTO.getTeacher())
                    .orElseThrow(() -> new ResourceNotFoundException("Professor não encontrado: " + lessonDTO.getTeacher()));
            existingLesson.setTeacher(teacher);
        }

        if (lessonDTO.getDayOfWeek() != null) {
            existingLesson.setDayOfWeek(lessonDTO.getDayOfWeek());
        }
        if (lessonDTO.getStartTime() != null) {
            existingLesson.setStartTime(LocalTime.parse(lessonDTO.getStartTime()));
        }
        if (lessonDTO.getEndTime() != null) {
            existingLesson.setEndTime(LocalTime.parse(lessonDTO.getEndTime()));
        }
        checkForScheduleConflicts(existingLesson, id);

        Lesson updatedLesson = lessonRepository.save(existingLesson);
        return LessonMapper.toDTO(updatedLesson);
    }

    @Transactional
    public void delete(Long id) {
        if (!lessonRepository.existsById(id)) {
            throw new ResourceNotFoundException("Aula não encontrada com ID: " + id);
        }
        lessonRepository.deleteById(id);
    }


    private void validateLessonTimes(LessonDTO lessonDTO) {
        if (lessonDTO.getStartTime() != null && lessonDTO.getEndTime() != null) {
            if (LocalTime.parse(lessonDTO.getEndTime()).isBefore(LocalTime.parse(lessonDTO.getStartTime()))) {
                throw new IllegalArgumentException("O horário de término não pode ser antes do horário de início");
            }
            if (lessonDTO.getStartTime().equals(lessonDTO.getEndTime())) {
                throw new IllegalArgumentException("O horário de início e término não podem ser iguais");
            }
        }
    }

    public void checkForScheduleConflicts(Lesson lesson) {
        checkForScheduleConflicts(lesson, null);
    }

    private void checkForScheduleConflicts(Lesson lesson, Long excludeId) {
        List<Lesson> conflictingLessons = lessonRepository.findConflictingLessons(
                lesson.getDayOfWeek(),
                lesson.getStartTime(),
                lesson.getEndTime(),
                lesson.getTeacher() != null ? lesson.getTeacher().getId() : null,
                excludeId);

        if (!conflictingLessons.isEmpty()) {
            throw new IllegalStateException("Conflito de horário com a aula ID: " + 
                    conflictingLessons.get(0).getId());
        }
    }

    @Transactional(readOnly = true)
    public List<LessonDTO> findByTeacher(String teacherCpf) {
        Teacher teacher = teacherRepository.findTeacherByCpf(teacherCpf)
                .orElseThrow(() -> new ResourceNotFoundException("Professor não encontrado com ID: " + teacherCpf));
        
        return LessonMapper.toDtoList(lessonRepository.findByTeacher(teacher));
    }

    @Transactional(readOnly = true)
    public List<LessonDTO> findBySubject(Long subjectId) {
        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new ResourceNotFoundException("Matéria não encontrada com ID: " + subjectId));
        
        return LessonMapper.toDtoList(lessonRepository.findBySubject(subject));
    }
}