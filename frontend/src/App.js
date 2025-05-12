import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Login  from './pages/Login/Login'
import Register from './pages/Register/Register';

function App() {
  return (
    <Router>
      <div className="App">
        <Routes>
          <Route path="/" element={<Login />} />
          <Route path="/login" element={<Login />} />
          <Route path="/register" element={<Register />} />
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

*/ 

