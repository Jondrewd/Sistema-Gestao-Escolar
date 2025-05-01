# 🎓 Sistema de Gestão de Alunos e Avaliações

Sistema completo para gestão acadêmica de instituições de ensino, desenvolvido com **Java (Spring Boot)** no backend e **React** no frontend. A aplicação permite que professores registrem notas, avaliações e presenças, enquanto alunos acompanham seu desempenho acadêmico de forma segura e intuitiva.

---

## ✨ Funcionalidades Principais

- Cadastro e gerenciamento de **Alunos** e **Professores**
- Registro de **Disciplinas** e **Turmas**
- **Lançamento de Notas** e **Presença** por professores
- **Visualização de Boletins** e **Frequência** por alunos
- **Sistema de Login Seguro** com JWT (JSON Web Token)
- **Relatórios automáticos em PDF** (JasperReports/iText)
- **Painel Administrativo** para gestão da instituição

---

## 🛠️ Tecnologias Utilizadas

### Backend – Java & Spring Boot
- Java 21
- Spring Boot 3
- Spring Security + JWT
- Spring Data JPA
- PostgreSQL
- Flyway (migrações de banco de dados)
- Lombok
- MapStruct (mapeamento DTO)
- Swagger (documentação da API)
- Docker (ambiente do banco de dados)

### Frontend – React & Vite
- React 18
- Vite
- TailwindCSS
- Axios
- React Router DOM
- Context API (autenticação e estado global)

---

## 📁 Organização do Projeto

```plaintext
gestao-alunos/
├── backend/
│   ├── src/main/java/com/gestaoescolar/
│   │   ├── controllers/
│   │   ├── services/
│   │   ├── repositories/
│   │   ├── entities/
│   │   ├── dtos/
│   │   ├── config/
│   │   ├── exceptions/
│   │   └── security/
│   └── resources/
│       ├── application.properties
│       └── db/migration/
├── frontend/
│   ├── src/
│   │   ├── components/
│   │   ├── pages/
│   │   ├── services/
│   │   ├── contexts/
│   │   ├── hooks/
│   │   ├── routes/
│   │   └── App.jsx
│   ├── public/
│   └── vite.config.js
```

---

## 🔐 Sistema de Autenticação

- Autenticação baseada em JWT.
- Perfis distintos:
  - **Professor:** pode lançar notas e presenças.
  - **Aluno:** pode visualizar seu boletim e presença.
- As rotas são protegidas com base nos **roles** do usuário.

---

## 📋 Endpoints da API (Principais)

| Método | Endpoint                        | Descrição                         | Acesso       |
|--------|----------------------------------|------------------------------------|--------------|
| POST   | `/api/auth/login`               | Login e geração do token JWT       | Público      |
| POST   | `/api/alunos`                   | Cadastro de aluno                  | Admin        |
| GET    | `/api/alunos/{id}`              | Buscar aluno por ID                | Privado      |
| POST   | `/api/professores`              | Cadastro de professor              | Admin        |
| POST   | `/api/disciplinas`              | Cadastro de disciplina             | Admin        |
| POST   | `/api/turmas`                   | Cadastro de turma                  | Admin        |
| POST   | `/api/avaliacoes`               | Criar avaliação                    | Professor    |
| POST   | `/api/notas`                    | Lançar nota para aluno             | Professor    |
| POST   | `/api/presencas`                | Lançar presença para aluno         | Professor    |
| GET    | `/api/boletim/{alunoId}`        | Visualizar notas e frequência      | Aluno        |

---

## 🖥️ Telas do Frontend

- **Tela de Login**
- **Dashboard do Professor**
  - Listagem de turmas
  - Lançamento de notas e presenças
- **Dashboard do Aluno**
  - Visualização de boletim e frequência
- **Painel Administrativo**
  - Cadastro de alunos, professores, turmas e disciplinas
- **Relatórios**
  - Geração de boletins em PDF

---

## 📦 Funcionalidades Futuras

- 📄 Geração automática de boletins em PDF
- 📧 Envio de e-mails para alunos com baixa frequência
- 📊 Dashboards com gráficos de desempenho
- 🔐 Recuperação de senha via e-mail
- 🌙 Tema escuro (dark mode)

---

## 🚀 Como Rodar o Projeto Localmente

### Backend (Spring Boot)

```bash
# Clone o repositório
git clone https://github.com/seuusuario/gestao-alunos.git
cd gestao-alunos/backend

# Configure o banco de dados em src/main/resources/application.properties

# Rode as migrações do Flyway
./mvnw flyway:migrate

# Inicie a aplicação
./mvnw spring-boot:run
```

### Frontend (React)

```bash
# Acesse o frontend
cd ../frontend

# Instale as dependências
npm install

# Inicie o servidor de desenvolvimento
npm run dev
```

---

## 🧠 Aprendizados

- Autenticação segura com JWT e Spring Security
- Integração eficiente entre **Spring Boot** e **React**
- Modelagem de banco relacional com relacionamentos complexos
- Utilização do **Context API** e gerenciamento de estado no frontend
- Implementação de **roles** e autorização por perfis
- Uso de ferramentas como Docker, Swagger e Flyway para produtividade

---

## 📄 Licença

Este projeto está licenciado sob a [MIT License](LICENSE).

---

## 📫 Contato

🔗 [LinkedIn]([https://www.linkedin.com/in/seulinkedin](https://www.linkedin.com/in/jonas-sousa-93ba3826b?utm_source=share&utm_campaign=share_via&utm_content=profile&utm_medium=android_app))  

