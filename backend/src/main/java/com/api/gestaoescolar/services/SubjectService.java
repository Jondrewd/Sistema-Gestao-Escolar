package com.api.gestaoescolar.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.api.gestaoescolar.dtos.SubjectDTO;
import com.api.gestaoescolar.entities.Subject;
import com.api.gestaoescolar.exceptions.ResourceNotFoundException;
import com.api.gestaoescolar.mappers.SubjectMapper;
import com.api.gestaoescolar.repositories.SubjectRepository;

@Service
@Transactional
public class SubjectService {

    private final SubjectRepository subjectRepository;

    public SubjectService(
        SubjectRepository subjectRepository
    ) {
        this.subjectRepository = subjectRepository;
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
        Subject saved = subjectRepository.save(subject);
        return SubjectMapper.toDto(saved);
    }

    @Transactional
    public SubjectDTO update(Long id, SubjectDTO dto) {
        Subject subject = subjectRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Disciplina não encontrada com ID: " + id));

        if (dto.getName() != null) subject.setName(dto.getName());
  
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
