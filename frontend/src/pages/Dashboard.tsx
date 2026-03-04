import React from 'react';
import { Sidebar } from '../components/Sidebar';
import { ActivityHeatmap } from '../components/ActivityHeatmap';

export function Dashboard() {
    return (
        <div className="flex h-screen w-full bg-background-light dark:bg-background-dark font-display text-[#111618] dark:text-gray-200 overflow-hidden">
            <Sidebar displayName="Alex's Space" />

            <main className="flex-1 h-full overflow-y-auto overflow-x-hidden relative">
                <div className="max-w-6xl mx-auto px-6 py-8 md:px-10 lg:py-12 flex flex-col gap-8">
                    {/* Header */}
                    <header className="flex flex-col md:flex-row md:items-end justify-between gap-4">
                        <div>
                            <h1 className="text-3xl font-bold text-[#111618] dark:text-white tracking-tight leading-tight">
                                Welcome back, Alex
                            </h1>
                            <div className="flex items-center gap-2 mt-2 text-gray-500 dark:text-gray-400">
                                <span
                                    className="material-symbols-outlined text-emerald-500 text-[20px]"
                                    style={{ fontVariationSettings: "'FILL' 1" }}
                                >
                                    bolt
                                </span>
                                <p className="text-base font-medium">
                                    You've maintained a{' '}
                                    <span className="text-emerald-600 dark:text-emerald-400 font-bold">
                                        12-day streak
                                    </span>
                                    .
                                </p>
                            </div>
                        </div>
                        <div className="flex gap-3">
                            <button className="flex items-center gap-2 px-4 py-2 bg-white dark:bg-[#2d333b] border border-border-light dark:border-gray-700 rounded-lg text-sm font-medium text-gray-700 dark:text-gray-200 shadow-sm hover:bg-gray-50 dark:hover:bg-gray-700 transition-all">
                                <span className="material-symbols-outlined text-[18px]">calendar_today</span>
                                This Month
                            </button>
                            <button className="flex items-center gap-2 px-4 py-2 bg-primary text-white rounded-lg text-sm font-medium shadow-md hover:bg-sky-600 transition-all">
                                <span className="material-symbols-outlined text-[18px]">add</span>
                                New Task
                            </button>
                        </div>
                    </header>

                    {/* Activity Heatmap */}
                    <ActivityHeatmap />

                    {/* Two Column Grid */}
                    <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
                        {/* Recent Commits Card */}
                        <RecentCommitsCard />

                        {/* Daily Focus Card */}
                        <DailyFocusCard />
                    </div>
                </div>
            </main>
        </div>
    );
}

/* ─── Recent Commits ─── */

interface Commit {
    message: string;
    hash: string;
    branch: string;
    timeAgo: string;
}

const commits: Commit[] = [
    { message: 'Refactor authentication middleware', hash: '7a2b9d', branch: 'main', timeAgo: '2h ago' },
    { message: 'Fix: Sidebar overflow on mobile', hash: 'c4f1e2', branch: 'fix/ui-layout', timeAgo: '5h ago' },
    { message: 'Update dependencies', hash: 'b892a1', branch: 'chore/deps', timeAgo: 'Yesterday' },
];

function RecentCommitsCard() {
    return (
        <div className="flex flex-col bg-card-light dark:bg-card-dark rounded-xl border border-border-light dark:border-gray-800 shadow-sm h-full">
            <div className="p-6 border-b border-border-light dark:border-gray-800 flex items-center justify-between">
                <div className="flex items-center gap-3">
                    <span className="material-symbols-outlined text-[24px] text-gray-800 dark:text-white">
                        code_blocks
                    </span>
                    <h2 className="text-lg font-bold text-gray-900 dark:text-white">Recent Commits</h2>
                </div>
                <button className="flex items-center gap-2 px-3 py-1.5 text-xs font-medium bg-gray-100 dark:bg-[#2d333b] text-gray-700 dark:text-gray-300 rounded-full border border-gray-200 dark:border-gray-700 hover:border-gray-400 dark:hover:border-gray-500 transition-colors">
                    <div className="size-2 rounded-full bg-primary" />
                    alex/devhub-ui
                    <span className="material-symbols-outlined text-[14px]">arrow_drop_down</span>
                </button>
            </div>

            <div className="flex-1 p-0">
                <ul className="flex flex-col">
                    {commits.map((commit, i) => (
                        <li
                            key={commit.hash}
                            className={`group flex items-start gap-4 p-4 hover:bg-gray-50 dark:hover:bg-white/5 transition-colors ${
                                i < commits.length - 1
                                    ? 'border-b border-border-light dark:border-gray-800/50'
                                    : ''
                            }`}
                        >
                            <div className="mt-1 text-gray-400 dark:text-gray-500">
                                <span className="material-symbols-outlined text-[20px] rotate-90">commit</span>
                            </div>
                            <div className="flex-1 min-w-0">
                                <div className="flex items-center justify-between mb-1">
                                    <p className="text-sm font-semibold text-gray-900 dark:text-gray-100 truncate pr-2">
                                        {commit.message}
                                    </p>
                                    <span className="text-xs font-mono text-gray-500 bg-gray-100 dark:bg-[#2d333b] px-2 py-0.5 rounded border border-gray-200 dark:border-gray-700">
                                        {commit.hash}
                                    </span>
                                </div>
                                <div className="flex items-center gap-3 text-xs text-gray-500 dark:text-gray-400">
                                    <div className="flex items-center gap-1">
                                        <span className="material-symbols-outlined text-[14px]">call_split</span>
                                        <span>{commit.branch}</span>
                                    </div>
                                    <span>&bull;</span>
                                    <span>{commit.timeAgo}</span>
                                </div>
                            </div>
                        </li>
                    ))}
                </ul>
            </div>

            <div className="p-4 mt-auto border-t border-border-light dark:border-gray-800 bg-gray-50/50 dark:bg-[#2d333b]/30 rounded-b-xl">
                <div className="flex justify-around text-sm">
                    <div className="flex items-center gap-2 text-gray-600 dark:text-gray-400">
                        <span className="material-symbols-outlined text-[18px]">star</span>
                        <span className="font-medium">124</span>
                    </div>
                    <div className="flex items-center gap-2 text-gray-600 dark:text-gray-400">
                        <span className="material-symbols-outlined text-[18px]">fork_right</span>
                        <span className="font-medium">45</span>
                    </div>
                    <div className="flex items-center gap-2 text-gray-600 dark:text-gray-400">
                        <span className="material-symbols-outlined text-[18px]">adjust</span>
                        <span className="font-medium">12 Open</span>
                    </div>
                </div>
            </div>
        </div>
    );
}

