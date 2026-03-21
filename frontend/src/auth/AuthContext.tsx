import React, { createContext, useContext, useEffect, useState, useRef, useCallback, type ReactNode } from 'react';
import type { LoginResponse } from '../api/types';

interface AuthCtx {
    user: LoginResponse | null;
    token: string | null;
    isAuthenticated: boolean;
    login: (token: string, user: LoginResponse) => void;
    logout: () => void;
    updateUser: (user: LoginResponse) => void;
}

const AuthContext = createContext<AuthCtx>({
    user: null,
    token: null,
    isAuthenticated: false,
    login: () => {},
    logout: () => {},
    updateUser: () => {},
});

function getTokenExpiration(jwt: string): number | null {
    try {
        const parts = jwt.split('.');
        if (parts.length !== 3) return null;
        const payload = JSON.parse(atob(parts[1].replace(/-/g, '+').replace(/_/g, '/')));
        return typeof payload.exp === 'number' ? payload.exp : null;
    } catch {
        return null;
    }
}

function isTokenExpired(jwt: string): boolean {
    const exp = getTokenExpiration(jwt);
    if (exp === null) return true;
    return Date.now() >= exp * 1000;
}

export function AuthProvider({ children }: { children: ReactNode }) {
    const [token, setToken] = useState<string | null>(() => {
        const stored = localStorage.getItem('token');
        if (stored && isTokenExpired(stored)) {
            localStorage.removeItem('token');
            localStorage.removeItem('user');
            return null;
        }
        return stored;
    });
    const [user, setUser] = useState<LoginResponse | null>(() => {
        if (!localStorage.getItem('token')) {
            localStorage.removeItem('user');
            return null;
        }
        const stored = localStorage.getItem('user');
        return stored ? JSON.parse(stored) : null;
    });

    const logoutTimerRef = useRef<ReturnType<typeof setTimeout> | null>(null);

    const logout = useCallback(() => {
        if (logoutTimerRef.current) {
            clearTimeout(logoutTimerRef.current);
            logoutTimerRef.current = null;
        }
        setToken(null);
        setUser(null);
        localStorage.removeItem('token');
        localStorage.removeItem('user');
    }, []);

    const scheduleAutoLogout = useCallback(
        (jwt: string) => {
            if (logoutTimerRef.current) {
                clearTimeout(logoutTimerRef.current);
                logoutTimerRef.current = null;
            }
            const exp = getTokenExpiration(jwt);
            if (exp === null) return;
            const msUntilExpiry = exp * 1000 - Date.now();
            if (msUntilExpiry <= 0) {
                logout();
                return;
            }
            logoutTimerRef.current = setTimeout(() => {
                logout();
                window.location.href = '/login';
            }, msUntilExpiry);
        },
        [logout],
    );

    useEffect(() => {
        if (token) {
            localStorage.setItem('token', token);
            scheduleAutoLogout(token);
        } else {
            localStorage.removeItem('token');
        }
    }, [token, scheduleAutoLogout]);

    useEffect(() => {
        if (user) {
            localStorage.setItem('user', JSON.stringify(user));
        } else {
            localStorage.removeItem('user');
        }
    }, [user]);

    useEffect(() => {
        return () => {
            if (logoutTimerRef.current) clearTimeout(logoutTimerRef.current);
        };
    }, []);

    const login = (jwt: string, userData: LoginResponse) => {
        setToken(jwt);
        setUser(userData);
    };

    const updateUser = (userData: LoginResponse) => {
        setToken(userData.jwtToken);
        setUser(userData);
    };


    return (
        <AuthContext.Provider value={{ user, token, isAuthenticated: !!token, login, logout, updateUser }}>
            {children}
        </AuthContext.Provider>
    );
}

export function useAuth() {
    return useContext(AuthContext);
}
