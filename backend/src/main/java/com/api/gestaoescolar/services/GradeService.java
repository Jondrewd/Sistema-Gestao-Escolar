package com.api.gestaoescolar.services;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.api.gestaoescolar.dtos.GradeDTO;
import com.api.gestaoescolar.entities.Evaluation;
import com.api.gestaoescolar.entities.Grade;
import com.api.gestaoescolar.entities.Student;
import com.api.gestaoescolar.exceptions.ResourceNotFoundException;
import com.api.gestaoescolar.mappers.GradeMapper;
import com.api.gestaoescolar.repositories.EvaluationRepository;
import com.api.gestaoescolar.repositories.GradeRepository;
import com.api.gestaoescolar.repositories.UserRepository;


@Service
@Transactional
public class GradeService {

    private final GradeRepository gradeRepository;
    private final EvaluationRepository evaluationRepository;
    private final UserRepository studentRepository;

    public GradeService(GradeRepository gradeRepository,
                       EvaluationRepository evaluationRepository,
                       UserRepository studentRepository) {
        this.gradeRepository = gradeRepository;
        this.evaluationRepository = evaluationRepository;
        this.studentRepository = studentRepository;
    }

    @Transactional(readOnly = true)
    public GradeDTO findById(Long id) {
        Grade grade = gradeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Nota não encontrada com ID: " + id));
        return GradeMapper.toDTO(grade);
    }

    @Transactional(readOnly = true)
    public List<GradeDTO> findAll() {
        return GradeMapper.toDtoList(gradeRepository.findAll());
    }

    @Transactional(readOnly = true)
    public Page<GradeDTO> findAll(Pageable pageable) {
        return gradeRepository.findAll(pageable)
                .map(GradeMapper::toDTO);
    }

    @Transactional(readOnly = true)
    public List<GradeDTO> findByEvaluation(Long evaluationId) {
        Evaluation evaluation = evaluationRepository.findById(evaluationId)
                .orElseThrow(() -> new ResourceNotFoundException("Avaliação não encontrada com ID: " + evaluationId));
        return GradeMapper.toDtoList(gradeRepository.findByEvaluation(evaluation));
    }

    @Transactional(readOnly = true)
    public List<GradeDTO> findByStudent(String studentCpf) {
        Student student = studentRepository.findStudentByCpf(studentCpf)
                .orElseThrow(() -> new ResourceNotFoundException("Aluno não encontrado com CPF: " + studentCpf));
        return GradeMapper.toDtoList(gradeRepository.findByStudent(student));
    }

    @Transactional
    public GradeDTO create(GradeDTO gradeDTO) {
        validateGrade(gradeDTO);

        Grade grade = new Grade();
        
        Evaluation evaluation = evaluationRepository.findById(gradeDTO.getEvaluation().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Avaliação não encontrada com ID: " + gradeDTO.getEvaluation().getId()));
        grade.setEvaluation(evaluation);

        Student student = studentRepository.findStudentByCpf(gradeDTO.getStudent())
                .orElseThrow(() -> new ResourceNotFoundException("Aluno não encontrado com CPF: " + gradeDTO.getStudent()));
        grade.setStudent(student);

        grade.setScore(gradeDTO.getScore());

        Grade savedGrade = gradeRepository.save(grade);
        return GradeMapper.toDTO(savedGrade);
    }

    @Transactional
    public GradeDTO update(Long id, GradeDTO gradeDTO) {
        Grade existingGrade = gradeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Nota não encontrada com ID: " + id));

        validateGrade(gradeDTO);

        if (gradeDTO.getEvaluation() != null) {
            Evaluation evaluation = evaluationRepository.findById(gradeDTO.getEvaluation().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Avaliação não encontrada com ID: " + gradeDTO.getEvaluation().getId()));
            existingGrade.setEvaluation(evaluation);
        }

        if (gradeDTO.getStudent() != null) {
            Student student = studentRepository.findStudentByCpf(gradeDTO.getStudent())
                    .orElseThrow(() -> new ResourceNotFoundException("Aluno não encontrado com CPF: " + gradeDTO.getStudent()));
            existingGrade.setStudent(student);
        }

        if (gradeDTO.getScore() != null) {
            existingGrade.setScore(gradeDTO.getScore());
        }

        Grade updatedGrade = gradeRepository.save(existingGrade);
        return GradeMapper.toDTO(updatedGrade);
    }

    @Transactional
    public void delete(Long id) {
        if (!gradeRepository.existsById(id)) {
            throw new ResourceNotFoundException("Nota não encontrada com ID: " + id);
        }
        gradeRepository.deleteById(id);
    }

    @Transactional
    public void deleteByEvaluation(Long evaluationId) {
        Evaluation evaluation = evaluationRepository.findById(evaluationId)
                .orElseThrow(() -> new ResourceNotFoundException("Avaliação não encontrada com ID: " + evaluationId));
        gradeRepository.deleteByEvaluation(evaluation);
    }

    @Transactional(readOnly = true)
    public Double calculateStudentAverage(String studentCpf) {
        Student student = studentRepository.findStudentByCpf(studentCpf)
                .orElseThrow(() -> new ResourceNotFoundException("Aluno não encontrado com CPF: " + studentCpf));
        
        List<Grade> grades = gradeRepository.findByStudent(student);
        
        if (grades.isEmpty()) {
            return 0.0;
        }
        
        double sum = grades.stream()
                .mapToDouble(Grade::getScore)
                .sum();
                
        return sum / grades.size();
    }

    private void validateGrade(GradeDTO gradeDTO) {
        if (gradeDTO.getScore() != null && (gradeDTO.getScore() < 0 || gradeDTO.getScore() > 10)) {
            throw new IllegalArgumentException("A nota deve estar entre 0 e 10");
        }
    }
}