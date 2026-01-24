import React from 'react';
import './App.css';
import './index.css';
import { LoginCard } from './pages/LoginCard';

function App() {
    return (
        <div className="bg-background-light dark:bg-background-dark min-h-screen flex flex-col font-display">
            <header className="flex items-center justify-between border-b border-gray-200 px-6 py-4">
                <h2 className="text-xl font-bold">Developer Hub</h2>
            </header>

            <main className="flex-1 flex items-center justify-center px-4 py-12 relative overflow-hidden">
                <div className="absolute top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 w-[500px] h-[500px] bg-primary/5 rounded-full blur-[120px] pointer-events-none"></div>
                <div className="layout-content-container flex flex-col max-w-[480px] w-full z-10">
                    <div className="mb-8">
                        <h1 className="text-black dark:text-white tracking-tight text-[32px] font-extrabold leading-tight text-center">
                            Welcome back
                        </h1>
                        <p className="text-gray-500 dark:text-[#9cb0ba] text-sm font-normal leading-normal text-center mt-2">
                            Log in to your developer workspace
                        </p>
                    </div>
                    <LoginCard />
                </div>
            </main>

            <footer className="py-8 text-center text-gray-400 text-xs">
                © 2026 Developer Hub Inc.
            </footer>
        </div>
    );
}

export default App;
