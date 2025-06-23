import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import './UserManager.css';
import api from '../../Service/Api';
import Modal from 'react-modal';

Modal.setAppElement('#root');

const UserManager = () => {
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [filter, setFilter] = useState('all');
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [currentUser, setCurrentUser] = useState(null);
  const [formData, setFormData] = useState({
    fullName: '',
    cpf: '',
    email: '',
    userType: 'STUDENT',
    password: ''
  });

  useEffect(() => {
    fetchUsers();
  }, []);

  const fetchUsers = async () => {
    setLoading(true);
    setError(null);
    try {
      const response = await api.get('/users');
      setUsers(Array.isArray(response.data) ? response.data : []);
    } catch (error) {
      console.error("Erro ao carregar usuários:", error);
      setError("Erro ao carregar usuários. Tente recarregar a página.");
    } finally {
      setLoading(false);
    }
  };

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
  };

  const handleOpenModal = (user = null) => {
    setCurrentUser(user);
    setFormData(user ? { 
      ...user, 
      password: '' // Não mostrar senha existente
    } : {
      fullName: '',
      cpf: '',
      email: '',
      userType: 'STUDENT',
      password: ''
    });
    setIsModalOpen(true);
  };

  const handleCloseModal = () => {
    setIsModalOpen(false);
    setCurrentUser(null);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      if (currentUser) {
        // Atualizar usuário existente
        await api.put(`/users/${currentUser.id}`, formData);
      } else {
        // Criar novo usuário
        await api.post('/users', formData);
      }
      fetchUsers();
      handleCloseModal();
    } catch (error) {
      console.error("Erro ao salvar usuário:", error);
      alert("Erro ao salvar usuário. Verifique os dados e tente novamente.");
    }
  };

  const handleDelete = async (userId) => {
    if (window.confirm('Tem certeza que deseja excluir este usuário?')) {
      try {
        await api.delete(`/users/${userId}`);
        setUsers(prev => prev.filter(u => u.id !== userId));
      } catch (error) {
        console.error("Erro ao excluir usuário:", error);
        alert("Não foi possível excluir o usuário. Tente novamente.");
      }
    }
  };

  const filteredUsers = users.filter(user => {
    if (filter === 'all') return true;
    return user.userType?.toLowerCase() === filter.toLowerCase();
  });

  const translateUserType = (type) => {
    switch(type?.toUpperCase()) {
      case 'STUDENT': return 'Aluno';
      case 'TEACHER': return 'Professor';
      case 'ADMIN': return 'Administrador';
      default: return type || 'Desconhecido';
    }
  };

  if (loading) return <div className="loading">Carregando usuários...</div>;
  if (error) return <div className="error">{error}</div>;

  return (
    <div className="user-manager">
      <div className="manager-header">
        <h2>Gestão de Usuários</h2>
        <div className="controls">
          <div className="filter-controls">
            <button 
              className={`filter-btn ${filter === 'all' ? 'active' : ''}`}
              onClick={() => setFilter('all')}
            >
              Todos
            </button>
            <button 
              className={`filter-btn ${filter === 'student' ? 'active' : ''}`}
              onClick={() => setFilter('student')}
            >
              Alunos
            </button>
            <button 
              className={`filter-btn ${filter === 'teacher' ? 'active' : ''}`}
              onClick={() => setFilter('teacher')}
            >
              Professores
            </button>
            <button 
              className={`filter-btn ${filter === 'admin' ? 'active' : ''}`}
              onClick={() => setFilter('admin')}
            >
              Administradores
            </button>
          </div>
          <button onClick={() => handleOpenModal()} className="add-button">
            + Novo Usuário
          </button>
        </div>
      </div>

      <table className="user-table">
        <thead>
          <tr>
            <th>Nome</th>
            <th>CPF</th>
            <th>E-mail</th>
            <th>Tipo</th>
            <th>Status</th>
            <th>Ações</th>
          </tr>
        </thead>
        <tbody>
            {filteredUsers.map(user => (
              <tr key={user.id}>
                <td>{user.fullName}</td>
                <td>{user.cpf}</td>
                <td>{user.email}</td>
                <td>{translateUserType(user.userType)}</td>
                <td className="actions">
                  <button
                    onClick={() => handleOpenModal(user)}
                    className="edit-button"
                  >
                    Editar
                  </button>
                  <button
                    onClick={() => handleDelete(user.id)}
                    className="delete-button"
                  >
                    Excluir
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
      </table>

      <Modal
        isOpen={isModalOpen}
        onRequestClose={handleCloseModal}
        className="modal"
        overlayClassName="modal-overlay"
      >
        <h3>{currentUser ? 'Editar Usuário' : 'Novo Usuário'}</h3>
        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label>Nome Completo:</label>
            <input
              type="text"
              name="fullName"
              value={formData.fullName}
              onChange={handleInputChange}
              required
            />
          </div>

          <div className="form-group">
            <label>CPF:</label>
            <input
              type="text"
              name="cpf"
              value={formData.cpf}
              onChange={handleInputChange}
              required
              disabled={!!currentUser}
            />
          </div>

          <div className="form-group">
            <label>E-mail:</label>
            <input
              type="email"
              name="email"
              value={formData.email}
              onChange={handleInputChange}
              required
            />
          </div>

          <div className="form-group">
            <label>Tipo de Usuário:</label>
            <select
              name="userType"
              value={formData.userType}
              onChange={handleInputChange}
              required
            >
              <option value="STUDENT">Aluno</option>
              <option value="TEACHER">Professor</option>
              <option value="ADMIN">Administrador</option>
            </select>
          </div>

          <div className="form-group">
            <label>Senha:</label>
            <input
              type="password"
              name="password"
              value={formData.password}
              onChange={handleInputChange}
              required={!currentUser}
              placeholder={currentUser ? "Deixe em branco para manter a senha atual" : ""}
            />
          </div>

          <div className="modal-actions">
            <button type="button" onClick={handleCloseModal}>
              Cancelar
            </button>
            <button type="submit" className="confirm-button">
              Salvar
            </button>
          </div>
        </form>
      </Modal>
    </div>
  );
};

export default UserManager;




