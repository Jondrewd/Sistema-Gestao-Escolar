package com.api.gestaoescolar.services;

import java.time.LocalTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.api.gestaoescolar.dtos.SubjectDTO;
import com.api.gestaoescolar.entities.Subject;
import com.api.gestaoescolar.entities.Teacher;
import com.api.gestaoescolar.entities.Classes;
import com.api.gestaoescolar.exceptions.ResourceNotFoundException;
import com.api.gestaoescolar.mappers.SubjectMapper;
import com.api.gestaoescolar.repositories.SubjectRepository;
import com.api.gestaoescolar.repositories.UserRepository;
import com.api.gestaoescolar.repositories.ClassesRepository;

@Service
@Transactional
public class SubjectService {

    private final SubjectRepository subjectRepository;
    private final UserRepository teacherRepository;
    private final ClassesRepository classesRepository;

    public SubjectService(
        SubjectRepository subjectRepository,
        UserRepository teacherRepository,
        ClassesRepository classesRepository
    ) {
        this.subjectRepository = subjectRepository;
        this.teacherRepository = teacherRepository;
        this.classesRepository = classesRepository;
    }

    @Transactional(readOnly = true)
    public SubjectDTO findById(Long id) {
        Subject subject = subjectRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Disciplina não encontrada com ID: " + id));
        return SubjectMapper.toDto(subject);
    }

    @Transactional(readOnly = true)
    public Page<SubjectDTO> findAll(Pageable pageable) {
        return subjectRepository.findAll(pageable)
            .map(SubjectMapper::toDto);
    }

    @Transactional
    public SubjectDTO create(SubjectDTO dto) {
        Subject subject = SubjectMapper.toEntity(dto);

        if (dto.getTeacher() != null) {
            Teacher teacher = (Teacher) teacherRepository.findById(dto.getTeacherId())
                .orElseThrow(() -> new ResourceNotFoundException("Professor não encontrado com ID: " + dto.getTeacherId()));
            subject.setTeacher(teacher);
        }

        if (dto.getClassId() != null) {
            Classes classes = classesRepository.findById(dto.getClassId())
                .orElseThrow(() -> new ResourceNotFoundException("Turma não encontrada com ID: " + dto.getClassId()));
            subject.setClasses(classes);
        }

        Subject saved = subjectRepository.save(subject);
        return SubjectMapper.toDto(saved);
    }

    @Transactional
    public SubjectDTO update(Long id, SubjectDTO dto) {
        Subject subject = subjectRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Disciplina não encontrada com ID: " + id));

        if (dto.getName() != null) subject.setName(dto.getName());
        if (dto.getDayOfWeek() != null) subject.setDayOfWeek(dto.getDayOfWeek());
        if (dto.getStartTime() != null) subject.setStartTime(LocalTime.parse(dto.getStartTime()));
        if (dto.getEndTime() != null) subject.setEndTime(LocalTime.parse(dto.getEndTime()));

        if (dto.getTeacherId() != null) {
            Teacher teacher = (Teacher) teacherRepository.findById(dto.getTeacherId())
                .orElseThrow(() -> new ResourceNotFoundException("Professor não encontrado com ID: " + dto.getTeacherId()));
            subject.setTeacher(teacher);
        }

        if (dto.getClassId() != null) {
            Classes classes = classesRepository.findById(dto.getClassId())
                .orElseThrow(() -> new ResourceNotFoundException("Turma não encontrada com ID: " + dto.getClassId()));
            subject.setClasses(classes);
        }

        Subject updated = subjectRepository.save(subject);
        return SubjectMapper.toDto(updated);
    }

    @Transactional
    public void delete(Long id) {
        if (!subjectRepository.existsById(id)) {
            throw new ResourceNotFoundException("Disciplina não encontrada com ID: " + id);
        }
        subjectRepository.deleteById(id);
    }
   
}
