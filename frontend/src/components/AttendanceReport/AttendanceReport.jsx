import { useState, useEffect } from 'react';
import './AttendanceReport.css';

const AttendanceReport = () => {
  const [attendanceData, setAttendanceData] = useState([]);
  const [overallAttendance, setOverallAttendance] = useState(0);
  const [error, setError] = useState(null);

  useEffect(() => {
    try {
      const userData = JSON.parse(sessionStorage.getItem('userData'));
      
      if (!userData) {
        throw new Error('Dados do aluno não encontrados');
      }

      const attendanceBySubject = userData.attendances?.reduce((acc, attendance) => {
        const subjectName = attendance.subjectName || 'Disciplina não informada';
        
        if (!acc[subjectName]) {
          acc[subjectName] = { attended: 0, total: 0 };
        }
        
        acc[subjectName].total++;
        if (attendance.present) acc[subjectName].attended++;
        
        return acc;
      }, {}) || {};

      const processedData = Object.entries(attendanceBySubject).map(([subject, {attended, total}]) => ({
        subject,
        attended,
        totalClasses: total,
        percentage: total > 0 ? Math.round((attended / total) * 100) : 0
      }));

      setAttendanceData(processedData);

      if (processedData.length > 0) {
        const totalAttended = processedData.reduce((sum, item) => sum + item.attended, 0);
        const totalClasses = processedData.reduce((sum, item) => sum + item.totalClasses, 0);
        const overall = totalClasses > 0 ? Math.round((totalAttended / totalClasses) * 100) : 0;
        setOverallAttendance(overall);
      }
    } catch (err) {
      console.error('Erro ao processar frequência:', err);
      setError('Erro ao carregar dados de frequência');
    }
  }, []);

  const getStatusClass = (percentage) => {
    return percentage >= 85 ? 'good' : 
           percentage >= 75 ? 'medium' : 'bad';
  };

  if (error) {
    return <div className="error">{error}</div>;
  }

  if (attendanceData.length === 0) {
    return <div className="empty-state">Nenhum registro de frequência disponível</div>;
  }

  return (
    <div className="attendance-report">
      <div className="summary-card">
        <h3>Frequência Geral</h3>
        <div className="attendance-percentage">{overallAttendance}%</div>
        <div className={`attendance-status ${getStatusClass(overallAttendance)}`}>
          {overallAttendance >= 85 ? 'Frequência regular' : 'Frequência abaixo do esperado'}
        </div>
      </div>

      <table className="attendance-table">
        <thead>
          <tr>
            <th>Disciplina</th>
            <th>Aulas</th>
            <th>Presenças</th>
            <th>Frequência</th>
          </tr>
        </thead>
        <tbody>
          {attendanceData.map((item, index) => (
            <tr key={index}>
              <td>{item.subject}</td>
              <td>{item.totalClasses}</td>
              <td>{item.attended}</td>
              <td>
                <div className="progress-container">
                  <div 
                    className={`progress-bar ${getStatusClass(item.percentage)}`}
                    style={{ width: `${item.percentage}%` }}
                  ></div>
                  <span className="percentage-text">{item.percentage}%</span>
                </div>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};

export default AttendanceReport;