import { useState, useEffect } from 'react';
import './Dashboard.css';
import UserProfile from '../../components/UserProfile/UserProfile';
import ClassList from '../../components/ClassList/ClassList';
import GradeForm from '../../components/GradeForm/GradeForm';
import AttendanceForm from '../../components/AttendanceForm/AttendanceForm';
import GradeReport from '../../components/GradeReport/GradeReport';
import AttendanceReport from '../../components/AttendanceReport/AttendanceReport';
import ClassSchedule from '../../components/ClassSchedule/ClassSchedule';
import Exams from '../../components/Exams/Exams';
import ExamScheduler from '../../components/ExamScheduler/ExamScheduler'
import Classes from '../../components/Classes/Classes';
import SchoolAnnouncements from '../../components/SchoolAnnouncements/SchoolAnnoucements';
import api from '../../Service/Api';

const Dashboard = () => {
  const [userType, setUserType] = useState(sessionStorage.getItem('userType') || 'student');
  const [activeTab, setActiveTab] = useState('overview');
  const [selectedClass, setSelectedClass] = useState(null);
  const [userData, setUserData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const loadUserData = async () => {
      try {
        setLoading(true);
        const cpf = sessionStorage.getItem('cpf');
        
        if (!cpf) {
          throw new Error('CPF não encontrado na sessão');
        }

        const endpoint = userType.toUpperCase() === 'TEACHER' 
          ? `/teachers/${cpf}`
          : `/students/${cpf}`;
        
        const response = await api.get(endpoint);
        const data = response.data;
        
        if (userType.toUpperCase() === 'TEACHER' && Array.isArray(data.subjects)) {
        data.classes = data.subjects.reduce((acc, subject) => {
          const existingClass = acc.find(c => c.id === subject.classId);
          if (!existingClass) {
            acc.push({
              id: subject.classId,
              name: `Turma ${subject.classId}`,
              subjects: [subject]
            });
          } else {
            existingClass.subjects.push(subject);
          }
          return acc;
        }, []);
      } else {
        data.classes = [];
      }

        
        sessionStorage.setItem('userData', JSON.stringify(data));
        setUserData(data);
      } catch (err) {
        console.error('Erro ao carregar dados:', err);
        setError('Erro ao carregar dados. Tente novamente.');
      } finally {
        setLoading(false);
      }
    };

    loadUserData();
  }, [userType]);

  const isTeacher = userType.toUpperCase() === 'TEACHER';

  if (loading) {
    return <div className="loading">Carregando...</div>;
  }

  if (error) {
    return <div className="error">{error}</div>;
  }

  if (!userData) {
    return <div className="error">Nenhum dado encontrado</div>;
  }

  return (
    <div className={`dashboard ${isTeacher ? 'teacher' : 'student'}-dashboard`}>
      <header className="dashboard-header">
        <div className="header-content">
          <h1>Painel do {isTeacher ? 'Professor' : 'Aluno'}</h1>
          <div className="header-actions">
            <UserProfile user={userData} />
          </div>
        </div>
      </header>

      <div className="dashboard-container">
        <aside className="sidebar">
          <nav className="sidebar-nav">
            {isTeacher ? (
              <>
                <button 
                  className={`nav-item ${activeTab === 'classes' ? 'active' : ''}`}
                  onClick={() => setActiveTab('classes')}
                >
                  Minhas Turmas
                </button>
                <button 
                  className={`nav-item ${activeTab === 'grades' ? 'active' : ''}`}
                  onClick={() => setActiveTab('grades')}
                  disabled={!selectedClass}
                >
                  Lançar Notas
                </button>
                <button 
                  className={`nav-item ${activeTab === 'attendance' ? 'active' : ''}`}
                  onClick={() => setActiveTab('attendance')}
                  disabled={!selectedClass}
                >
                  Registrar Presenças
                </button>
                <button 
                  className={`nav-item ${activeTab === 'exams' ? 'active' : ''}`}
                  onClick={() => setActiveTab('exams')}
                  disabled={!selectedClass}
                >
                  Agendar Provas
                </button>
              </>
            ) : (
              <>
                <button 
                  className={`nav-item ${activeTab === 'overview' ? 'active' : ''}`}
                  onClick={() => setActiveTab('overview')}
                >
                  Visão Geral
                </button>
                <button 
                  className={`nav-item ${activeTab === 'grades' ? 'active' : ''}`}
                  onClick={() => setActiveTab('grades')}
                >
                  Meu Boletim
                </button>
                <button 
                  className={`nav-item ${activeTab === 'attendance' ? 'active' : ''}`}
                  onClick={() => setActiveTab('attendance')}
                >
                  Minha Frequência
                </button>
                <button 
                  className={`nav-item ${activeTab === 'schedule' ? 'active' : ''}`}
                  onClick={() => setActiveTab('schedule')}
                >
                  Meu Horário
                </button>
              </>
            )}
          </nav>

          <div className="sidebar-footer">
            {isTeacher ? (
              <Classes />
            ) : (
              <Exams />
            )}
          </div>
        </aside>

        <main className="main-content">
          {isTeacher ? (
            <>
              {activeTab === 'classes' && (
                <ClassList 
                  classes={userData.classes}
                  onSelectClass={setSelectedClass} 
                  selectedClass={selectedClass}
                />
              )}
              
              {activeTab === 'grades' && selectedClass && (
                <div className="tab-content">
                  <h2>Lançar Notas - {selectedClass.name}</h2>
                  <GradeForm 
                    classId={selectedClass.id}
                  />
                </div>
              )}
              
              {activeTab === 'attendance' && selectedClass && (
                <div className="tab-content">
                  <h2>Registrar Presenças - {selectedClass.name}</h2>
                  <AttendanceForm 
                    classId={selectedClass.id}
                  />
                </div>
              )}

              {!selectedClass && activeTab !== 'classes' && (
                <div className="empty-state">
                  <p>Selecione uma turma para continuar</p>
                </div>
              )}  
              {activeTab === 'exams' && selectedClass && (
                <div className="tab-content">
                  <h2>Agendar Prova - {selectedClass.name}</h2>
                  <ExamScheduler classId={selectedClass.id} />
                </div>
              )}
            </>
          ) : (
            <>
              {activeTab === 'overview' && (
                <div className="student-overview">
                  <SchoolAnnouncements />
                  <ClassSchedule />
                </div>
              )}
              
              {activeTab === 'grades' && (
                <div className="tab-content">
                  <h2>Meu Boletim</h2>
                  <GradeReport />
                </div>
              )}
              
              {activeTab === 'attendance' && (
                <div className="tab-content">
                  <h2>Minha Frequência</h2>
                  <AttendanceReport />
                </div>
              )}
              
              {activeTab === 'schedule' && (
                <div className="tab-content">
                  <h2>Meu Horário</h2>
                  <ClassSchedule detailed />
                </div>
              )}
            </>
          )}
        </main>
      </div>
    </div>
  );
};

export default Dashboard;