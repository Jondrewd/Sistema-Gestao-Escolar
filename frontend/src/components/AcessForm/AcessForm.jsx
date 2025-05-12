import { useState } from 'react';
import { Link } from 'react-router-dom';
import './AcessForm.css'

const AccessForm = ({ onSuccess }) => {
  const [formData, setFormData] = useState({
    name: '',
    email: '',
    cpf: '',
    role: 'student',
    studentId: '',
    specialty: ''
  });
  
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState('');

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
  };

  const formatCPF = (value) => {
    return value
      .replace(/\D/g, '')
      .replace(/(\d{3})(\d)/, '$1.$2')
      .replace(/(\d{3})(\d)/, '$1.$2')
      .replace(/(\d{3})(\d{1,2})/, '$1-$2')
      .replace(/(-\d{2})\d+?$/, '$1');
  };

  const handleCPFChange = (e) => {
    const formattedValue = formatCPF(e.target.value);
    setFormData(prev => ({
      ...prev,
      cpf: formattedValue
    }));
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    setIsLoading(true);
    setError('');

    if (!formData.name || !formData.email || !formData.cpf) {
      setError('Preencha todos os campos obrigatórios');
      setIsLoading(false);
      return;
    }

    if (formData.cpf.replace(/\D/g, '').length !== 11) {
      setError('CPF inválido');
      setIsLoading(false);
      return;
    }

    setTimeout(() => {
      console.log('Dados enviados:', formData);
      setIsLoading(false);
      onSuccess('Solicitação enviada! Analisaremos seus dados e entraremos em contato em até 48h.');
    }, 1500);
  };

  return (
    <form onSubmit={handleSubmit} className="access-form">
      {error && <div className="error-message">{error}</div>}

      <div className="form-row">
        <div className="form-group">
          <label htmlFor="name">Nome Completo *</label>
          <input
            type="text"
            id="name"
            name="name"
            value={formData.name}
            onChange={handleChange}
            placeholder="Digite seu nome completo"
            required
          />
        </div>
      </div>

      <div className="form-row">
        <div className="form-group">
          <label htmlFor="email">E-mail *</label>
          <input
            type="email"
            id="email"
            name="email"
            value={formData.email}
            onChange={handleChange}
            placeholder="seu@email.com"
            required
          />
        </div>

        <div className="form-group">
          <label htmlFor="cpf">CPF *</label>
          <input
            type="text"
            id="cpf"
            name="cpf"
            value={formData.cpf}
            onChange={handleCPFChange}
            placeholder="000.000.000-00"
            maxLength="14"
            required
          />
        </div>
      </div>

      <div className="form-group">
        <label htmlFor="role">Você é: *</label>
        <select
          id="role"
          name="role"
          value={formData.role}
          onChange={handleChange}
          required
        >
          <option value="student">Aluno</option>
          <option value="teacher">Professor</option>
        </select>
      </div>

      {formData.role === 'student' && (
        <div className="form-group">
          <label htmlFor="studentId">Número de Matrícula *</label>
          <input
            type="text"
            id="studentId"
            name="studentId"
            value={formData.studentId}
            onChange={handleChange}
            placeholder="Digite seu número de matrícula"
            required
          />
        </div>
      )}

      {formData.role === 'teacher' && (
        <div className="form-group">
          <label htmlFor="specialty">Formação/Especialidade *</label>
          <input
            type="text"
            id="specialty"
            name="specialty"
            value={formData.specialty}
            onChange={handleChange}
            placeholder="Ex: Matemática, História, Pedagogia..."
            required
          />
        </div>
      )}

      <div className="form-footer">
        <button type="submit" className="submit-button" disabled={isLoading}>
          {isLoading ? (
            <span className="button-loader"></span>
          ) : (
            'Enviar Solicitação'
          )}
        </button>
        <p className="login-link">
          Já possui acesso? <Link to="/login">Faça login aqui</Link>
        </p>
      </div>
    </form>
  );
};

export default AccessForm;