import React, { useState } from 'react';
import styles from './LoginCard.module.css';
import axios from 'axios';

const BACKEND_URL = "http://localhost:9000";


export function LoginCard() {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [showPassword, setShowPassword] = useState(false);
    const [isLoading, setIsLoading] = useState(false);
    const [error, setError] = useState('');

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setIsLoading(true);

        try {
            console.log(`the password is ${password}, and the email is ${email}`); 
            const { data } = await axios.post(`${BACKEND_URL}/api/login`, {
                email,
                password
            });
            
            if (data.ok) {
                console.log(`Login successful: ${data}`);
            }
            else {
                setError(data.message || 'invalid email or password');
                console.error(`Login error: ${data.message}`);
            }
        }
        catch(error) {
            if (axios.isAxiosError(error)) {
                if(error.response) {
                    setError(error.response.data?.message || 'Invalid email or password');
                }
                else if (error.request) {
                    setError('Unable to connect to server. Please try again.');
                }
                else {
                    setError('An error occurred. Please try again.');
                }
            }
            else {
                setError('An unexpected error occured');
            }
            console.error('Login failed:', error);
        }
        finally {
            setIsLoading(false);
        }
    };

    return (
        <div className='bg-white dark:bg-[#1b2327] border border-gray-200 dark:border-[#3b4c54] rounded-xl p-8 shadow-xl'>
        <form 
            className='flex flex-col gap-4'
            onSubmit={handleSubmit}
        >
            <button className='flex w-full cursor-pointer items-center justify-center rounded-lg h-12 px-5 bg-[#24292e] hover:bg-[#1b1f23] text-white gap-3 text-base font-bold transition-all'>
                <svg aria-hidden="true" className="w-5 h-5" fill="currentColor" viewBox="0 0 24 24">
                    <path clipRule="evenodd" d="M12 2C6.477 2 2 6.484 2 12.017c0 4.425 2.865 8.18 6.839 9.504.5.092.682-.217.682-.483 0-.237-.008-.868-.013-1.703-2.782.605-3.369-1.343-3.369-1.343-.454-1.158-1.11-1.466-1.11-1.466-.908-.62.069-.608.069-.608 1.003.07 1.531 1.032 1.531 1.032.892 1.53 2.341 1.088 2.91.832.092-.647.35-1.088.636-1.338-2.22-.253-4.555-1.113-4.555-4.951 0-1.093.39-1.988 1.029-2.688-.103-.253-.446-1.272.098-2.65 0 0 .84-.27 2.75 1.026A9.564 9.564 0 0112 6.844c.85.004 1.705.115 2.504.337 1.909-1.296 2.747-1.027 2.747-1.027.546 1.379.202 2.398.1 2.651.64.7 1.028 1.595 1.028 2.688 0 3.848-2.339 4.695-4.566 4.943.359.309.678.92.678 1.855 0 1.338-.012 2.419-.012 2.747 0 .268.18.58.688.482A10.019 10.019 0 0022 12.017C22 6.484 17.522 2 12 2z" fillRule="evenodd"></path>
                </svg>
                <span>Continue with GitHub</span>
            </button>

            <div className="relative flex py-5 items-center">
                <div className="flex-grow border-t border-gray-200 dark:border-[#3b4c54]"></div>
                    <span className="flex-shrink mx-4 text-gray-400 dark:text-[#9cb0ba] text-xs font-medium uppercase tracking-wider">or email</span>
                <div className="flex-grow border-t border-gray-200 dark:border-[#3b4c54]"></div>
            </div>
            
            {error && (
                <div className="bg-red-50 dark:bg-red-900/20 border border-red-200 dark:border-red-800 text-red-800 dark:text-red-200 px-4 py-3 rounded-lg text-sm">
                    <div className="flex items-center gap-2">
                        <span className="material-symbols-outlined text-lg">error</span>
                        <span>{error}</span>
                    </div>
                </div>
            )}

            <div className="flex flex-col gap-4">
                <div className='flex flex-col gap-2'>
                <label className='text-gray-900 dark:text-white text-sm font-semibold leading-normal'>
                    Email address
                </label>
                <div className='relative'>
                    <span className="material-symbols-outlined absolute left-3 top-1/2 -translate-y-1/2 text-gray-500 dark:text-[#9cb0ba] text-xl">mail</span>
                    <input 
                        className='flex w-full rounded-lg h-12 pl-10 pr-4 border border-gray-200 bg-gray-50 dark:bg-gray-800 dark:border-gray-700 dark:text-whit'
                        placeholder="name@company.com" 
                        type="email" 
                        value={email}
                        onChange={(e) => setEmail(e.target.value)}
                        required
                    />
                </div>
                </div>

                    <div className="flex flex-col gap-2">
                        <div className="flex justify-between items-center">
                            <label className="text-gray-900 dark:text-white text-sm font-semibold leading-normal">Password</label>
                        </div>
                        <div className="relative">
                            <span className="material-symbols-outlined absolute left-3 top-1/2 -translate-y-1/2 text-gray-500 dark:text-[#9cb0ba] text-xl">lock</span>
                            <input 
                                className="form-input flex w-full rounded-lg text-gray-900 dark:text-white focus:outline-0 focus:ring-2 focus:ring-primary/50 border border-gray-200 dark:border-[#3b4c54] bg-gray-50 dark:bg-[#1b2327] h-12 pl-10 pr-10 placeholder:text-gray-400 dark:placeholder:text-[#9cb0ba] text-sm font-normal"
                                placeholder="••••••••"
                                type="password"
                                value={password}
                                onChange={e => setPassword(e.target.value)}
                                required
                            />
                            <button 
                                className="absolute right-3 top-1/2 -translate-y-1/2 text-gray-500 dark:text-[#9cb0ba] hover:text-primary" 
                                type="button"
                                onClick={() => setShowPassword(!showPassword)}
                            >
                                <span className="material-symbols-outlined text-xl">
                                    {showPassword ? "visibility" : "visibility_off"}
                                </span>
                            </button>
                        </div>
                        <a className="text-primary text-xs font-bold hover:underline" href="#">Forgot password?</a>
                    </div>
            </div>
            <button 
                className='flex w-full cursor-pointer items-center justify-center rounded-lg h-12 px-5 bg-primary text-white text-base font-bold transition-all hover:brightness-110 shadow-lg shadow-primary/20 mt-2'
                type="submit"
                disabled={isLoading}
            >
                {isLoading ? "Signing in ..." :"Sign in"}
            </button>
        </form>
        <div className='flex flex-col items-center gap-3 my-6'>
            <p className="text-gray-500 dark:text-[#9cb0ba] text-sm font-normal leading-normal text-center">
                        New to Developer Hub? 
            </p>
            <a className="text-primary font-bold hover:underline" href="#">Create an account</a>
        </div>
        </div>
    );
}
