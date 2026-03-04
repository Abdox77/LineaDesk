import React, { createContext, useContext, useEffect, useState, type ReactNode } from 'react';
import type { LoginResponse } from '../api/types';

interface AuthCtx {
    user: LoginResponse | null;
    token: string | null;
    isAuthenticated: boolean;
    login: (token: string, user: LoginResponse) => void;
    logout: () => void;
}

const AuthContext = createContext<AuthCtx>({
    user: null,
    token: null,
    isAuthenticated: false,
    login: () => {},
    logout: () => {},
});

export function AuthProvider({ children }: { children: ReactNode }) {
    const [token, setToken] = useState<string | null>(() => localStorage.getItem('token'));
    const [user, setUser] = useState<LoginResponse | null>(() => {
        const stored = localStorage.getItem('user');
        return stored ? JSON.parse(stored) : null;
    });

    useEffect(() => {
        if (token) {
            localStorage.setItem('token', token);
        } else {
            localStorage.removeItem('token');
        }
    }, [token]);

    useEffect(() => {
        if (user) {
            localStorage.setItem('user', JSON.stringify(user));
        } else {
            localStorage.removeItem('user');
        }
    }, [user]);

    const login = (jwt: string, userData: LoginResponse) => {
        setToken(jwt);
        setUser(userData);
    };

    const logout = () => {
        setToken(null);
        setUser(null);
        localStorage.removeItem('token');
        localStorage.removeItem('user');
    };

    return (
        <AuthContext.Provider value={{ user, token, isAuthenticated: !!token, login, logout }}>
            {children}
        </AuthContext.Provider>
    );
}

export function useAuth() {
    return useContext(AuthContext);
}
