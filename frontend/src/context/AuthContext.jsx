import { createContext, useContext, useState, useEffect } from 'react';
import { authApi } from '../api/apiClient';

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [token, setToken] = useState(() => localStorage.getItem('keystone_token'));
  const [user, setUser] = useState(() => {
    const saved = localStorage.getItem('keystone_user');
    return saved ? JSON.parse(saved) : null;
  });
  const [loading, setLoading] = useState(false);

  // Helper to parse JWT payload
  const parseJwt = (jwtToken) => {
    try {
      const base64Url = jwtToken.split('.')[1];
      const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
      const jsonPayload = decodeURIComponent(
        atob(base64)
          .split('')
          .map((c) => '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2))
          .join('')
      );
      return JSON.parse(jsonPayload);
    } catch {
      return null;
    }
  };

  const login = async (email, password) => {
    setLoading(true);
    try {
      const data = await authApi.login(email, password);
      const jwtToken = data.token;
      const decoded = parseJwt(jwtToken);
      const userData = {
        email: decoded?.sub || email,
        role: decoded?.role || 'DISPATCHER',
      };

      localStorage.setItem('keystone_token', jwtToken);
      localStorage.setItem('keystone_user', JSON.stringify(userData));
      setToken(jwtToken);
      setUser(userData);
      return userData;
    } finally {
      setLoading(false);
    }
  };

  const logout = () => {
    localStorage.removeItem('keystone_token');
    localStorage.removeItem('keystone_user');
    setToken(null);
    setUser(null);
  };

  return (
    <AuthContext.Provider value={{ token, user, isAuthenticated: !!token, loading, login, logout }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
}
