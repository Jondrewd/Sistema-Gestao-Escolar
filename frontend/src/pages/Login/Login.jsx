import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import './Login.css';
import api from '../../Service/Api';

const Login = () => {
  const [cpf, setCpf] = useState('');
  const [password, setPassword] = useState('');
  const [rememberMe, setRememberMe] = useState(false);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState('');
  const navigate = useNavigate();

  const formatCpf = (value) => {
    const numericValue = value.replace(/\D/g, '');
    
    let formattedValue = numericValue;
    if (numericValue.length > 3) {
      formattedValue = `${numericValue.substring(0, 3)}.${numericValue.substring(3)}`;
    }
    if (numericValue.length > 6) {
      formattedValue = `${formattedValue.substring(0, 7)}.${formattedValue.substring(7)}`;
    }
    if (numericValue.length > 9) {
      formattedValue = `${formattedValue.substring(0, 11)}-${formattedValue.substring(11)}`;
    }
    
    return formattedValue.substring(0, 14);
  };

  const handleCpfChange = (e) => {
    const formattedCpf = formatCpf(e.target.value);
    setCpf(formattedCpf);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setIsLoading(true);
    setError('');

    try {
      if (cpf.length !== 14) {
        throw new Error("CPF incompleto. Formato esperado: 000.000.000-00");
      }
      const response = await api.post('/auth/login', {
        cpf,
        password
      });


      const { accessToken, refreshToken, userType } = response.data;

      if (rememberMe) {
        localStorage.setItem('token', accessToken);
        localStorage.setItem('refreshToken', refreshToken);
      } else {
        sessionStorage.setItem('token', accessToken);
        sessionStorage.setItem('refreshToken', refreshToken);
      }
      
      sessionStorage.setItem('cpf', cpf);
      sessionStorage.setItem('userType', userType);

      console.log('Login bem-sucedido');

      if (userType === 'ADMIN') {
        navigate("/admin");
      } else {
        navigate("/dashboard");
      }

    } catch (err) {
      console.error(err);
      if (err.response && err.response.status === 401) {
        setError("CPF ou senha inválidos");
      } else {
        setError("Erro ao fazer login. Tente novamente.");
      }
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="login-container">
      <div className="login-card">
        <div className="login-header">
          <img 
            src="https://cdn-icons-png.flaticon.com/512/668/668709.png" 
            alt="School Logo" 
            className="logo"
          />
          <h1>Sistema de Gestão Escolar</h1>
          <p>Entre com suas credenciais para acessar o sistema</p>
        </div>

        {error && <div className="error-message">{error}</div>}

        <form onSubmit={handleSubmit} className="login-form">
          <div className="form-group">
            <label htmlFor="cpf">CPF</label>
            <input
              type="text"
              id="cpf"
              value={cpf}
              onChange={handleCpfChange}
              placeholder="000.000.000-00"
              required
              maxLength={14}
            />
          </div>

          <div className="form-group">
            <label htmlFor="password">Senha</label>
            <input
              type="password"
              id="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              placeholder="••••••••"
              required
            />
          </div>

          <div className="form-options">
            <div className="remember-me">
              <input
                type="checkbox"
                id="remember"
                checked={rememberMe}
                onChange={(e) => setRememberMe(e.target.checked)}
              />
              <label htmlFor="remember">Lembrar de mim</label>
            </div>
            <a href="#forgot-password" className="forgot-password">
              Esqueceu a senha?
            </a>
          </div>

          <button type="submit" className="login-button" disabled={isLoading}>
            {isLoading ? (
              <span className="spinner"></span>
            ) : (
              'Entrar'
            )}
          </button>
        </form>

        <div className="login-footer">
          <p>
            Não tem uma conta? <Link to="/register">Solicitar acesso</Link>
          </p>
        </div>
      </div>
    </div>
  );
};

export default Login;