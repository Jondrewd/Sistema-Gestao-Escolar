package com.api.gestaoescolar.services;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.api.gestaoescolar.dtos.ClassesDTO;
import com.api.gestaoescolar.entities.*;
import com.api.gestaoescolar.exceptions.ResourceNotFoundException;
import com.api.gestaoescolar.mappers.ClassesMapper;
import com.api.gestaoescolar.repositories.*;

@Service
@Transactional
public class ClassesService {

    private final ClassesRepository classesRepository;
    private final UserRepository userRepository;
    private final LessonRepository lessonRepository;

    public ClassesService(ClassesRepository classesRepository,
                        UserRepository userRepository,
                        LessonRepository lessonRepository) {
        this.classesRepository = classesRepository;
        this.userRepository = userRepository;
        this.lessonRepository = lessonRepository;
    }

    @Transactional
    public ClassesDTO createClass(ClassesDTO classesDTO) {
        Classes newClass = new Classes();
        newClass.setName(classesDTO.getName());

        if (classesDTO.getLessonIds() != null && !classesDTO.getLessonIds().isEmpty()) {
            List<Lesson> lessons = lessonRepository.findAllById(classesDTO.getLessonIds());
            if (lessons.size() != classesDTO.getLessonIds().size()) {
                throw new ResourceNotFoundException("Uma ou mais matérias não encontradas");
            }
            newClass.setLessons(lessons);
        }

        if (classesDTO.getStudentCpfs() != null && !classesDTO.getStudentCpfs().isEmpty()) {
            Set<Student> students = userRepository.findStudentsByCpf(classesDTO.getStudentCpfs())
                .orElseThrow(() -> new ResourceNotFoundException("Um ou mais alunos não encontrados"))
                .stream()
                .collect(Collectors.toSet());
            newClass.setStudents(students);
        }

        Classes savedClass = classesRepository.save(newClass);
        return ClassesMapper.toDto(savedClass);
    }

    @Transactional(readOnly = true)
    public ClassesDTO getClassById(Long id) {
        Classes classes = classesRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Turma não encontrada com ID: " + id));
        return ClassesMapper.toDto(classes);
    }

    @Transactional(readOnly = true)
    public Page<ClassesDTO> getAllClasses(Pageable pageable) {
        return classesRepository.findAll(pageable)
                .map(ClassesMapper::toDto);
    }

    @Transactional
    public ClassesDTO updateClass(Long id, ClassesDTO classesDTO) {
        Classes existingClass = classesRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Turma não encontrada com ID: " + id));

        if (classesDTO.getName() != null) {
            existingClass.setName(classesDTO.getName());
        }

        if (classesDTO.getLessonIds() != null) {
            List<Lesson> lessons = lessonRepository.findAllById(classesDTO.getLessonIds());
            if (lessons.size() != classesDTO.getLessonIds().size()) {
                throw new ResourceNotFoundException("Uma ou mais matérias não encontradas");
            }
            existingClass.setLessons(lessons);
        }

        if (classesDTO.getStudentCpfs() != null) {
            Set<Student> students = userRepository.findStudentsByCpf(classesDTO.getStudentCpfs())
                .orElseThrow(() -> new ResourceNotFoundException("Um ou mais alunos não encontrados"))
                .stream()
                .collect(Collectors.toSet());
            existingClass.setStudents(students);
        }

        Classes updatedClass = classesRepository.save(existingClass);
        return ClassesMapper.toDto(updatedClass);
    }

    @Transactional
    public void deleteClass(Long id) {
        if (!classesRepository.existsById(id)) {
            throw new ResourceNotFoundException("Turma não encontrada com ID: " + id);
        }
        classesRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<String> getStudentsInClass(Long classId) {
        Classes classe = classesRepository.findById(classId)
                .orElseThrow(() -> new ResourceNotFoundException("Turma não encontrada com ID: " + classId));
        
        return classe.getStudents().stream()
                .map(Student::getCpf)
                .collect(Collectors.toList());
    }

    @Transactional
    public ClassesDTO addStudentsToClass(Long classId, List<String> studentCpfs) {
        Classes existingClass = classesRepository.findById(classId)
                .orElseThrow(() -> new ResourceNotFoundException("Turma não encontrada com ID: " + classId));

        if (studentCpfs == null || studentCpfs.isEmpty()) {
            throw new IllegalArgumentException("Lista de CPFs não pode ser vazia");
        }

        Set<Student> studentsToAdd = userRepository.findStudentsByCpf(studentCpfs)
                .orElseThrow(() -> new ResourceNotFoundException("Alunos não encontrados"))
                .stream()
                .collect(Collectors.toSet());

        if (studentsToAdd.size() != studentCpfs.size()) {
            List<String> foundCpfs = studentsToAdd.stream()
                    .map(Student::getCpf)
                    .collect(Collectors.toList());
            
            List<String> missingCpfs = studentCpfs.stream()
                    .filter(cpf -> !foundCpfs.contains(cpf))
                    .collect(Collectors.toList());
            
            throw new ResourceNotFoundException("Alunos não encontrados com os CPFs: " + missingCpfs);
        }

        existingClass.getStudents().addAll(studentsToAdd);
        
        Classes updatedClass = classesRepository.save(existingClass);
        return ClassesMapper.toDto(updatedClass);
    }  
    @Transactional
    public ClassesDTO removeStudentsFromClass(Long classId, List<String> studentCpfs) {
        Classes existingClass = classesRepository.findById(classId)
                .orElseThrow(() -> new ResourceNotFoundException("Turma não encontrada com ID: " + classId));

        if (studentCpfs == null || studentCpfs.isEmpty()) {
            throw new IllegalArgumentException("Lista de CPFs não pode ser vazia");
        }

        Set<Student> studentsToRemove = existingClass.getStudents().stream()
                .filter(student -> studentCpfs.contains(student.getCpf()))
                .collect(Collectors.toSet());

        existingClass.getStudents().removeAll(studentsToRemove);
        
        Classes updatedClass = classesRepository.save(existingClass);
        return ClassesMapper.toDto(updatedClass);
    }
}