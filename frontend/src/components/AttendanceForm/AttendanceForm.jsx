import { useState, useEffect } from 'react';
import './AttendanceForm.css';
import api from '../../Service/Api';

const AttendanceForm = ({ classId }) => {
  const [studentCpfs, setStudentCpfs] = useState([]);
  const [studentData, setStudentData] = useState({});
  const [attendance, setAttendance] = useState({});
  const [lessons, setLessons] = useState([]);
  const [selectedLesson, setSelectedLesson] = useState('');
  const [classDate, setClassDate] = useState(new Date().toISOString().split('T')[0]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [isSubmitting, setIsSubmitting] = useState(false);

  useEffect(() => {
    const fetchClassData = async () => {
      try {
        setLoading(true);
        
        const classResponse = await api.get(`/classes/${classId}`);
        const classData = classResponse.data;
        
        setLessons(classData.lessons);
        
        setStudentCpfs(classData.studentCpfs);
        
        const studentsInfo = {};
        for (const cpf of classData.studentCpfs) {
          const studentResponse = await api.get(`/students/${cpf}`);
          studentsInfo[cpf] = studentResponse.data;
        }
        setStudentData(studentsInfo);
        
        const initialAttendance = classData.studentCpfs.reduce((acc, cpf) => {
          acc[cpf] = true;
          return acc;
        }, {});
        setAttendance(initialAttendance);
        
        if (classData.lessons.length > 0) {
          setSelectedLesson(classData.lessons[0].id);
        }
      } catch (err) {
        console.error('Erro ao carregar dados da turma:', err);
        setError('Erro ao carregar dados da turma');
      } finally {
        setLoading(false);
      }
    };

    if (classId) {
      fetchClassData();
    }
  }, [classId]);

  const handleAttendanceChange = (cpf, isPresent) => {
    setAttendance(prev => ({
      ...prev,
      [cpf]: isPresent
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    if (!selectedLesson) {
      alert('Selecione uma aula antes de registrar');
      return;
    }

    setIsSubmitting(true);
    
    try {
      const selectedLessonData = lessons.find(lesson => lesson.id === selectedLesson);
      
      if (!selectedLessonData) {
        throw new Error('Aula selecionada não encontrada');
      }

      const isoDate = new Date(classDate).toISOString();
      
      const attendanceRecords = studentCpfs.map(cpf => ({
        date: isoDate,
        present: attendance[cpf],
        student: cpf,
        subjectId: selectedLessonData.subject.id,
        subjectName: selectedLessonData.subject.name
      }));

      const requests = attendanceRecords.map(record => 
        api.post('/attendances', record)
      );

      await Promise.all(requests);
      
      alert('Presenças registradas com sucesso!');
    } catch (err) {
      console.error('Erro ao registrar presenças:', err);
      alert('Erro ao registrar presenças. Tente novamente.');
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

  if (lessons.length === 0) {
    return <div className="empty-state">Nenhuma aula encontrada para esta turma</div>;
  }

  return (
    <form onSubmit={handleSubmit} className="attendance-form">
      <div className="form-control">
        <label htmlFor="lesson">Aula:</label>
        <select
          id="lesson"
          value={selectedLesson}
          onChange={(e) => setSelectedLesson(e.target.value)}
          disabled={isSubmitting}
        >
          {lessons.map(lesson => (
            <option key={lesson.id} value={lesson.id}>
              {lesson.subject.name} - {lesson.dayOfWeek.replace('_', ' ')} {lesson.startTime}-{lesson.endTime}
            </option>
          ))}
        </select>
      </div>

      <div className="form-control">
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

      <div className="table-container">
        <table>
          <thead>
            <tr>
              <th>Aluno</th>
              <th>CPF</th>
              <th>Presente</th>
              <th>Ausente</th>
            </tr>
          </thead>
          <tbody>
            {studentCpfs.map(cpf => (
              <tr key={cpf}>
                <td>{studentData[cpf]?.fullName || 'Carregando...'}</td>
                <td>{cpf}</td>
                <td>
                  <input
                    type="radio"
                    name={`attendance-${cpf}`}
                    checked={attendance[cpf] === true}
                    onChange={() => handleAttendanceChange(cpf, true)}
                    disabled={isSubmitting}
                  />
                </td>
                <td>
                  <input
                    type="radio"
                    name={`attendance-${cpf}`}
                    checked={attendance[cpf] === false}
                    onChange={() => handleAttendanceChange(cpf, false)}
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
        disabled={isSubmitting}
      >
        {isSubmitting ? 'Registrando...' : 'Registrar Presenças'}
      </button>
    </form>
  );
};

export default AttendanceForm;