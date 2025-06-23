import { useState, useEffect } from 'react';
import './OverviewPainel.css';
import api from '../../Service/Api';

const OverviewPainel = () => {
  const [stats, setStats] = useState({
    totalStudents: 0,
    totalTeachers: 0,
    totalClasses: 0,
    hasData: false
  });
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchData = async () => {
      setLoading(true);
      setError(null);
      
      try {
        const [classesRes, studentsRes, teachersRes] = await Promise.all([
          api.get('/classes?size=1'), 
          api.get('/students?size=1'),
          api.get('/teachers?size=1')
        ]);

        const hasRealClasses = classesRes.data?.content?.length > 0;
        const hasRealStudents = studentsRes.data?.content?.length > 0;
        const hasRealTeachers = teachersRes.data?.content?.length > 0;

        let totalClasses = 0;
        let totalStudents = 0;
        let totalTeachers = 0;

        if (hasRealClasses) {
          const countRes = await api.get('/classes/count');
          totalClasses = countRes.data.count || 0;
        }

        if (hasRealStudents) {
          const countRes = await api.get('/students/count');
          totalStudents = countRes.data.count || 0;
        }

        if (hasRealTeachers) {
          const countRes = await api.get('/teachers/count');
          totalTeachers = countRes.data.count || 0;
        }

        setStats({
          totalStudents,
          totalTeachers,
          totalClasses,
          hasData: totalStudents > 0 || totalTeachers > 0 || totalClasses > 0
        });

      } catch (error) {
        console.error("Erro ao carregar dados:", error);
        setError("Erro ao carregar dados do painel");
        setStats({
          totalStudents: 0,
          totalTeachers: 0,
          totalClasses: 0,
          hasData: false
        });
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, []);

  if (loading) return <div className="loading">Carregando dados...</div>;
  if (error) return <div className="error">{error}</div>;

  return (
    <div className="overview-panel">
      <h2>Visão Geral do Sistema</h2>
      
      {stats.hasData ? (
        <div className="stats-grid">
          <StatCard title="Alunos" value={stats.totalStudents} />
          <StatCard title="Professores" value={stats.totalTeachers} />
          <StatCard title="Turmas" value={stats.totalClasses} />
        </div>
      ) : (
        <div className="empty-state">
          <p>Nenhum dado cadastrado ainda</p>
        </div>
      )}
    </div>
  );
};

const StatCard = ({ title, value }) => (
  <div className="stat-card">
    <h3>{title}</h3>
    <div className="stat-value">{value}</div>
  </div>
);

export default OverviewPainel;