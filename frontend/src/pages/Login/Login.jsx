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

 const handleSubmit = async (e) => {
  e.preventDefault();
  setIsLoading(true);
  setError('');

  try {
    const response = await api.post('/auth/login', {
      cpf,
      password
    });

    const { acessToken, refreshToken } = response.data;

    sessionStorage.setItem('cpf', cpf);
    sessionStorage.setItem('acessToken', acessToken);
    sessionStorage.setItem('refreshToken', refreshToken);

    console.log('Login bem-sucedido');

    navigate("/dashboard");

  } catch (err) {
    console.error(err);
    if (err.response && err.response.status === 401) {
      setError("Cpf ou senha inválidos");
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
            <label htmlFor="cpf">Cpf</label>
            <input
              type="text"
              id="cpf"
              value={cpf}
              onChange={(e) => setCpf(e.target.value)}
              placeholder="Cpf"
              required
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