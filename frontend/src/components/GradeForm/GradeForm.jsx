import { useState, useEffect } from 'react';
import './GradeForm.css';
import api from '../../Service/Api';

const GradeForm = ({ classId }) => {
  const [students, setStudents] = useState([]);
  const [grades, setGrades] = useState({});
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [isSubmitting, setIsSubmitting] = useState(false);

  useEffect(() => {
    const fetchStudents = async () => {
      try {
        setLoading(true);
        const response = await api.get(`/classes/${classId}/students`);
        setStudents(response.data);
        
        // Inicializa grades com os valores existentes
        const initialGrades = response.data.reduce((acc, student) => {
          acc[student.id] = student.grade || '';
          return acc;
        }, {});
        setGrades(initialGrades);
      } catch (err) {
        console.error('Erro ao carregar alunos:', err);
        setError('Erro ao carregar lista de alunos');
      } finally {
        setLoading(false);
      }
    };

    if (classId) {
      fetchStudents();
    }
  }, [classId]);

  const handleGradeChange = (studentId, value) => {
    // Validação básica do valor
    const numericValue = parseFloat(value);
    if (value === '' || (numericValue >= 0 && numericValue <= 10)) {
      setGrades(prev => ({
        ...prev,
        [studentId]: value
      }));
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setIsSubmitting(true);
    
    try {
      // Filtra apenas as grades que foram alteradas
      const gradesToSend = Object.entries(grades).reduce((acc, [studentId, grade]) => {
        if (grade !== '') {
          acc.push({ studentId, grade: parseFloat(grade) });
        }
        return acc;
      }, []);

      await api.post(`/classes/${classId}/grades`, { grades: gradesToSend });
      alert('Notas salvas com sucesso!');
    } catch (err) {
      console.error('Erro ao salvar notas:', err);
      alert('Erro ao salvar notas. Tente novamente.');
    } finally {
      setIsSubmitting(false);
    }
  };

  if (loading) {
    return <div className="loading">Carregando alunos...</div>;
  }

  if (error) {
    return <div className="error">{error}</div>;
  }

  if (students.length === 0) {
    return <div className="empty-state">Nenhum aluno encontrado nesta turma</div>;
  }

  return (
    <form onSubmit={handleSubmit} className="grade-form">
      <table>
        <thead>
          <tr>
            <th>Aluno</th>
            <th>Nota (0-10)</th>
          </tr>
        </thead>
        <tbody>
          {students.map(student => (
            <tr key={student.id}>
              <td>{student.name}</td>
              <td>
                <input
                  type="number"
                  min="0"
                  max="10"
                  step="0.1"
                  value={grades[student.id] || ''}
                  onChange={(e) => handleGradeChange(student.id, e.target.value)}
                  placeholder="Digite a nota"
                  disabled={isSubmitting}
                />
              </td>
            </tr>
          ))}
        </tbody>
      </table>
      
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