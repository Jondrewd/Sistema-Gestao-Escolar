package com.api.gestaoescolar.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.api.gestaoescolar.dtos.EvaluationDTO;
import com.api.gestaoescolar.entities.Subject;
import com.api.gestaoescolar.entities.Classes;
import com.api.gestaoescolar.entities.Evaluation;
import com.api.gestaoescolar.exceptions.ResourceNotFoundException;
import com.api.gestaoescolar.mappers.EvaluationMapper;
import com.api.gestaoescolar.repositories.ClassesRepository;
import com.api.gestaoescolar.repositories.EvaluationRepository;
import com.api.gestaoescolar.repositories.SubjectRepository;

@Service
@Transactional
public class EvaluationService {

    private final EvaluationRepository evaluationRepository;
    private final SubjectRepository subjectRepository;
    private final ClassesRepository classesRepository;

    public EvaluationService(EvaluationRepository evaluationRepository,
                           SubjectRepository subjectRepository,
                           ClassesRepository classesRepository) {
        this.evaluationRepository = evaluationRepository;
        this.subjectRepository = subjectRepository;
        this.classesRepository = classesRepository;
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
        Evaluation evaluation = new Evaluation();
        evaluation.setDate(evaluationDTO.getDate());

        Subject subject = subjectRepository.findById(evaluationDTO.getSubjectId())
            .orElseThrow(() -> new ResourceNotFoundException("Matéria não encontrada com ID: " + evaluationDTO.getSubjectId()));
        evaluation.setSubject(subject);

        Classes classes = classesRepository.findById(evaluationDTO.getClassId())
            .orElseThrow(() -> new ResourceNotFoundException("Turma não encontrada com ID: " + evaluationDTO.getClassId()));
        evaluation.setClasses(classes);

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

        if (evaluationDTO.getSubjectId() != null) {
            Subject subject = subjectRepository.findById(evaluationDTO.getSubjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Matéria não encontrada com ID: " + evaluationDTO.getSubjectId()));
            existingEvaluation.setSubject(subject);
        }

        if (evaluationDTO.getClassId() != null) {
            Classes classes = classesRepository.findById(evaluationDTO.getClassId())
                .orElseThrow(() -> new ResourceNotFoundException("Turma não encontrada com ID: " + evaluationDTO.getClassId()));
            existingEvaluation.setClasses(classes);
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