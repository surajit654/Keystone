import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

function Login() {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const { login, loading } = useAuth();
  const navigate = useNavigate();

  const handleLogin = async (e) => {
    e.preventDefault();
    setError('');

    try {
      const user = await login(email, password);
      if (user.role === 'CUSTOMER') {
        navigate('/portal');
      } else if (user.role === 'TECHNICIAN') {
        navigate('/technicians');
      } else {
        navigate('/dashboard');
      }
    } catch (err) {
      setError(err.message || 'Login failed. Check your credentials.');
    }
  };

  const handleQuickLogin = (quickEmail, quickPassword) => {
    setEmail(quickEmail);
    setPassword(quickPassword);
  };

  return (
    <div className="login-wrapper">
      <div className="login-card">
        <div className="login-header">
          <div className="brand-logo" style={{ margin: '0 auto 16px', width: '48px', height: '48px', fontSize: '24px' }}>
            K
          </div>
          <h1 className="page-title" style={{ fontSize: '24px', marginBottom: '8px' }}>Project KEYSTONE</h1>
          <p className="page-subtitle">Meridian Facilities Management Platform</p>
        </div>

        {error && (
          <div style={{
            backgroundColor: 'rgba(244, 63, 94, 0.15)',
            border: '1px solid rgba(244, 63, 94, 0.3)',
            borderRadius: '8px',
            padding: '12px 16px',
            color: '#f43f5e',
            fontSize: '13px',
            marginBottom: '20px',
            display: 'flex',
            alignItems: 'center',
            gap: '8px'
          }}>
            <span>⚠️</span>
            <span>{error}</span>
          </div>
        )}

        <form onSubmit={handleLogin} style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
          <div className="form-group">
            <label className="form-label">Work Email</label>
            <input
              className="form-input"
              type="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              placeholder="e.g. dispatcher@keystone.com"
              required
            />
          </div>

          <div className="form-group">
            <label className="form-label">Password</label>
            <input
              className="form-input"
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              placeholder="••••••••"
              required
            />
          </div>

          <button
            type="submit"
            className="btn btn-primary"
            style={{ width: '100%', marginTop: '8px', padding: '12px' }}
            disabled={loading}
          >
            {loading ? 'Authenticating...' : 'Sign In to Workspace'}
          </button>
        </form>

        <div className="login-badge-list">
          <div style={{ fontSize: '11px', fontWeight: 600, color: 'var(--text-dim)', textTransform: 'uppercase', letterSpacing: '0.5px', marginTop: '12px' }}>
            Quick Demo Logins (Click to autofill):
          </div>

          <button
            type="button"
            className="quick-user-btn"
            onClick={() => handleQuickLogin('dispatcher@keystone.com', 'password')}
          >
            <span><strong>Dispatcher</strong>: dispatcher@keystone.com</span>
            <span className="badge badge-high" style={{ fontSize: '10px' }}>Dispatch</span>
          </button>

          <button
            type="button"
            className="quick-user-btn"
            onClick={() => handleQuickLogin('technician@keystone.com', 'password')}
          >
            <span><strong>Technician</strong>: technician@keystone.com</span>
            <span className="badge badge-medium" style={{ fontSize: '10px' }}>Field Tech</span>
          </button>

          <button
            type="button"
            className="quick-user-btn"
            onClick={() => handleQuickLogin('manager@keystone.com', 'password')}
          >
            <span><strong>Manager</strong>: manager@keystone.com</span>
            <span className="badge badge-low" style={{ fontSize: '10px' }}>Admin</span>
          </button>

          <button
            type="button"
            className="quick-user-btn"
            onClick={() => handleQuickLogin('customer@keystone.com', 'password')}
          >
            <span><strong>Customer</strong>: customer@keystone.com</span>
            <span className="badge" style={{ fontSize: '10px', backgroundColor: 'var(--bg-tertiary)', color: 'var(--text-main)' }}>Client</span>
          </button>
        </div>
      </div>
    </div>
  );
}

export default Login;