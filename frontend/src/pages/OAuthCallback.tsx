import React, { useEffect } from 'react';
import { useSearchParams } from 'react-router-dom';
import { useAuth } from '../auth/AuthContext';

export function OAuthCallback() {
    const [searchParams] = useSearchParams();
    const { login } = useAuth();

    useEffect(() => {
        const token = searchParams.get('token');
        const error = searchParams.get('error');

        if (token) {
            login(token, {
                id: Number(searchParams.get('id') ?? 0),
                email: searchParams.get('email') ?? '',
                jwtToken: token,
                username: searchParams.get('username') ?? 'Developer',
            });
            window.location.replace('/dashboard');
        } else {
            window.location.replace(`/login?error=${error ?? 'oauth_failed'}`);
        }
    }, [searchParams, login]);

    return (
        <div className="min-h-screen flex items-center justify-center bg-background-light dark:bg-background-dark">
            <div className="text-center">
                <div className="w-8 h-8 border-4 border-primary border-t-transparent rounded-full animate-spin mx-auto mb-4"></div>
                <p className="text-slate-600 dark:text-slate-400 text-sm font-medium">Completing sign in...</p>
            </div>
        </div>
    );
}
