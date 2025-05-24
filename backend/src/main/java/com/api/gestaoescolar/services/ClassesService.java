package com.api.gestaoescolar.services;

import java.util.HashSet;
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

import jakarta.persistence.EntityNotFoundException;

@Service
@Transactional
public class ClassesService {

    private final ClassesRepository classesRepository;
    private final UserRepository userRepository;
    private final SubjectRepository subjectRepository;

    public ClassesService(ClassesRepository classesRepository,
                          UserRepository userRepository,
                          SubjectRepository subjectRepository) {
        this.classesRepository = classesRepository;
        this.userRepository = userRepository;
        this.subjectRepository = subjectRepository;
    }

    public ClassesDTO createClass(ClassesDTO classesDTO) {
        Classes newClass = ClassesMapper.toEntity(classesDTO);
     List<Long> subjectIds = classesDTO.getSubjectIds();
        List<Subject> subject = subjectRepository.findAllById(subjectIds);

        if (subject.size() != subjectIds.size()) {
            throw new EntityNotFoundException("Um ou mais cursos não encontrados com os IDs: " + subjectIds);
        }

        newClass.setSubjects(subject);

        if (classesDTO.getStudentCpfs() != null && !classesDTO.getStudentCpfs().isEmpty()) {
        List<Student> studentList = userRepository.findStudentsByCpf(classesDTO.getStudentCpfs()) 
            .orElseThrow(() -> new EntityNotFoundException("Um ou mais alunos não encontrados ou não são do tipo STUDENT"));
        
        if (studentList.isEmpty()) {
            throw new EntityNotFoundException("Um ou mais alunos não encontrados ou não são do tipo STUDENT");
        }

    Set<Student> students = new HashSet<>(studentList);
    newClass.setStudents(students);
}


        Classes savedClass = classesRepository.save(newClass);

        return ClassesMapper.toDto(savedClass);
    }

    public ClassesDTO getClassById(Long id) {
        Classes classes = classesRepository.findByIdWithRelations(id)
                .orElseThrow(() -> new EntityNotFoundException("Turma não encontrada com ID: " + id));
        return ClassesMapper.toDto(classes);
    }

    public Page<ClassesDTO> getAllClasses(Pageable pageable) {
        return classesRepository.findAllWithRelations(pageable)
                .map(ClassesMapper::toDto);
    }

    @Transactional
    public ClassesDTO addStudentsToClass(Long classId, List<String> studentCpfs) {
        Classes existingClass = classesRepository.findById(classId)
                .orElseThrow(() -> new ResourceNotFoundException("Turma não encontrada com ID: " + classId));

        List<Student> studentsToAdd = userRepository.findStudentsByCpf(studentCpfs)
                .orElseThrow(() -> new ResourceNotFoundException("Um ou mais alunos não encontrados"));

        studentsToAdd.forEach(student -> {
            if (!existingClass.getStudents().contains(student)) {
                student.setClasses(existingClass);
                existingClass.getStudents().add(student);
            }
        });

        Classes updatedClass = classesRepository.save(existingClass);
        return ClassesMapper.toDto(updatedClass);
    }

    public ClassesDTO updateClass(Long id, ClassesDTO classesDTO) {
        Classes existingClass = classesRepository.findByIdWithRelations(id)
                .orElseThrow(() -> new EntityNotFoundException("Turma não encontrada com ID: " + id));

        existingClass.setName(classesDTO.getName());


        List<Long> newSubjectIds = classesDTO.getSubjectIds();
        List<Subject> newSubjects = subjectRepository.findAllById(newSubjectIds);

        if (newSubjects.size() != newSubjectIds.size()) {
            throw new EntityNotFoundException("Um ou mais cursos não encontrados com os IDs: " + newSubjectIds);
        }

        List<Long> currentSubjectIds = existingClass.getSubjects().stream()
            .map(Subject::getId)
            .collect(Collectors.toList());

        if (!currentSubjectIds.containsAll(newSubjectIds) || !newSubjectIds.containsAll(currentSubjectIds)) {
            existingClass.setSubjects(newSubjects);
        }


        List<String> currentStudentCpfs = existingClass.getStudents().stream()
                .map(Student::getCpf)
                .collect(Collectors.toList());

        if (!currentStudentCpfs.equals(classesDTO.getStudentCpfs())) {
            List<Student> newStudents = userRepository.findStudentsByCpf(classesDTO.getStudentCpfs())
                    .orElseThrow(() -> new EntityNotFoundException("Um ou mais alunos não encontrados ou não são do tipo STUDENT"));
            Set<Student> students = new HashSet<>(newStudents);

            existingClass.setStudents(students);
        }

        Classes updatedClass = classesRepository.save(existingClass);
        return ClassesMapper.toDto(updatedClass);
    }

    public void deleteClass(Long id) {
        Classes classes = classesRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Turma não encontrada com ID: " + id));
        classesRepository.delete(classes);
    }

    public List<String> getStudentsInClass(Long classId) {
    Classes classe = classesRepository.findByIdWithRelations(classId)
            .orElseThrow(() -> new ResourceNotFoundException("Turma não encontrada com ID: " + classId));

    return classe.getStudents().stream()
            .map(Student::getCpf) 
            .collect(Collectors.toList());
    }

}
