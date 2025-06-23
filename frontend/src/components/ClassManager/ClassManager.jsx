import { useState, useEffect } from 'react';
import './ClassManager.css';
import api from '../../Service/Api';
import Modal from 'react-modal';

// Configuração do modal (importante para acessibilidade)
Modal.setAppElement('#root');

const ClassManager = () => {
  const [classes, setClasses] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [isAddStudentsModalOpen, setIsAddStudentsModalOpen] = useState(false);
  const [selectedClass, setSelectedClass] = useState(null);
  const [formData, setFormData] = useState({
    name: '',
    studentCpfs: ''
  });
  const [studentsForm, setStudentsForm] = useState({
    cpf: ''
  });

  // Busca as turmas
  const fetchClasses = async () => {
    setLoading(true);
    setError(null);
    try {
      const response = await api.get('/classes');
      const classesData = response.data?.content || response.data || [];
      setClasses(classesData);
    } catch (error) {
      console.error("Erro ao carregar turmas:", error);
      setError("Erro ao carregar turmas. Tente recarregar a página.");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchClasses();
  }, []);

  // Cria uma nova turma
  const handleCreateClass = async () => {
    try {
      const payload = {
        name: formData.name,
        ...(formData.studentCpfs && {
          studentCpfs: formData.studentCpfs.split(',').map(cpf => cpf.trim())
        })
      };

      const response = await api.post('/classes', payload);
      setClasses([...classes, response.data]);
      setIsModalOpen(false);
      setFormData({ name: '', studentCpfs: '' });
    } catch (error) {
      console.error("Erro ao criar turma:", error);
      alert("Erro ao criar turma. Verifique os dados e tente novamente.");
    }
  };

  // Adiciona alunos a uma turma existente
  const handleAddStudents = async () => {
    if (!selectedClass) return;

    try {
      await api.post(`/classes/${selectedClass.id}/students`, {
        cpf: studentsForm.cpf
      });

      // Atualiza a lista de turmas
      await fetchClasses();
      setIsAddStudentsModalOpen(false);
      setStudentsForm({ cpf: '' });
      alert("Aluno adicionado com sucesso!");
    } catch (error) {
      console.error("Erro ao adicionar aluno:", error);
      alert("Erro ao adicionar aluno. Verifique o CPF e tente novamente.");
    }
  };

  // Exclui uma turma
  const handleDelete = async (classId) => {
    if (window.confirm('Tem certeza que deseja excluir esta turma?')) {
      try {
        await api.delete(`/classes/${classId}`);
        setClasses(classes.filter(c => c.id !== classId));
      } catch (error) {
        console.error("Erro ao excluir turma:", error);
        alert("Não foi possível excluir a turma. Tente novamente.");
      }
    }
  };

  if (loading) return <div className="loading">Carregando turmas...</div>;
  if (error) return <div className="error">{error}</div>;

  return (
    <div className="class-manager">
      <div className="manager-header">
        <h2>Gestão de Turmas</h2>
        <button onClick={() => setIsModalOpen(true)} className="add-button">
          + Nova Turma
        </button>
      </div>

      {classes.length === 0 ? (
        <div className="empty-state">
          <p>Nenhuma turma cadastrada</p>
        </div>
      ) : (
        <div className="class-grid">
          {classes.map(cls => (
            <div key={cls.id} className="class-card">
              <div className="class-info">
                <h3>{cls.name}</h3>
                <p>ID: {cls.id}</p>
                <p>Alunos: {cls.studentCpfs?.length || 0}</p>
              </div>
              <div className="class-actions">
                <button
                  onClick={() => {
                    setSelectedClass(cls);
                    setIsAddStudentsModalOpen(true);
                  }}
                  className="edit-button"
                >
                  Adicionar Aluno
                </button>
                <button
                  onClick={() => handleDelete(cls.id)}
                  className="delete-button"
                >
                  Excluir
                </button>
              </div>
            </div>
          ))}
        </div>
      )}

      <Modal
        isOpen={isModalOpen}
        onRequestClose={() => setIsModalOpen(false)}
        className="modal"
        overlayClassName="modal-overlay"
      >
        <h3>Criar Nova Turma</h3>
        <div className="form-group">
          <label>Nome da Turma:</label>
          <input
            type="text"
            value={formData.name}
            onChange={(e) => setFormData({...formData, name: e.target.value})}
          />
        </div>
        <div className="form-group">
          <label>CPFs dos Alunos (opcional, separados por vírgula):</label>
          <input
            type="text"
            value={formData.studentCpfs}
            onChange={(e) => setFormData({...formData, studentCpfs: e.target.value})}
            placeholder="Ex: 123.456.789-00, 987.654.321-00"
          />
        </div>
        <div className="modal-actions">
          <button onClick={() => setIsModalOpen(false)}>Cancelar</button>
          <button onClick={handleCreateClass} className="confirm-button">
            Criar Turma
          </button>
        </div>
      </Modal>

      <Modal
        isOpen={isAddStudentsModalOpen}
        onRequestClose={() => setIsAddStudentsModalOpen(false)}
        className="modal"
        overlayClassName="modal-overlay"
      >
        <h3>Adicionar Aluno à Turma {selectedClass?.name}</h3>
        <div className="form-group">
          <label>CPF do Aluno:</label>
          <input
            type="text"
            value={studentsForm.cpf}
            onChange={(e) => setStudentsForm({...studentsForm, cpf: e.target.value})}
            placeholder="Ex: 123.456.789-00"
          />
        </div>
        <div className="modal-actions">
          <button onClick={() => setIsAddStudentsModalOpen(false)}>Cancelar</button>
          <button onClick={handleAddStudents} className="confirm-button">
            Adicionar Aluno
          </button>
        </div>
      </Modal>
    </div>
  );
};

export default ClassManager;