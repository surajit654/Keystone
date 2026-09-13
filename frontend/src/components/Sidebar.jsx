import { NavLink, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

function Sidebar() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  const getRoleBadgeClass = (role) => {
    switch (role) {
      case 'MANAGER': return 'user-role-badge';
      case 'DISPATCHER': return 'user-role-badge';
      case 'TECHNICIAN': return 'user-role-badge';
      case 'CUSTOMER': return 'user-role-badge';
      default: return 'user-role-badge';
    }
  };

  return (
    <aside className="sidebar">
      <div className="sidebar-header">
        <div className="brand-logo">K</div>
        <div>
          <div className="brand-title">KEYSTONE</div>
          <div className="brand-subtitle">Field Service OS</div>
        </div>
      </div>

      <nav className="sidebar-nav">
        <NavLink to="/dashboard" className={({ isActive }) => `nav-item ${isActive ? 'active' : ''}`}>
          <span>📊</span>
          <span>Dashboard</span>
        </NavLink>

        <NavLink to="/work-orders" className={({ isActive }) => `nav-item ${isActive ? 'active' : ''}`}>
          <span>📋</span>
          <span>Work Orders</span>
        </NavLink>

        {(user?.role === 'DISPATCHER' || user?.role === 'MANAGER') && (
          <NavLink to="/customers" className={({ isActive }) => `nav-item ${isActive ? 'active' : ''}`}>
            <span>🏢</span>
            <span>Customers & Sites</span>
          </NavLink>
        )}

        {(user?.role === 'DISPATCHER' || user?.role === 'MANAGER' || user?.role === 'TECHNICIAN') && (
          <NavLink to="/technicians" className={({ isActive }) => `nav-item ${isActive ? 'active' : ''}`}>
            <span>🔧</span>
            <span>Technicians</span>
          </NavLink>
        )}
      </nav>

      <div className="sidebar-footer">
        {user && (
          <div className="user-profile">
            <div className="user-avatar">
              {user.email ? user.email.charAt(0).toUpperCase() : 'U'}
            </div>
            <div className="user-info">
              <span className="user-email">{user.email}</span>
              <span className={getRoleBadgeClass(user.role)}>{user.role}</span>
            </div>
          </div>
        )}

        <button onClick={handleLogout} className="logout-btn">
          <span>🚪</span>
          <span>Sign Out</span>
        </button>
      </div>
    </aside>
  );
}

export default Sidebar;