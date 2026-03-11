import React, { useEffect, useState } from 'react';
import { useSearchParams, useNavigate, Link } from 'react-router-dom';
import { joinProjectByToken } from '../api/endpoints';
import { useAuth } from '../auth/AuthContext';

export function JoinProject() {
    const [searchParams] = useSearchParams();
    const navigate = useNavigate();
    const { isAuthenticated } = useAuth();
    const token = searchParams.get('token');

    const [status, setStatus] = useState<'loading' | 'success' | 'error'>('loading');
    const [message, setMessage] = useState('');
    const [projectId, setProjectId] = useState<number | null>(null);

    useEffect(() => {
        if (!token) {
            setStatus('error');
            setMessage('Invalid invite link.');
            return;
        }
        if (!isAuthenticated) {
            navigate(`/login`, { state: { from: `/join?token=${token}` } });
            return;
        }
        joinProjectByToken(token)
            .then((project) => {
                setProjectId(project.projectId);
                setStatus('success');
                setMessage(`You joined "${project.projectName}" successfully!`);
            })
            .catch((err) => {
                setStatus('error');
                setMessage(err?.message || 'This invite link is invalid or has expired.');
            });
    }, [token, isAuthenticated, navigate]);

    return (
        <div className="min-h-screen flex items-center justify-center bg-background-light dark:bg-background-dark px-4">
            <div className="bg-white dark:bg-[#161b22] border border-slate-200 dark:border-[#30363d] rounded-xl shadow-2xl w-full max-w-md p-8 text-center">
                {status === 'loading' && (
                    <>
                        <div className="w-10 h-10 border-4 border-primary border-t-transparent rounded-full animate-spin mx-auto mb-4" />
                        <p className="text-gray-600 dark:text-gray-400 font-medium">Joining project...</p>
                    </>
                )}
                {status === 'success' && (
                    <>
                        <div className="w-14 h-14 rounded-full bg-emerald-100 dark:bg-emerald-900/30 flex items-center justify-center mx-auto mb-4">
                            <span className="material-symbols-outlined text-emerald-600 dark:text-emerald-400 text-[32px]">check_circle</span>
                        </div>
                        <h1 className="text-xl font-bold text-gray-900 dark:text-white mb-2">You're in!</h1>
                        <p className="text-gray-500 dark:text-gray-400 mb-6">{message}</p>
                        <Link
                            to={projectId ? `/project/${projectId}` : '/projects'}
                            className="inline-flex items-center gap-2 px-6 py-3 bg-primary text-white rounded-lg font-semibold hover:bg-primary/90 transition-all shadow-lg shadow-primary/20"
                        >
                            <span className="material-symbols-outlined text-[18px]">arrow_forward</span>
                            Go to Project
                        </Link>
                    </>
                )}
                {status === 'error' && (
                    <>
                        <div className="w-14 h-14 rounded-full bg-red-100 dark:bg-red-900/30 flex items-center justify-center mx-auto mb-4">
                            <span className="material-symbols-outlined text-red-500 dark:text-red-400 text-[32px]">error</span>
                        </div>
                        <h1 className="text-xl font-bold text-gray-900 dark:text-white mb-2">Invalid Invite</h1>
                        <p className="text-gray-500 dark:text-gray-400 mb-6">{message}</p>
                        <Link
                            to="/projects"
                            className="inline-flex items-center gap-2 px-6 py-3 bg-gray-100 dark:bg-surface-dark-alt text-gray-700 dark:text-gray-200 rounded-lg font-semibold hover:bg-gray-200 dark:hover:bg-surface-dark transition-all"
                        >
                            Back to Projects
                        </Link>
                    </>
                )}
            </div>
        </div>
    );
}

