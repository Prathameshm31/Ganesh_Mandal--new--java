import { createContext, useContext, useState, useEffect, useCallback, useMemo } from 'react';

const MOCK_USERS = [
  { id: 1, username: 'admin', password: 'admin123', name: 'Admin User', role: 'admin', avatar: '' },
  { id: 2, username: 'user', password: 'user123', name: 'Regular User', role: 'user', avatar: '' },
];

const AuthContext = createContext(null);

export function useAuth() {
  const ctx = useContext(AuthContext);
  if (!ctx) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return ctx;
}

export function AuthProvider({ children }) {
  const [user, setUser] = useState(() => {
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

  const login = useCallback((username, password) => {
    const found = MOCK_USERS.find(u => u.username === username && u.password === password);
    if (!found) {
      throw new Error('Invalid username or password');
    }
    const { password: _, ...safeUser } = found;
    setUser(safeUser);
    return safeUser;
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
