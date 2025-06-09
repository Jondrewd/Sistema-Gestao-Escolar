import { useState, useEffect } from 'react';
import './ExamScheduler.css';
import api from '../../Service/Api';

const ExamScheduler = ({ classId }) => {
  const [examDate, setExamDate] = useState('');
  const [selectedSubject, setSelectedSubject] = useState('');
  const [subjects, setSubjects] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [successMessage, setSuccessMessage] = useState('');

  useEffect(() => {
    const fetchClassSubjects = async () => {
      try {
        setLoading(true);
        const response = await api.get(`/classes/${classId}`);
        const classData = response.data;
        
        const uniqueSubjects = classData.lessons.reduce((acc, lesson) => {
          if (!acc.some(subj => subj.id === lesson.subject.id)) {
            acc.push({
              id: lesson.subject.id,
              name: lesson.subject.name
            });
          }
          return acc;
        }, []);
        
        setSubjects(uniqueSubjects);
        
        if (uniqueSubjects.length > 0) {
          setSelectedSubject(uniqueSubjects[0].id);
        }
        
        const defaultDate = new Date();
        defaultDate.setDate(defaultDate.getDate() + 7);
        setExamDate(defaultDate.toISOString().split('T')[0]);
      } catch (err) {
        console.error('Erro ao carregar matérias:', err);
        setError('Erro ao carregar matérias da turma');
      } finally {
        setLoading(false);
      }
    };

    if (classId) {
      fetchClassSubjects();
    }
  }, [classId]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    if (!examDate || !selectedSubject || !classId) {
      alert('Preencha todos os campos obrigatórios');
      return;
    }

    setIsSubmitting(true);
    
    try {
      const selectedSubjectData = subjects.find(subj => subj.id === selectedSubject);
      
      const examData = {
        date: examDate,
        subjectId: selectedSubject,
        subjectName: selectedSubjectData?.name || '',
        classId: parseInt(classId)
      };

      await api.post('/evaluations', examData);
      
      setSuccessMessage(`Prova de ${selectedSubjectData?.name} agendada para ${new Date(examDate).toLocaleDateString('pt-BR')}`);
      setExamDate('');
    } catch (err) {
      console.error('Erro ao agendar prova:', err);
      setError('Erro ao agendar prova. Tente novamente.');
    } finally {
      setIsSubmitting(false);
    }
  };

  if (loading) {
    return <div className="loading">Carregando matérias...</div>;
  }

  if (error) {
    return <div className="error">{error}</div>;
  }

  if (subjects.length === 0) {
    return <div className="empty-state">Nenhuma matéria encontrada para esta turma</div>;
  }

  return (
    <div className="exam-scheduler">
      <h2>Agendar Nova Prova</h2>
      
      {successMessage && (
        <div className="success-message">
          <svg viewBox="0 0 20 20" fill="currentColor">
            <path fillRule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clipRule="evenodd" />
          </svg>
          {successMessage}
        </div>
      )}

      <form onSubmit={handleSubmit} className="exam-form">
        <div className="form-group">
          <label htmlFor="subject">Matéria:</label>
          <select
            id="subject"
            value={selectedSubject}
            onChange={(e) => setSelectedSubject(e.target.value)}
            disabled={isSubmitting}
            required
          >
            {subjects.map(subject => (
              <option key={subject.id} value={subject.id}>
                {subject.name}
              </option>
            ))}
          </select>
        </div>

        <div className="form-group">
          <label htmlFor="examDate">Data da Prova:</label>
          <input
            type="date"
            id="examDate"
            value={examDate}
            onChange={(e) => setExamDate(e.target.value)}
            min={new Date().toISOString().split('T')[0]}
            required
            disabled={isSubmitting}
          />
        </div>

        <button 
          type="submit" 
          className="submit-button"
          disabled={isSubmitting}
        >
          {isSubmitting ? 'Agendando...' : 'Agendar Prova'}
        </button>
      </form>
    </div>
  );
};

export default ExamScheduler;