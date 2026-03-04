import React, { useEffect } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import { useAuth } from '../auth/AuthContext';

export function OAuthCallback() {
    const [searchParams] = useSearchParams();
    const navigate = useNavigate();
    const { login } = useAuth();

    useEffect(() => {
        const token = searchParams.get('token');
        const error = searchParams.get('error');

        if (token) {
            login(token, {
                id: 0,
                email: '',
                jwtToken: token,
                username: searchParams.get('username') ?? 'Developer',
            });
            navigate('/dashboard', { replace: true });
        } else {
            navigate(`/login?error=${error ?? 'oauth_failed'}`, { replace: true });
        }
    }, [searchParams, navigate, login]);

    return (
        <div className="min-h-screen flex items-center justify-center bg-background-light dark:bg-background-dark">
            <div className="text-center">
                <div className="w-8 h-8 border-4 border-primary border-t-transparent rounded-full animate-spin mx-auto mb-4"></div>
                <p className="text-slate-600 dark:text-slate-400 text-sm font-medium">Completing sign in...</p>
            </div>
        </div>
    );
}