/* ─── Daily Focus ─── */

function DailyFocusCard() {
    return (
        <div className="flex flex-col bg-card-light dark:bg-card-dark rounded-xl border border-border-light dark:border-gray-800 shadow-sm h-full relative overflow-hidden">
            <div className="p-6 pb-0 flex items-center justify-between z-10">
                <div className="flex items-center gap-3">
                    <span className="material-symbols-outlined text-[24px] text-gray-800 dark:text-white">
                        target
                    </span>
                    <h2 className="text-lg font-bold text-gray-900 dark:text-white">Daily Focus</h2>
                </div>
                <span className="bg-emerald-100 dark:bg-emerald-900/30 text-emerald-700 dark:text-emerald-400 text-xs font-bold px-2 py-1 rounded uppercase tracking-wide">
                    On Track
                </span>
            </div>

            <div className="flex-1 p-6 flex items-center justify-between gap-6 z-10">
                {/* Progress Ring */}
                <div className="relative size-32 flex-shrink-0">
                    <svg className="size-full -rotate-90 transform" viewBox="0 0 100 100">
                        <circle
                            className="text-gray-100 dark:text-gray-800"
                            cx="50" cy="50" r="40"
                            fill="transparent" stroke="currentColor" strokeWidth="8"
                        />
                        <circle
                            className="text-primary"
                            cx="50" cy="50" r="40"
                            fill="transparent" stroke="currentColor" strokeWidth="8"
                            strokeDasharray="251.2" strokeDashoffset="62.8"
                            strokeLinecap="round"
                        />
                    </svg>
                    <div className="absolute inset-0 flex flex-col items-center justify-center">
                        <span className="text-3xl font-bold text-gray-900 dark:text-white">
                            75<span className="text-sm align-top">%</span>
                        </span>
                        <span className="text-[10px] uppercase text-gray-500 font-semibold tracking-wider">
                            Goal
                        </span>
                    </div>
                </div>

                <div className="flex-1 flex flex-col justify-center gap-4">
                    <div>
                        <p className="text-sm font-medium text-gray-500 dark:text-gray-400 mb-1">
                            Deep Work Session
                        </p>
                        <div className="flex items-center gap-2">
                            <h3 className="text-2xl font-mono font-medium text-gray-900 dark:text-white">
                                02:14:30
                            </h3>
                            <span className="size-2 rounded-full bg-emerald-500 animate-pulse" />
                        </div>
                    </div>
                    <div className="flex flex-col gap-2">
                        <div className="flex items-center justify-between text-sm">
                            <span className="text-gray-600 dark:text-gray-400">Tasks Closed</span>
                            <span className="font-bold text-gray-900 dark:text-white">5 / 8</span>
                        </div>
                        <div className="h-1.5 w-full bg-gray-100 dark:bg-gray-700 rounded-full overflow-hidden">
                            <div className="h-full bg-emerald-500 rounded-full" style={{ width: '62.5%' }} />
                        </div>
                    </div>
                </div>
            </div>

            <div className="p-6 pt-0 mt-auto z-10">
                <button className="w-full py-3 px-4 bg-primary hover:bg-sky-600 text-white font-semibold rounded-lg shadow-lg shadow-primary/20 transition-all flex items-center justify-center gap-2">
                    <span className="material-symbols-outlined text-[20px]">play_arrow</span>
                    Start Focus Session
                </button>
            </div>

            {/* Decorative background element */}
            <div className="absolute top-0 right-0 w-32 h-32 bg-primary/5 rounded-bl-full pointer-events-none z-0" />
        </div>
    );
}
