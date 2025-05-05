package com.api.gestaoescolar.services;

import java.time.Instant;
import java.util.List;
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
        private final CourseRepository courseRepository;
        private final AttendanceRepository attendanceRepository;

        public ClassesService(ClassesRepository classesRepository,
                                UserRepository userRepository,
                                CourseRepository courseRepository,
                                AttendanceRepository attendanceRepository) {
                this.classesRepository = classesRepository;
                this.userRepository = userRepository;
                this.courseRepository = courseRepository;
                this.attendanceRepository = attendanceRepository;
        }

        public ClassesDTO createClass(ClassesDTO classesDTO) {
                Classes newClass = ClassesMapper.toEntity(classesDTO);
                
                Teacher teacher = userRepository.findTeacherByUsername(classesDTO.getTeacher())
                        .orElseThrow(() -> new EntityNotFoundException("Professor não encontrado ou não é do tipo TEACHER: " + classesDTO.getTeacher()));
                
                Course course = courseRepository.findById(classesDTO.getCourse().getId())
                        .orElseThrow(() -> new EntityNotFoundException("Curso não encontrado com ID: " + classesDTO.getCourse().getId()));
                
                List<Student> students = userRepository.findStudentsByUsernames(classesDTO.getStudents())
                        .orElseThrow(() -> new EntityNotFoundException("Um ou mais alunos não encontrados ou não são do tipo STUDENT"));
                
                newClass.setTeacher(teacher);
                newClass.setCourse(course);
                newClass.setStudents(students);
                
                Classes savedClass = classesRepository.save(newClass);
                
                createDefaultAttendances(savedClass, students);
                
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
        public ClassesDTO addStudentsToClass(Long classId, List<String> studentUsernames) {
        Classes existingClass = classesRepository.findById(classId)
                .orElseThrow(() -> new ResourceNotFoundException("Turma não encontrada com ID: " + classId));

        List<Student> studentsToAdd = userRepository.findStudentsByUsernames(studentUsernames)
                .orElseThrow(() -> new ResourceNotFoundException("Um ou mais alunos não encontrados"));

        studentsToAdd.forEach(student -> {
                if (!existingClass.getStudents().contains(student)) {
                existingClass.getStudents().add(student);
                }
        });

        Classes updatedClass = classesRepository.save(existingClass);

        createDefaultAttendances(updatedClass, studentsToAdd);

        return ClassesMapper.toDto(updatedClass);
        }


    public ClassesDTO updateClass(Long id, ClassesDTO classesDTO) {
        Classes existingClass = classesRepository.findByIdWithRelations(id)
                .orElseThrow(() -> new EntityNotFoundException("Turma não encontrada com ID: " + id));
        
        existingClass.setName(classesDTO.getName());
        
        if (!existingClass.getTeacher().getUsername().equals(classesDTO.getTeacher())) {
            Teacher newTeacher = userRepository.findTeacherByUsername(classesDTO.getTeacher())
                    .orElseThrow(() -> new EntityNotFoundException("Professor não encontrado ou não é do tipo TEACHER"));
            existingClass.setTeacher(newTeacher);
        }
        
        if (!existingClass.getCourse().getId().equals(classesDTO.getCourse().getId())) {
            Course newCourse = courseRepository.findById(classesDTO.getCourse().getId())
                    .orElseThrow(() -> new EntityNotFoundException("Curso não encontrado"));
            existingClass.setCourse(newCourse);
        }
        
        List<String> currentStudentUsernames = existingClass.getStudents().stream()
                .map(Student::getUsername)
                .collect(Collectors.toList());
        
        if (!currentStudentUsernames.equals(classesDTO.getStudents())) {
            List<Student> newStudents = userRepository.findStudentsByUsernames(classesDTO.getStudents())
                    .orElseThrow(() -> new EntityNotFoundException("Um ou mais alunos não encontrados ou não são do tipo STUDENT"));
            existingClass.setStudents(newStudents);
        }
        
        Classes updatedClass = classesRepository.save(existingClass);
        return ClassesMapper.toDto(updatedClass);
    }

    public void deleteClass(Long id) {
        Classes classes = classesRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Turma não encontrada com ID: " + id));
        
        attendanceRepository.deleteByClassesId(id);
        classesRepository.delete(classes);
    }

    private void createDefaultAttendances(Classes classes, List<Student> students) {
        List<Attendance> attendances = students.stream()
                .map(student -> {
                    Attendance attendance = new Attendance();
                    attendance.setStudent(student);
                    attendance.setClasses(classes);
                    attendance.setPresent(false);
                    attendance.setDate(Instant.now());
                    return attendance;
                })
                .collect(Collectors.toList());
        
        attendanceRepository.saveAll(attendances);
    }
}