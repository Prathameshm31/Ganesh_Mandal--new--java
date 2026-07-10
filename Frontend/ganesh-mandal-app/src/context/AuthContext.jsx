import { createContext, useContext, useState, useEffect, useCallback, useMemo } from 'react';
import api from '../services/api';

const AuthContext = createContext(null);

// TEMPORARY: Set VITE_SKIP_AUTH=true in .env to bypass login for testing.
// Revert by setting VITE_SKIP_AUTH=false or removing it from .env.
const SKIP_AUTH = import.meta.env.VITE_SKIP_AUTH === 'true';

const MOCK_USER = { id: 1, username: 'admin', name: 'Admin User', role: 'admin' };

export function useAuth() {
  const ctx = useContext(AuthContext);
  if (!ctx) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return ctx;
}

export function AuthProvider({ children }) {
  const [user, setUser] = useState(() => {
    if (SKIP_AUTH) return MOCK_USER;
    try {
      const stored = localStorage.getItem('ganeshMandalAuthUser');
      return stored ? JSON.parse(stored) : null;
    } catch {
      return null;
    }
  });

  useEffect(() => {
    try {
      if (user) {
        localStorage.setItem('ganeshMandalAuthUser', JSON.stringify(user));
      } else {
        localStorage.removeItem('ganeshMandalAuthUser');
      }
    } catch {
      // localStorage unavailable
    }
  }, [user]);

  const login = useCallback(async (username, password) => {
    if (SKIP_AUTH) {
      setUser(MOCK_USER);
      return MOCK_USER;
    }
    try {
      const response = await api.post('/auth/login', { username, password });
      const userData = response.data;
      setUser(userData);
      return userData;
    } catch (err) {
      const message = err.response?.data?.message || 'Invalid username or password';
      throw new Error(message);
    }
  }, []);

  const logout = useCallback(() => {
    setUser(null);
  }, []);

  const value = useMemo(
    () => ({
      user,
      isAuthenticated: !!user,
      login,
      logout,
    }),
    [user, login, logout],
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}
