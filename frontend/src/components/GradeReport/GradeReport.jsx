import { useState, useEffect } from 'react';
import './GradeReport.css';

const GradeReport = () => {
  const [grades, setGrades] = useState([]);
  const [average, setAverage] = useState(0);
  const [error, setError] = useState(null);

  useEffect(() => {
    try {
      const userData = JSON.parse(sessionStorage.getItem('userData'));

      if (!userData) {
        throw new Error('Dados do aluno não encontrados');
      }

      const processedGrades = userData.grades?.map(gradeItem => ({
        subject: gradeItem.evaluation?.subjectName || 'Disciplina não informada',
        grade: gradeItem.score || 0,
        date: gradeItem.evaluation?.date
          ? new Date(gradeItem.evaluation.date).toLocaleDateString('pt-BR')
          : 'Data não informada',
        teacher: 'Professor não informado' 
      })) || [];

      setGrades(processedGrades);

      if (processedGrades.length > 0) {
        const validGrades = processedGrades.filter(g => !isNaN(g.grade));
        const sum = validGrades.reduce((total, g) => total + g.grade, 0);
        const avg = validGrades.length > 0 ? sum / validGrades.length : 0;
        setAverage(avg);
      }
    } catch (err) {
      console.error('Erro ao processar avaliações:', err);
      setError('Erro ao carregar boletim');
    }
  }, []);


  const getStatus = (grade) => {
    const numericGrade = parseFloat(grade);
    if (isNaN(numericGrade)) return 'Indefinido';
    
    return numericGrade >= 7 ? 'Aprovado' : 
           numericGrade >= 5 ? 'Recuperação' : 'Reprovado';
  };

  if (error) {
    return <div className="error">{error}</div>;
  }

  if (grades.length === 0) {
    return <div className="empty-state">Nenhuma avaliação disponível</div>;
  }

  return (
    <div className="grade-report">
      <div className="summary-card">
        <h3>Média Geral</h3>
        <div className="average-grade">{average.toFixed(1)}</div>
        <div className={`performance ${getStatus(average).toLowerCase()}`}>
          {getStatus(average)}
        </div>
      </div>

      <table className="grades-table">
        <thead>
          <tr>
            <th>Disciplina</th>
            <th>Nota</th>
            <th>Professor</th>
            <th>Situação</th>
            <th>Data</th>
          </tr>
        </thead>
        <tbody>
          {grades.map((item, index) => {
            const numericGrade = parseFloat(item.grade);
            const status = getStatus(item.grade);
            
            return (
              <tr key={index}>
                <td>{item.subject}</td>
                <td className={`grade ${status.toLowerCase()}`}>
                  {numericGrade.toFixed(1)}
                </td>
                <td>{item.teacher}</td>
                <td>
                  <span className={`status ${status.toLowerCase()}`}>
                    {status}
                  </span>
                </td>
                <td>{item.date}</td>
              </tr>
            );
          })}
        </tbody>
      </table>
    </div>
  );
};

export default GradeReport;