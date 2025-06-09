import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Login  from './pages/Login/Login'
import Register from './pages/Register/Register';
import Dashboard from './pages/Dashboard/Dashboard';
import AdminDashboard from './pages/AdminDashboard/AdminDashboard';

function App() {
  return (
    <Router>
      <div className="App">
        <Routes>
          <Route path="/" element={<Login />} />
          <Route path="/login" element={<Login />} />
          <Route path="/register" element={<Register />} />
          <Route path="/dashboard" element={<Dashboard />} />
          <Route path="/admin" element={<AdminDashboard />} />
        </Routes>
      </div>
    </Router>
  );
}

export default App;

/*
Preciso criar:
  Tela de Login e registro  *feito
  Dashboard do Professor
  Listagem de turmas
  Lançamento de notas e presenças
  Dashboard do Aluno
  Visualização de boletim e frequência
  Painel Administrativo
  Cadastro de alunos, professores, turmas e disciplinas
  Relatórios
  Geração de boletins em PDF


  Tela de Login

Dashboard do Professor:

Listar turmas, lançar notas, registrar presenças

Dashboard do Aluno:

Visualizar boletim e frequência

Tela de Cadastro (Admin):

Criar novos usuários (aluno/professor/admin)

Gerenciamento de Turmas e Disciplinas

Página de Relatórios
*/ 

