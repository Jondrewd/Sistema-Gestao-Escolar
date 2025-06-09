import { useState, useEffect } from 'react';
import './ClassList.css';
import api from '../../Service/Api';

const ClassList = ({ onSelectClass, selectedClass }) => {
  const [classes, setClasses] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchTeacherClasses = async () => {
      try {
        setLoading(true);
        
        const teacherData = JSON.parse(sessionStorage.getItem('userData'));
        if (!teacherData?.lessons) {
          throw new Error('Dados do professor não encontrados');
        }

        const classIds = [...new Set(teacherData.lessons.map(l => l.classId))];

        const classesData = await Promise.all(
          classIds.map(async id => {
            const response = await api.get(`/classes/${id}`);
            return response.data;
          })
        );

        const enrichedClasses = classesData.map(classData => ({
          ...classData,
          lessons: teacherData.lessons.filter(l => l.classId === classData.id)
        }));

        setClasses(enrichedClasses);
      } catch (err) {
        console.error('Erro ao carregar turmas:', err);
        setError(err.message || 'Erro ao carregar turmas');
      } finally {
        setLoading(false);
      }
    };

    fetchTeacherClasses();
  }, []);

  const formatDayOfWeek = (day) => {
    const days = {
      'SEGUNDA_FEIRA': 'Segunda',
      'TERCA_FEIRA': 'Terça',
      'QUARTA_FEIRA': 'Quarta',
      'QUINTA_FEIRA': 'Quinta',
      'SEXTA_FEIRA': 'Sexta'
    };
    return days[day] || day;
  };

  const formatTimeRange = (start, end) => {
    return `${start.substring(0, 5)} - ${end.substring(0, 5)}`;
  };

  if (loading) {
    return (
      <div className="class-list">
        <h2>Minhas Turmas</h2>
        <div className="loading">Carregando turmas...</div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="class-list">
        <h2>Minhas Turmas</h2>
        <div className="error">{error}</div>
      </div>
    );
  }

  if (classes.length === 0) {
    return (
      <div className="class-list">
        <h2>Minhas Turmas</h2>
        <div className="empty-state">Nenhuma turma encontrada</div>
      </div>
    );
  }

  return (
    <div className="class-list">
      <h2>Minhas Turmas</h2>
      
      <div className="classes-grid">
        {classes.map((classItem) => (
          <div 
            key={classItem.id}
            className={`class-card ${selectedClass?.id === classItem.id ? 'selected' : ''}`}
            onClick={() => onSelectClass(classItem)}
          >
            <h3>{classItem.name}</h3>
            <div className="class-meta">
              <span className="lesson-count">
                <i className="fas fa-calendar-alt"></i> {classItem.lessons.length} aulas
              </span>
            </div>
            
            <div className="class-lessons">
              <h4>Horários:</h4>
              <ul>
                {classItem.lessons.map((lesson, index) => (
                  <li key={`${lesson.id}-${index}`}>
                    <div className="lesson-info">
                      <strong>{lesson.subject.name}</strong>
                      <div className="lesson-schedule">
                        <span>{formatDayOfWeek(lesson.dayOfWeek)}</span>
                        <span>{formatTimeRange(lesson.startTime, lesson.endTime)}</span>
                      </div>
                    </div>
                  </li>
                ))}
              </ul>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};

export default ClassList;