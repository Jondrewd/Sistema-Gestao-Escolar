import { useState, useEffect } from 'react';
import './ClassSchedule.css';
import api from '../../Service/Api';

const ClassSchedule = ({ detailed = false }) => {
  const [schedule, setSchedule] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [classId, setClassId] = useState(null);

  useEffect(() => {
    const fetchStudentAndSchedule = async () => {
      try {
        setLoading(true);
        const cpf = sessionStorage.getItem('cpf');
        
        if (!cpf) {
          throw new Error('CPF não encontrado na sessão');
        }

        const studentRaw = sessionStorage.getItem('userData');
        const student = JSON.parse(studentRaw);
        const studentClassId = student.classeId;
        
        if (!studentClassId) {
          throw new Error('Aluno não está matriculado em nenhuma turma');
        }

        setClassId(studentClassId);

        const scheduleResponse = await api.get(`classes/${studentClassId}/schedule`);
        const formattedSchedule = formatScheduleData(scheduleResponse.data);
        setSchedule(formattedSchedule);
      } catch (err) {
        console.error('Erro ao carregar horário:', err);
        setError(err.message || 'Erro ao carregar horário. Tente novamente.');
      } finally {
        setLoading(false);
      }
    };

    fetchStudentAndSchedule();
  }, []);

  const formatScheduleData = (apiData) => {
    if (!apiData || apiData.length === 0) return [];
    
    const scheduleByTime = apiData.reduce((acc, item) => {
      const timeSlot = `${item.startTime} - ${item.endTime}`;
      
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
      
      switch(item.dayOfWeek) {
        case 'SEGUNDA_FEIRA':
          acc[timeSlot].mon = item.subjectName;
          break;
        case 'TERCA_FEIRA':
          acc[timeSlot].tue = item.subjectName;
          break;
        case 'QUARTA_FEIRA':
          acc[timeSlot].wed = item.subjectName;
          break;
        case 'QUINTA_FEIRA':
          acc[timeSlot].thu = item.subjectName;
          break;
        case 'SEXTA_FEIRA':
          acc[timeSlot].fri = item.subjectName;
          break;
        default:
          console.warn(`Dia da semana não reconhecido: ${item.dayOfWeek}`);
      }
      
      return acc;
    }, {});
    
    return Object.values(scheduleByTime).sort((a, b) => {
      const timeA = a.time.split(' - ')[0];
      const timeB = b.time.split(' - ')[0];
      return timeA.localeCompare(timeB);
    });
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
    return <div className="empty-state">Nenhuma aula agendada para esta turma</div>;
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
                <td>{item.mon && <div className="subject">{item.mon}</div>}</td>
                <td>{item.tue && <div className="subject">{item.tue}</div>}</td>
                <td>{item.wed && <div className="subject">{item.wed}</div>}</td>
                <td>{item.thu && <div className="subject">{item.thu}</div>}</td>
                <td>{item.fri && <div className="subject">{item.fri}</div>}</td>
              </tr>
            ))}
          </tbody>
        </table>
      ) : (
        <div className="compact-schedule">
          <h3>Horário da Turma {classId}</h3>
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