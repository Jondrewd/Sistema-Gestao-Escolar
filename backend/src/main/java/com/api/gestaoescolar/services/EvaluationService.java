package com.api.gestaoescolar.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.api.gestaoescolar.dtos.EvaluationDTO;
import com.api.gestaoescolar.entities.Subject;
import com.api.gestaoescolar.entities.Evaluation;
import com.api.gestaoescolar.entities.Student;
import com.api.gestaoescolar.exceptions.ResourceNotFoundException;
import com.api.gestaoescolar.mappers.EvaluationMapper;
import com.api.gestaoescolar.repositories.EvaluationRepository;
import com.api.gestaoescolar.repositories.UserRepository;
import com.api.gestaoescolar.repositories.SubjectRepository;

@Service
@Transactional
public class EvaluationService {

    private final EvaluationRepository evaluationRepository;
    private final UserRepository userRepository;
    private final SubjectRepository subjectRepository;

    public EvaluationService(EvaluationRepository evaluationRepository,
                           UserRepository userRepository,
                           SubjectRepository subjectRepository) {
        this.evaluationRepository = evaluationRepository;
        this.userRepository = userRepository;
        this.subjectRepository = subjectRepository;
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

        Student student = (Student) userRepository.findByCpf(evaluationDTO.getStudent())
            .orElseThrow(() -> new ResourceNotFoundException("Aluno não encontrado: " + evaluationDTO.getStudent()));
        
        Subject subject = subjectRepository.findById(evaluationDTO.getSubjectId())
            .orElseThrow(() -> new ResourceNotFoundException("Curso não encontrado com ID: " + evaluationDTO.getSubjectId()));
        
        evaluation.setStudent(student);
        evaluation.setSubject(subject);
        
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
            !evaluationDTO.getStudent().equals(existingEvaluation.getStudent().getCpf())) {
            Student student = (Student) userRepository.findByCpf(evaluationDTO.getStudent())
                .orElseThrow(() -> new ResourceNotFoundException("Aluno não encontrado: " + evaluationDTO.getStudent()));
            existingEvaluation.setStudent(student);
        }

        if (evaluationDTO.getSubjectId() != null && 
            !evaluationDTO.getSubjectId().equals(existingEvaluation.getSubject().getId())) {
            Subject subject = subjectRepository.findById(evaluationDTO.getSubjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Curso não encontrado com ID: " + evaluationDTO.getSubjectId()));
            existingEvaluation.setSubject(subject);
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