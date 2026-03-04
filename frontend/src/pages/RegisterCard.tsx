import React, { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import axios from 'axios';
import { NavBar } from '../components/NavBar';
import { AuthFooter } from '../components/AuthFooter';

const BACKEND_URL = 'http://localhost:9000';

export function RegisterCard() {
    const navigate = useNavigate();
    const [name, setName] = useState('');
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [isLoading, setIsLoading] = useState(false);
    const [error, setError] = useState('');

    const handleGithubOAuth = () => {
        window.location.href = `${BACKEND_URL}/oauth2/authorization/github`;
    };

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setIsLoading(true);
        setError('');
        try {
            const { data } = await axios.post(`${BACKEND_URL}/auth/signup`, {
                username: name,
                email,
                password,
            });

            if (data.success) {
                navigate('/login', {
                    state: { message: 'Registration successful! Please log in.' },
                });
            } else {
                setError(data.message || 'Registration failed. Please try again.');
            }
        } catch (err) {
            if (axios.isAxiosError(err)) {
                if (err.response) {
                    setError(err.response.data?.message || 'Registration failed. Please try again.');
                } else {
                    setError('Unable to connect to server. Please try again.');
                }
            } else {
                setError('An unexpected error occurred.');
            }
        } finally {
            setIsLoading(false);
        }
    };

    return (
        <div className="bg-background-light dark:bg-background-dark text-slate-900 dark:text-slate-100 min-h-screen flex flex-col">
            <NavBar ctaLabel="Sign In" ctaHref="/login" />
            <main className="flex-1 flex flex-col items-center justify-center px-4 py-12">
                <div className="w-full max-w-[440px]">
                    {/* Headline */}
                    <div className="mb-8">
                        <h1 className="text-slate-900 dark:text-white tracking-tight text-[32px] font-extrabold leading-tight text-center pb-2 font-display">
                            Join Developer Hub
                        </h1>
                        <p className="text-slate-500 dark:text-[#9cb0ba] text-base font-normal leading-normal text-center">
                            Start your productive journey today.
                        </p>
                    </div>

                    {/* Registration Card */}
                    <div className="bg-white dark:bg-[#161b22] border border-slate-200 dark:border-[#30363d] rounded-xl p-8 shadow-sm">
                        {error && (
                            <div className="mb-5 bg-red-50 dark:bg-red-900/20 border border-red-200 dark:border-red-800 text-red-800 dark:text-red-200 px-4 py-3 rounded-lg text-sm">
                                <div className="flex items-center gap-2">
                                    <span className="material-symbols-outlined text-lg">error</span>
                                    <span>{error}</span>
                                </div>
                            </div>
                        )}

                        <form className="space-y-5" onSubmit={handleSubmit}>
                            {/* Full Name */}
                            <div className="flex flex-col gap-2">
                                <label className="text-slate-700 dark:text-slate-200 text-sm font-semibold leading-normal">Full Name</label>
                                <input
                                    className="form-input flex w-full rounded-lg text-slate-900 dark:text-white border border-slate-300 dark:border-[#30363d] bg-slate-50 dark:bg-[#0d1117] h-12 placeholder:text-slate-400 dark:placeholder:text-[#484f58] px-4 text-sm font-normal focus:outline-none focus:border-primary focus:ring-1 focus:ring-primary transition-all"
                                    placeholder="Enter your full name"
                                    type="text"
                                    value={name}
                                    onChange={e => setName(e.target.value)}
                                    required
                                />
                            </div>

                            {/* Email */}
                            <div className="flex flex-col gap-2">
                                <label className="text-slate-700 dark:text-slate-200 text-sm font-semibold leading-normal">Email Address</label>
                                <input
                                    className="form-input flex w-full rounded-lg text-slate-900 dark:text-white border border-slate-300 dark:border-[#30363d] bg-slate-50 dark:bg-[#0d1117] h-12 placeholder:text-slate-400 dark:placeholder:text-[#484f58] px-4 text-sm font-normal focus:outline-none focus:border-primary focus:ring-1 focus:ring-primary transition-all"
                                    placeholder="name@company.com"
                                    type="email"
                                    value={email}
                                    onChange={e => setEmail(e.target.value)}
                                    required
                                />
                            </div>

                            {/* Password */}
                            <div className="flex flex-col gap-2">
                                <label className="text-slate-700 dark:text-slate-200 text-sm font-semibold leading-normal">Password</label>
                                <input
                                    className="form-input flex w-full rounded-lg text-slate-900 dark:text-white border border-slate-300 dark:border-[#30363d] bg-slate-50 dark:bg-[#0d1117] h-12 placeholder:text-slate-400 dark:placeholder:text-[#484f58] px-4 text-sm font-normal focus:outline-none focus:border-primary focus:ring-1 focus:ring-primary transition-all"
                                    placeholder="Create a strong password"
                                    type="password"
                                    value={password}
                                    onChange={e => setPassword(e.target.value)}
                                    required
                                    minLength={8}
                                />
                                <p className="text-[11px] text-slate-500 dark:text-[#8b949e] mt-1">
                                    Make sure it's at least 8 characters including a number and a lowercase letter.
                                </p>
                            </div>

                            {/* Submit */}
                            <button
                                className="w-full flex items-center justify-center rounded-lg h-12 px-4 bg-primary text-white text-sm font-bold tracking-wide hover:bg-primary/90 transition-all shadow-lg shadow-primary/20 mt-2 disabled:opacity-60"
                                type="submit"
                                disabled={isLoading}
                            >
                                {isLoading ? 'Creating Account...' : 'Create Account'}
                            </button>
                        </form>

                        {/* Divider */}
                        <div className="relative my-8">
                            <div className="absolute inset-0 flex items-center">
                                <span className="w-full border-t border-slate-200 dark:border-[#30363d]"></span>
                            </div>
                            <div className="relative flex justify-center text-xs uppercase">
                                <span className="bg-white dark:bg-[#161b22] px-3 text-slate-500 dark:text-[#8b949e] font-medium tracking-wider">
                                    Or continue with
                                </span>
                            </div>
                        </div>

                        {/* GitHub OAuth Button */}
                        <button
                            className="w-full flex items-center justify-center gap-3 rounded-lg h-12 px-4 bg-white dark:bg-[#21262d] text-slate-900 dark:text-white border border-slate-300 dark:border-[#30363d] text-sm font-bold hover:bg-slate-50 dark:hover:bg-[#30363d] transition-all"
                            type="button"
                            onClick={handleGithubOAuth}
                        >
                            <svg fill="currentColor" height="20" viewBox="0 0 16 16" width="20">
                                <path d="M8 0C3.58 0 0 3.58 0 8c0 3.54 2.29 6.53 5.47 7.59.4.07.55-.17.55-.38 0-.19-.01-.82-.01-1.49-2.01.37-2.53-.49-2.69-.94-.09-.23-.48-.94-.82-1.13-.28-.15-.68-.52-.01-.53.63-.01 1.08.58 1.23.82.72 1.21 1.87.87 2.33.66.07-.52.28-.87.51-1.07-1.78-.2-3.64-.89-3.64-3.95 0-.87.31-1.59.82-2.15-.08-.2-.36-1.02.08-2.12 0 0 .67-.21 2.2.82.64-.18 1.32-.27 2-.27.68 0 1.36.09 2 .27 1.53-1.04 2.2-.82 2.2-.82.44 1.1.16 1.92.08 2.12.51.56.82 1.27.82 2.15 0 3.07-1.87 3.75-3.65 3.95.29.25.54.73.54 1.48 0 1.07-.01 1.93-.01 2.2 0 .21.15.46.55.38A8.013 8.013 0 0016 8c0-4.42-3.58-8-8-8z" />
                            </svg>
                            Continue with GitHub
                        </button>
                    </div>

                    {/* Bottom Navigation */}
                    <div className="mt-8 text-center">
                        <p className="text-sm text-slate-600 dark:text-[#8b949e]">
                            Already have an account?{' '}
                            <Link className="text-primary font-bold hover:underline" to="/login">Log in</Link>
                        </p>
                    </div>

                    <div className="mt-12 text-center">
                        <p className="text-[11px] text-slate-400 dark:text-[#484f58] uppercase tracking-[0.2em] font-semibold">
                            Trusted by 10k+ developers
                        </p>
                        <div className="flex justify-center gap-4 mt-4 opacity-40 grayscale filter">
                            <span className="material-symbols-outlined text-2xl">terminal</span>
                            <span className="material-symbols-outlined text-2xl">deployed_code</span>
                            <span className="material-symbols-outlined text-2xl">javascript</span>
                            <span className="material-symbols-outlined text-2xl">data_object</span>
                        </div>
                    </div>
                </div>
            </main>
            <AuthFooter />
        </div>
    );
}
