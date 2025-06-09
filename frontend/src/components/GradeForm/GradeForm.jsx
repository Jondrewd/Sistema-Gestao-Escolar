import { useState, useEffect } from 'react';
import './GradeForm.css';
import api from '../../Service/Api';

const GradeForm = ({ classId }) => {
  const [studentCpfs, setStudentCpfs] = useState([]);
  const [studentData, setStudentData] = useState({});
  const [grades, setGrades] = useState({});
  const [evaluations, setEvaluations] = useState([]);
  const [selectedEvaluation, setSelectedEvaluation] = useState('');
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [isSubmitting, setIsSubmitting] = useState(false);

  useEffect(() => {
    const fetchClassData = async () => {
      try {
        setLoading(true);
        
        const classResponse = await api.get(`/classes/${classId}`);
        const classData = classResponse.data;
        
        const evaluationsResponse = await api.get(`/evaluations/class/${classId}`);
        const evaluationsData = evaluationsResponse.data?.content || [];

        setEvaluations(evaluationsData);
        setStudentCpfs(classData.studentCpfs);
        
        const studentsInfo = {};
        for (const cpf of classData.studentCpfs) {
          const studentResponse = await api.get(`/students/${cpf}`);
          studentsInfo[cpf] = studentResponse.data;
        }
        setStudentData(studentsInfo);
        
        const initialGrades = classData.studentCpfs.reduce((acc, cpf) => {
          acc[cpf] = '';
          return acc;
        }, {});
        setGrades(initialGrades);
        
        if (evaluationsData.length > 0) {
          setSelectedEvaluation(evaluationsData[0].id);
        }
      } catch (err) {
        console.error('Erro ao carregar dados:', err);
        setError('Erro ao carregar dados da turma');
      } finally {
        setLoading(false);
      }
    };

    if (classId) {
      fetchClassData();
    }
  }, [classId]);

  const handleGradeChange = (cpf, value) => {
    const numericValue = parseFloat(value);
    if (value === '' || (!isNaN(numericValue) && numericValue >= 0 && numericValue <= 10)) {
      setGrades(prev => ({
        ...prev,
        [cpf]: value
      }));
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    if (!selectedEvaluation) {
      alert('Selecione uma avaliação antes de salvar');
      return;
    }

    setIsSubmitting(true);
    
    try {
      const selectedEvalData = evaluations.find(e => e.id === selectedEvaluation);
      
      if (!selectedEvalData) {
        throw new Error('Avaliação selecionada não encontrada');
      }

      const gradesToSend = Object.entries(grades)
        .filter(([_, grade]) => grade !== '')
        .map(([cpf, grade]) => ({
          evaluation: { 
            id: selectedEvalData.id,
            date: selectedEvalData.date,
            subjectId: selectedEvalData.subjectId,
            subjectName: selectedEvalData.subjectName,
            classId: selectedEvalData.classId
          },
          student: cpf,
          score: parseFloat(grade)
        }));

      if (gradesToSend.length === 0) {
        alert('Nenhuma nota para salvar');
        return;
      }

      await api.post(`/grades`, gradesToSend);
      alert('Notas salvas com sucesso!');
      
      const resetGrades = studentCpfs.reduce((acc, cpf) => {
        acc[cpf] = '';
        return acc;
      }, {});
      setGrades(resetGrades);
    } catch (err) {
      console.error('Erro ao salvar notas:', err);
      alert('Erro ao salvar notas. Tente novamente.');
    } finally {
      setIsSubmitting(false);
    }
  };

  if (loading) {
    return <div className="loading">Carregando dados da turma...</div>;
  }

  if (error) {
    return <div className="error">{error}</div>;
  }

  if (studentCpfs.length === 0) {
    return <div className="empty-state">Nenhum aluno encontrado nesta turma</div>;
  }

  if (evaluations.length === 0) {
    return <div className="empty-state">Nenhuma avaliação encontrada para esta turma</div>;
  }

  return (
    <form onSubmit={handleSubmit} className="grade-form">
      <div className="form-control">
        <label htmlFor="evaluation">Avaliação:</label>
        <select
          id="evaluation"
          value={selectedEvaluation}
          onChange={(e) => setSelectedEvaluation(e.target.value)}
          disabled={isSubmitting}
          required
        >
          {evaluations.map(evaluation => (
            <option key={evaluation.id} value={evaluation.id}>
              {evaluation.subjectName} - {new Date(evaluation.date).toLocaleDateString('pt-BR')}
            </option>
          ))}
        </select>
      </div>

      <div className="table-container">
        <table>
          <thead>
            <tr>
              <th>Aluno</th>
              <th>CPF</th>
              <th>Nota (0-10)</th>
            </tr>
          </thead>
          <tbody>
            {studentCpfs.map(cpf => (
              <tr key={cpf}>
                <td>{studentData[cpf]?.fullName || 'Carregando...'}</td>
                <td>{cpf}</td>
                <td>
                  <input
                    type="number"
                    min="0"
                    max="10"
                    step="0.1"
                    value={grades[cpf] || ''}
                    onChange={(e) => handleGradeChange(cpf, e.target.value)}
                    placeholder="0.0"
                    disabled={isSubmitting}
                  />
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
      
      <button 
        type="submit" 
        className="save-button"
        disabled={isSubmitting || Object.values(grades).every(g => g === '')}
      >
        {isSubmitting ? 'Salvando...' : 'Salvar Notas'}
      </button>
    </form>
  );
};

export default GradeForm;