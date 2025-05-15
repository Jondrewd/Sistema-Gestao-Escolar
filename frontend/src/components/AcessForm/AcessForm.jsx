import { useState } from 'react';
import api from '../../Service/Api';
import './AcessForm.css';

const AcessForm = ({ onSuccess }) => {
  const [formData, setFormData] = useState({
    fullName: '', 
    cpf: '',
    email: '',
    password: '',
    confirmPassword: '',
    registrationNumber: '',
    specialty: ''          
  });
  const [role, setRole] = useState('student');
  const [error, setError] = useState('');
  const [isSubmitting, setIsSubmitting] = useState(false);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
  };

  const handleRoleChange = (e) => {
    setRole(e.target.value);
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
    setFormData(prev => ({ ...prev, cpf: formattedValue }));
  };

  const validateForm = () => {
    if (!formData.fullName.trim()) {
      setError('Nome completo é obrigatório');
      return false;
    }

    if (formData.cpf.replace(/\D/g, '').length !== 11) {
      setError('CPF inválido');
      return false;
    }

    if (formData.password.length < 6) {
      setError('A senha deve ter pelo menos 6 caracteres');
      return false;
    }

    if (formData.password !== formData.confirmPassword) {
      setError('As senhas não coincidem');
      return false;
    }

    if (!/^\S+@\S+\.\S+$/.test(formData.email)) {
      setError('Email inválido');
      return false;
    }

    return true;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setIsSubmitting(true);
    setError('');

    if (!validateForm()) {
      setIsSubmitting(false);
      return;
    }

    try {
      const { confirmPassword, ...dataToSend } = formData;
      dataToSend.role = role;
      
      const endpoint = role === 'student' 
        ? '/auth/register/student' 
        : '/auth/register/teacher';

      await api.post(endpoint, dataToSend);
      onSuccess('Solicitação enviada com sucesso! Aguarde a aprovação.');
    } catch (err) {
      setError(err.response?.data?.message || 'Erro ao enviar os dados. Verifique as informações e tente novamente.');
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <form onSubmit={handleSubmit} className="access-form">
      <div className="form-row">
        <div className="form-group">
          <label htmlFor="fullName">Nome Completo *</label>
          <input
            id="fullName"
            name="fullName"
            type="text"
            value={formData.fullName}
            onChange={handleChange}
            placeholder="Digite seu nome completo"
            required
          />
        </div>

        <div className="form-group">
          <label htmlFor="cpf">CPF *</label>
          <input
            id="cpf"
            name="cpf"
            type="text"
            value={formData.cpf}
            onChange={handleCPFChange}
            placeholder="000.000.000-00"
            maxLength="14"
            required
          />
        </div>
      </div>

      <div className="form-group">
        <label htmlFor="email">Email *</label>
        <input
          id="email"
          name="email"
          type="email"
          value={formData.email}
          onChange={handleChange}
          placeholder="seu@email.com"
          required
        />
      </div>

      <div className="form-row">
        <div className="form-group">
          <label htmlFor="password">Senha *</label>
          <input
            id="password"
            name="password"
            type="password"
            value={formData.password}
            onChange={handleChange}
            placeholder="Mínimo 6 caracteres"
            minLength="6"
            required
          />
        </div>

        <div className="form-group">
          <label htmlFor="confirmPassword">Confirmar Senha *</label>
          <input
            id="confirmPassword"
            name="confirmPassword"
            type="password"
            value={formData.confirmPassword}
            onChange={handleChange}
            placeholder="Digite novamente sua senha"
            minLength="6"
            required
          />
        </div>
      </div>

      <div className="form-group">
        <label htmlFor="role">Tipo de Usuário *</label>
        <select
          id="role"
          name="role"
          value={role}
          onChange={handleRoleChange}
          required
        >
          <option value="student">Aluno</option>
          <option value="teacher">Professor</option>
        </select>
      </div>

      {role === 'student' && (
        <div className="form-group">
          <label htmlFor="registrationNumber">Número de Matrícula *</label>
          <input
            id="registrationNumber"
            name="registrationNumber"
            type="text"
            value={formData.registrationNumber}
            onChange={handleChange}
            placeholder="Digite seu número de matrícula"
            required
          />
        </div>
      )}

      {role === 'teacher' && (
        <div className="form-group">
          <label htmlFor="specialty">Formação/Especialidade *</label>
          <input
            id="specialty"
            name="specialty"
            type="text"
            value={formData.specialty}
            onChange={handleChange}
            placeholder="Ex: Matemática, História, Pedagogia..."
            required
          />
        </div>
      )}

      {error && <div className="error-message">{error}</div>}

      <button type="submit" className="submit-button" disabled={isSubmitting}>
        {isSubmitting ? (
          <span className="spinner"></span>
        ) : (
          'Solicitar Acesso'
        )}
      </button>
    </form>
  );
};

export default AcessForm;