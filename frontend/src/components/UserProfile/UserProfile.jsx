import './UserProfile.css';
import { useEffect, useState } from 'react';

const UserProfile = ({ user }) => {
  const [avatarInitials, setAvatarInitials] = useState('');

  useEffect(() => {
    if (user && user.name) {
      setAvatarInitials(getInitials(user.name));
    } else {
      const userType = sessionStorage.getItem('userType');
      setAvatarInitials(userType === 'teacher' ? 'P' : 'A');
    }
  }, [user]);

  const getInitials = (name) => {
    if (!name || typeof name !== 'string') return '';
    
    const parts = name.trim().split(' ');
    if (parts.length === 1) return parts[0].charAt(0).toUpperCase();
    
    return `${parts[0].charAt(0)}${parts[parts.length - 1].charAt(0)}`.toUpperCase();
  };

  const userName = user?.name || (user?.userType === 'teacher' ? 'Professor' : 'Aluno');
  const userEmail = user?.email || 'email@escola.com';

  return (
    <div className="user-profile">
      <div className="user-info">
        <span className="user-name" title={userName}>{userName}</span>
        <span className="user-email" title={userEmail}>{userEmail}</span>
      </div>
      <div className="avatar" aria-label={`Avatar de ${userName}`}>
        {avatarInitials}
      </div>
    </div>
  );
};

export default UserProfile;