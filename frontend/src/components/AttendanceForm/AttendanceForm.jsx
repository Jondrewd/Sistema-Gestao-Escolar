import { useState, useEffect } from 'react';
import './AttendanceForm.css';
import api from '../../Service/Api';

const AttendanceForm = ({ classId }) => {
  const [students, setStudents] = useState([]);
  const [attendance, setAttendance] = useState({});
  const [classDate, setClassDate] = useState(new Date().toISOString().split('T')[0]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [isSubmitting, setIsSubmitting] = useState(false);

  useEffect(() => {
    const fetchStudents = async () => {
      try {
        setLoading(true);
        const response = await api.get(`/classes/${classId}/students`);
        setStudents(response.data);
        
        const initialAttendance = response.data.reduce((acc, student) => {
          acc[student.id] = true;
          return acc;
        }, {});
        setAttendance(initialAttendance);
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

  const handleAttendanceChange = (studentId, isPresent) => {
    setAttendance(prev => ({
      ...prev,
      [studentId]: isPresent
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setIsSubmitting(true);
    
    try {
      const attendanceRecords = Object.entries(attendance).map(([studentId, isPresent]) => ({
        studentId,
        present: isPresent
      }));

      await api.post(`/classes/${classId}/attendance`, {
        date: classDate,
        records: attendanceRecords
      });
      
      alert('Presenças registradas com sucesso!');
    } catch (err) {
      console.error('Erro ao registrar presenças:', err);
      alert('Erro ao registrar presenças. Tente novamente.');
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
    <form onSubmit={handleSubmit} className="attendance-form">
      <div className="form-group">
        <label htmlFor="classDate">Data da Aula:</label>
        <input
          type="date"
          id="classDate"
          value={classDate}
          onChange={(e) => setClassDate(e.target.value)}
          required
          max={new Date().toISOString().split('T')[0]} 
          disabled={isSubmitting}
        />
      </div>
      
      <table>
        <thead>
          <tr>
            <th>Aluno</th>
            <th>Presente</th>
            <th>Ausente</th>
          </tr>
        </thead>
        <tbody>
          {students.map(student => (
            <tr key={student.id}>
              <td>{student.name}</td>
              <td>
                <input
                  type="radio"
                  name={`attendance-${student.id}`}
                  checked={attendance[student.id] === true}
                  onChange={() => handleAttendanceChange(student.id, true)}
                  disabled={isSubmitting}
                />
              </td>
              <td>
                <input
                  type="radio"
                  name={`attendance-${student.id}`}
                  checked={attendance[student.id] === false}
                  onChange={() => handleAttendanceChange(student.id, false)}
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
        disabled={isSubmitting}
      >
        {isSubmitting ? 'Registrando...' : 'Registrar Presenças'}
      </button>
    </form>
  );
};

export default AttendanceForm;