package com.api.gestaoescolar.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.api.gestaoescolar.dtos.AttendanceDTO;
import com.api.gestaoescolar.entities.Attendance;
import com.api.gestaoescolar.entities.Student;
import com.api.gestaoescolar.entities.Subject;
import com.api.gestaoescolar.exceptions.ResourceNotFoundException;
import com.api.gestaoescolar.mappers.AttendanceMapper;
import com.api.gestaoescolar.repositories.AttendanceRepository;
import com.api.gestaoescolar.repositories.SubjectRepository;
import com.api.gestaoescolar.repositories.UserRepository;

@Service
@Transactional
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final UserRepository userRepository;
    private final SubjectRepository subjectRepository;

    public AttendanceService(AttendanceRepository attendanceRepository, UserRepository userRepository, SubjectRepository subjectRepository) {
        this.attendanceRepository = attendanceRepository;
        this.userRepository = userRepository;
        this.subjectRepository = subjectRepository;
    }

    @Transactional(readOnly = true)
    public AttendanceDTO findById(Long id) {
        Attendance attendance = attendanceRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Registro de presença não encontrado com ID: " + id));
        return AttendanceMapper.toDTO(attendance);
    }

    @Transactional(readOnly = true)
    public Page<AttendanceDTO> findAll(Pageable pageable) {
        return attendanceRepository.findAll(pageable)
            .map(AttendanceMapper::toDTO);
    }

    @Transactional
    public AttendanceDTO create(AttendanceDTO attendanceDTO) {
        Attendance attendance = AttendanceMapper.toEntity(attendanceDTO);

        attendance.setStudent(userRepository.findStudentByCpf(attendanceDTO.getStudent())
            .orElseThrow(() -> new ResourceNotFoundException("Aluno não encontrado." + attendanceDTO.getStudent())));

        attendance.setSubject(subjectRepository.findById(attendanceDTO.getSubjectId())
            .orElseThrow(() -> new ResourceNotFoundException("Materia não encontrada.")));
        
        Attendance savedAttendance = attendanceRepository.save(attendance);
        return AttendanceMapper.toDTO(savedAttendance);
    }

    @Transactional
    public void delete(Long id) {
        if (!attendanceRepository.existsById(id)) {
            throw new ResourceNotFoundException("Registro de presença não encontrado com ID: " + id);
        }
        attendanceRepository.deleteById(id);
    }

    @Transactional
public AttendanceDTO update(Long id, AttendanceDTO dto) {
    Attendance entity = attendanceRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Presença não encontrada"));
    
    AttendanceMapper.updateFromDto(dto, entity);
    
    if (dto.getStudent() != null) {
        Student student = userRepository.findStudentByCpf(dto.getStudent())
            .orElseThrow(() -> new ResourceNotFoundException("Aluno não encontrado"));
        entity.setStudent(student);
    }
    
    if (dto.getSubjectId() != null) {
        Subject subject = subjectRepository.findById(dto.getSubjectId())
            .orElseThrow(() -> new ResourceNotFoundException("Turma não encontrada"));
        entity.setSubject(subject);
    }
    
    Attendance updated = attendanceRepository.save(entity);
    return AttendanceMapper.toDTO(updated);
}
    public Page<AttendanceDTO> findByStudentCpf(String cpf, Pageable pageable){
        Page<AttendanceDTO> attendanceDTOs = attendanceRepository.findByStudentCpf(cpf, pageable)
            .map(AttendanceMapper::toDTO);
        return attendanceDTOs;
    }
}