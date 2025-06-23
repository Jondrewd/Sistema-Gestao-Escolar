import { useState } from 'react';
import { Outlet } from 'react-router-dom';
import './AdminDashboard.css';
import ClassManager from '../../components/ClassManager/ClassManager';
import SubjectManager from '../../components/SubjectManager/SubjectManager';
import UserManager from '../../components/UserManager/UserManager';

import OverviewPainel from '../../components/OverviewPainel/OverviewPainel';

const AdminDashboard = () => {
  const [activeTab, setActiveTab] = useState('overview');
  const [selectedItem, setSelectedItem] = useState(null);

  const tabs = [
    { id: 'overview', label: 'Visão Geral' },
    { id: 'classes', label: 'Turmas' },
    { id: 'subjects', label: 'Matérias' },
    { id: 'users', label: 'Usuários' },
  ];

  return (
    <div className="admin-dashboard">
      <header className="dashboard-header">
        <div className='header-content'>
          <h1>Painel Administrativo</h1>
          <div className="user-info">
            <span>Administrador</span>
            <div className="admin-badge">ADM</div>
          </div>
        </div>
      </header>

      <div className="dashboard-container">
        <aside className="sidebar">
          <nav className="sidebar-nav">
            {tabs.map(tab => (
              <button
                key={tab.id}
                className={`nav-item ${activeTab === tab.id ? 'active' : ''}`}
                onClick={() => {
                  setActiveTab(tab.id);
                  setSelectedItem(null);
                }}
              >
                {tab.label}
              </button>
            ))}
          </nav>
        </aside>

        <main className="main-content">
          {activeTab === 'overview' && <OverviewPainel />}
          
          {activeTab === 'classes' && (
            <ClassManager 
              onSelect={setSelectedItem} 
              selectedItem={selectedItem}
            />
          )}
          
          {activeTab === 'subjects' && (
            <SubjectManager 
              onSelect={setSelectedItem} 
              selectedItem={selectedItem}
            />
          )}
          
          {activeTab === 'users' && (
            <UserManager 
              onSelect={setSelectedItem} 
              selectedItem={selectedItem}
            />
          )}

        </main>
      </div>
    </div>
  );
};

export default AdminDashboard;