package com.api.gestaoescolar.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.api.gestaoescolar.dtos.EvaluationDTO;
import com.api.gestaoescolar.entities.Course;
import com.api.gestaoescolar.entities.Evaluation;
import com.api.gestaoescolar.entities.Student;
import com.api.gestaoescolar.exceptions.ResourceNotFoundException;
import com.api.gestaoescolar.mappers.EvaluationMapper;
import com.api.gestaoescolar.repositories.EvaluationRepository;
import com.api.gestaoescolar.repositories.UserRepository;
import com.api.gestaoescolar.repositories.CourseRepository;

@Service
@Transactional
public class EvaluationService {

    private final EvaluationRepository evaluationRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;

    public EvaluationService(EvaluationRepository evaluationRepository,
                           UserRepository userRepository,
                           CourseRepository courseRepository) {
        this.evaluationRepository = evaluationRepository;
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
    }

    @Transactional(readOnly = true)
    public EvaluationDTO findById(Long id) {
        Evaluation evaluation = evaluationRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Avaliação não encontrada com ID: " + id));
        return EvaluationMapper.toDto(evaluation);
    }

    @Transactional(readOnly = true)
    public Page<EvaluationDTO> findAll(Pageable pageable) {
        return evaluationRepository.findAll(pageable)
            .map(EvaluationMapper::toDto);
    }

    @Transactional
    public EvaluationDTO create(EvaluationDTO evaluationDTO) {
        Evaluation evaluation = EvaluationMapper.toEntity(evaluationDTO);

        Student student = userRepository.findStudentByUsername(evaluationDTO.getStudent())
            .orElseThrow(() -> new ResourceNotFoundException("Aluno não encontrado: " + evaluationDTO.getStudent()));
        
        Course course = courseRepository.findById(evaluationDTO.getCourse().getId())
            .orElseThrow(() -> new ResourceNotFoundException("Curso não encontrado com ID: " + evaluationDTO.getCourse().getId()));
        
        evaluation.setStudent(student);
        evaluation.setCourse(course);
        
        Evaluation savedEvaluation = evaluationRepository.save(evaluation);
        return EvaluationMapper.toDto(savedEvaluation);
    }

    @Transactional
    public EvaluationDTO update(Long id, EvaluationDTO evaluationDTO) {
        Evaluation existingEvaluation = evaluationRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Avaliação não encontrada com ID: " + id));

        if (evaluationDTO.getDate() != null) {
            existingEvaluation.setDate(evaluationDTO.getDate());
        }
        if (evaluationDTO.getScore() != null) {
            existingEvaluation.setScore(evaluationDTO.getScore());
        }

        if (evaluationDTO.getStudent() != null && 
            !evaluationDTO.getStudent().equals(existingEvaluation.getStudent().getUsername())) {
            Student student = userRepository.findStudentByUsername(evaluationDTO.getStudent())
                .orElseThrow(() -> new ResourceNotFoundException("Aluno não encontrado: " + evaluationDTO.getStudent()));
            existingEvaluation.setStudent(student);
        }

        if (evaluationDTO.getCourse().getId() != null && 
            !evaluationDTO.getCourse().getId().equals(existingEvaluation.getCourse().getId())) {
            Course course = courseRepository.findById(evaluationDTO.getCourse().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Curso não encontrado com ID: " + evaluationDTO.getCourse().getId()));
            existingEvaluation.setCourse(course);
        }

        Evaluation updatedEvaluation = evaluationRepository.save(existingEvaluation);
        return EvaluationMapper.toDto(updatedEvaluation);
    }

    @Transactional
    public void delete(Long id) {
        if (!evaluationRepository.existsById(id)) {
            throw new ResourceNotFoundException("Avaliação não encontrada com ID: " + id);
        }
        evaluationRepository.deleteById(id);
    }
}