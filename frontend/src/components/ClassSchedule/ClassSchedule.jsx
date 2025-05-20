import { useState, useEffect } from 'react';
import './ClassSchedule.css';

const ClassSchedule = ({ detailed = false }) => {
  const [schedule, setSchedule] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    try {
      setLoading(true);
      const userData = JSON.parse(sessionStorage.getItem('userData'));
      
      if (!userData || !userData.classes) {
        throw new Error('Dados de turmas não encontrados');
      }

      const formattedSchedule = formatClassesData(userData.classes);
      setSchedule(formattedSchedule);
    } catch (err) {
      console.error('Erro ao carregar horário:', err);
      setError('Erro ao carregar horário. Tente novamente.');
    } finally {
      setLoading(false);
    }
  }, []);

  const formatClassesData = (classes) => {
    if (!classes || classes.length === 0) return [];
    
    const scheduleByTime = classes.reduce((acc, classItem) => {
      const timeSlot = `${classItem.startTime} - ${classItem.endTime}`;
      
      if (!acc[timeSlot]) {
        acc[timeSlot] = {
          time: timeSlot,
          mon: '',
          tue: '',
          wed: '',
          thu: '',
          fri: ''
        };
      }
      
      classItem.weekDays.forEach(weekDay => {
        switch(weekDay.toLowerCase()) {
          case 'segunda':
            acc[timeSlot].mon = classItem.subject;
            break;
          case 'terça':
            acc[timeSlot].tue = classItem.subject;
            break;
          case 'quarta':
            acc[timeSlot].wed = classItem.subject;
            break;
          case 'quinta':
            acc[timeSlot].thu = classItem.subject;
            break;
          case 'sexta':
            acc[timeSlot].fri = classItem.subject;
            break;
        }
      });
      
      return acc;
    }, {});
    
    return Object.values(scheduleByTime);
  };

  const getTodaysClass = (scheduleItem) => {
    const day = new Date().getDay(); 
    switch(day) {
      case 1: return scheduleItem.mon;
      case 2: return scheduleItem.tue;
      case 3: return scheduleItem.wed;
      case 4: return scheduleItem.thu;
      case 5: return scheduleItem.fri;
      default: return 'Sem aula';
    }
  };

  if (loading) {
    return <div className="loading">Carregando horário...</div>;
  }

  if (error) {
    return <div className="error">{error}</div>;
  }

  if (schedule.length === 0) {
    return <div className="empty-state">Nenhuma turma matriculada</div>;
  }

  return (
    <div className={`class-schedule ${detailed ? 'detailed' : ''}`}>
      {detailed ? (
        <table>
          <thead>
            <tr>
              <th>Horário</th>
              <th>Segunda</th>
              <th>Terça</th>
              <th>Quarta</th>
              <th>Quinta</th>
              <th>Sexta</th>
            </tr>
          </thead>
          <tbody>
            {schedule.map((item, index) => (
              <tr key={index}>
                <td>{item.time}</td>
                <td>
                  {item.mon && <div className="subject">{item.mon}</div>}
                </td>
                <td>
                  {item.tue && <div className="subject">{item.tue}</div>}
                </td>
                <td>
                  {item.wed && <div className="subject">{item.wed}</div>}
                </td>
                <td>
                  {item.thu && <div className="subject">{item.thu}</div>}
                </td>
                <td>
                  {item.fri && <div className="subject">{item.fri}</div>}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      ) : (
        <div className="compact-schedule">
          {schedule.slice(0, 3).map((item, index) => {
            const todaysClass = getTodaysClass(item);
            return (
              <div key={index} className="schedule-item">
                <div className="time">{item.time}</div>
                <div className="today-class">
                  {todaysClass || 'Sem aula'}
                </div>
              </div>
            );
          })}
          <div className="view-all">
            <button>Ver horário completo</button>
          </div>
        </div>
      )}
    </div>
  );
};

export default ClassSchedule;