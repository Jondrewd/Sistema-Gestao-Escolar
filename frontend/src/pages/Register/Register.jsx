import { useState } from 'react';
import { Link } from 'react-router-dom';
import './Register.css';
import AccessForm from '../../components/AcessForm/AcessForm';

const Register = () => {
  const [successMessage, setSuccessMessage] = useState('');

  const handleSubmitSuccess = (message) => {
    setSuccessMessage(message);
  };

  return (
    <div className="register-container">
      <div className="register-card">
        <div className="register-header">
          <img 
            src="https://cdn-icons-png.flaticon.com/512/668/668709.png" 
            alt="School Logo" 
            className="logo"
          />
          <h1>Solicitar Acesso</h1>
          <p>Preencha seus dados para solicitar acesso ao sistema</p>
        </div>

        {successMessage ? (
          <div className="success-message">
            <div className="success-icon">✓</div>
            <p>{successMessage}</p>
            <Link to="/login" className="back-to-login">Voltar para o login</Link>
          </div>
        ) : (
          <AccessForm onSuccess={handleSubmitSuccess} />
        )}
      </div>
    </div>
  );
};

export default Register;