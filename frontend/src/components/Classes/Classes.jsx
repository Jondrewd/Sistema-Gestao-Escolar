import { useState, useEffect } from 'react';
import './Classes.css';

const Classes = () => {
  const [upcomingClasses, setUpcomingClasses] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  useEffect(() => {
    const loadClasses = () => {
      try {
        setLoading(true);
        const teacherData = JSON.parse(sessionStorage.getItem('userData'));
        
        if (!teacherData || !teacherData.lessons) {
          setUpcomingClasses([]);
          return;
        }

        const now = new Date();
        const classesList = teacherData.lessons.map(lesson => {
          const nextDate = getNextWeekday(now, lesson.dayOfWeek);
          return {
            id: lesson.id,
            classId: lesson.classId,
            subject: lesson.subject.name,
            date: nextDate,
            dayOfWeek: lesson.dayOfWeek,
            time: `${lesson.startTime} - ${lesson.endTime}`,
            fullDate: `${nextDate}T${lesson.startTime}`
          };
        })
        .filter(cls => cls.fullDate >= now.toISOString())
        .sort((a, b) => new Date(a.fullDate) - new Date(b.fullDate))
        .slice(0, 5);
        
        setUpcomingClasses(classesList);
      } catch (err) {
        setError('Erro ao carregar aulas');
      } finally {
        setLoading(false);
      }
    };

    loadClasses();
  }, []);

  const getNextWeekday = (fromDate, dayOfWeek) => {
    const days = {
      'SEGUNDA_FEIRA': 1,
      'TERCA_FEIRA': 2,
      'QUARTA_FEIRA': 3,
      'QUINTA_FEIRA': 4,
      'SEXTA_FEIRA': 5,
      'SABADO': 6,
      'DOMINGO': 0
    };
    
    const targetDay = days[dayOfWeek];
    const date = new Date(fromDate);
    date.setDate(date.getDate() + (targetDay + 7 - date.getDay()) % 7);
    return date.toISOString().split('T')[0];
  };

  if (loading) return <div className="loading">Carregando...</div>;
  if (error) return <div className="error">{error}</div>;

  return (
    <div className="upcoming-classes">
      <h3>Próximas Aulas</h3>
      {upcomingClasses.length === 0 ? (
        <div className="empty-state">Nenhuma aula agendada</div>
      ) : (
        <ul className="classes-list">
          {upcomingClasses.map(cls => (
            <li key={cls.id} className="class-item">
              <div className="class-header">
                <span className="class-date">
                  {new Date(cls.date).toLocaleDateString('pt-BR', { 
                    weekday: 'short', 
                    day: 'numeric', 
                    month: 'short' 
                  })}
                </span>
                <span className="class-time">{cls.time}</span>
              </div>
              <div className="class-details">
                <span className="class-subject">{cls.subject}</span>
                <span className="class-info">Turma {cls.classId}</span>
              </div>
            </li>
          ))}
        </ul>
      )}
    </div>
  );
};

export default Classes;