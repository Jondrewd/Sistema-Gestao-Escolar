import './Exams.css';

const Exams = () => {
  const exams = [
    { id: 1, subject: 'Matemática', date: '2023-06-20', type: 'Prova Bimestral' },
    { id: 2, subject: 'Português', date: '2023-06-22', type: 'Redação' },
    { id: 3, subject: 'Ciências', date: '2023-06-25', type: 'Trabalho Prático' },
  ];

  return (
    <div className="upcoming-exams">
      <h3>Próximas Avaliações</h3>
      <ul>
        {exams.map(exam => (
          <li key={exam.id}>
            <div className="exam-date">
              {new Date(exam.date).toLocaleDateString('pt-BR', { day: 'numeric', month: 'short' })}
            </div>
            <div className="exam-info">
              <div className="exam-subject">{exam.subject}</div>
              <div className="exam-type">{exam.type}</div>
            </div>
          </li>
        ))}
      </ul>
    </div>
  );
};

export default Exams;