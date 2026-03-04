import React, { useEffect, useState } from 'react';
import { Sidebar } from '../components/Sidebar';
import { ActivityHeatmap } from '../components/ActivityHeatmap';
import { useAuth } from '../auth/AuthContext';
import { fetchProjects, fetchHabits } from '../api/endpoints';
import type { ProjectResponseDto, HabitResponseDto, TaskResponseDto } from '../api/types';

export function Dashboard() {
    const { user } = useAuth();
    const displayName = user?.username ?? 'Developer';

    const [projects, setProjects] = useState<ProjectResponseDto[]>([]);
    const [habits, setHabits] = useState<HabitResponseDto[]>([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        async function load() {
            try {
                const [p, h] = await Promise.all([fetchProjects(), fetchHabits()]);
                setProjects(p);
                setHabits(h);
            } catch {
            } finally {
                setLoading(false);
            }
        }
        load();
    }, []);

    const allTasks = projects.flatMap((p) => p.tasks ?? []);
    const doneTasks = allTasks.filter((t) => t.state === 'DONE');
    const totalTasks = allTasks.length;

    const bestStreak = habits.reduce((max, h) => Math.max(max, h.streaks ?? 0), 0);

    return (
        <div className="flex h-screen w-full bg-background-light dark:bg-background-dark font-display text-[#111618] dark:text-gray-200 overflow-hidden">
            <Sidebar displayName={`${displayName}'s Space`} />

            <main className="flex-1 h-full overflow-y-auto overflow-x-hidden relative">
                <div className="max-w-6xl mx-auto px-6 py-8 md:px-10 lg:py-12 flex flex-col gap-8">
                    <header className="flex flex-col md:flex-row md:items-end justify-between gap-4">
                        <div>
                            <h1 className="text-3xl font-bold text-[#111618] dark:text-white tracking-tight leading-tight">
                                Welcome back, {displayName}
                            </h1>
                            {bestStreak > 0 && (
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
                                            {bestStreak}-day streak
                                        </span>
                                        .
                                    </p>
                                </div>
                            )}
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

                    <ActivityHeatmap />

                    <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
                        <ProjectsCard projects={projects} loading={loading} />

                        <DailyFocusCard
                            doneTasks={doneTasks.length}
                            totalTasks={totalTasks}
                            habits={habits}
                            loading={loading}
                        />
                    </div>
                </div>
            </main>
        </div>
    );
}

interface ProjectsCardProps {
    projects: ProjectResponseDto[];
    loading: boolean;
}

function ProjectsCard({ projects, loading }: ProjectsCardProps) {
    return (
        <div className="flex flex-col bg-card-light dark:bg-card-dark rounded-xl border border-border-light dark:border-gray-800 shadow-sm h-full">
            <div className="p-6 border-b border-border-light dark:border-gray-800 flex items-center justify-between">
                <div className="flex items-center gap-3">
                    <span className="material-symbols-outlined text-[24px] text-gray-800 dark:text-white">
                        folder_open
                    </span>
                    <h2 className="text-lg font-bold text-gray-900 dark:text-white">Projects</h2>
                </div>
                <span className="text-xs font-medium text-gray-500 dark:text-gray-400">
                    {projects.length} total
                </span>
            </div>

            <div className="flex-1 p-0">
                {loading ? (
                    <div className="flex items-center justify-center py-12">
                        <div className="w-6 h-6 border-2 border-primary border-t-transparent rounded-full animate-spin" />
                    </div>
                ) : projects.length === 0 ? (
                    <div className="flex flex-col items-center justify-center py-12 text-gray-400 dark:text-gray-500">
                        <span className="material-symbols-outlined text-[32px] mb-2">inbox</span>
                        <p className="text-sm">No projects yet</p>
                    </div>
                ) : (
                    <ul className="flex flex-col">
                        {projects.slice(0, 4).map((project, i) => {
                            const taskCount = project.tasks?.length ?? 0;
                            const doneCount = project.tasks?.filter((t) => t.state === 'DONE').length ?? 0;
                            return (
                                <li
                                    key={project.projectId}
                                    className={`group flex items-start gap-4 p-4 hover:bg-gray-50 dark:hover:bg-white/5 transition-colors ${
                                        i < Math.min(projects.length, 4) - 1
                                            ? 'border-b border-border-light dark:border-gray-800/50'
                                            : ''
                                    }`}
                                >
                                    <div className="mt-1 text-gray-400 dark:text-gray-500">
                                        <span className="material-symbols-outlined text-[20px]">code_blocks</span>
                                    </div>
                                    <div className="flex-1 min-w-0">
                                        <div className="flex items-center justify-between mb-1">
                                            <p className="text-sm font-semibold text-gray-900 dark:text-gray-100 truncate pr-2">
                                                {project.projectName}
                                            </p>
                                            {taskCount > 0 && (
                                                <span className="text-xs font-mono text-gray-500 bg-gray-100 dark:bg-[#2d333b] px-2 py-0.5 rounded border border-gray-200 dark:border-gray-700">
                                                    {doneCount}/{taskCount}
                                                </span>
                                            )}
                                        </div>
                                        <div className="flex items-center gap-3 text-xs text-gray-500 dark:text-gray-400">
                                            {project.description && (
                                                <span className="truncate">{project.description}</span>
                                            )}
                                            {project.githubLink && (
                                                <>
                                                    <span>&bull;</span>
                                                    <a
                                                        href={project.githubLink}
                                                        target="_blank"
                                                        rel="noreferrer"
                                                        className="flex items-center gap-1 hover:text-primary transition-colors"
                                                    >
                                                        <span className="material-symbols-outlined text-[14px]">link</span>
                                                        GitHub
                                                    </a>
                                                </>
                                            )}
                                        </div>
                                    </div>
                                </li>
                            );
                        })}
                    </ul>
                )}
            </div>

            <div className="p-4 mt-auto border-t border-border-light dark:border-gray-800 bg-gray-50/50 dark:bg-[#2d333b]/30 rounded-b-xl">
                <div className="flex justify-around text-sm">
                    <div className="flex items-center gap-2 text-gray-600 dark:text-gray-400">
                        <span className="material-symbols-outlined text-[18px]">folder_open</span>
                        <span className="font-medium">{projects.length} Projects</span>
                    </div>
                    <div className="flex items-center gap-2 text-gray-600 dark:text-gray-400">
                        <span className="material-symbols-outlined text-[18px]">task_alt</span>
                        <span className="font-medium">
                            {projects.reduce((sum, p) => sum + (p.tasks?.length ?? 0), 0)} Tasks
                        </span>
                    </div>
                </div>
            </div>
        </div>
    );
}

interface DailyFocusProps {
    doneTasks: number;
    totalTasks: number;
    habits: HabitResponseDto[];
    loading: boolean;
}

function DailyFocusCard({ doneTasks, totalTasks, habits, loading }: DailyFocusProps) {
    const pct = totalTasks > 0 ? Math.round((doneTasks / totalTasks) * 100) : 0;
    const circumference = 2 * Math.PI * 40;
    const offset = circumference - (pct / 100) * circumference;

    const statusLabel = pct >= 75 ? 'On Track' : pct >= 40 ? 'In Progress' : totalTasks === 0 ? 'No Tasks' : 'Getting Started';
    const statusColor =
        pct >= 75
            ? 'bg-emerald-100 dark:bg-emerald-900/30 text-emerald-700 dark:text-emerald-400'
            : pct >= 40
              ? 'bg-amber-100 dark:bg-amber-900/30 text-amber-700 dark:text-amber-400'
              : 'bg-gray-100 dark:bg-gray-800 text-gray-600 dark:text-gray-400';

    return (
        <div className="flex flex-col bg-card-light dark:bg-card-dark rounded-xl border border-border-light dark:border-gray-800 shadow-sm h-full relative overflow-hidden">
            <div className="p-6 pb-0 flex items-center justify-between z-10">
                <div className="flex items-center gap-3">
                    <span className="material-symbols-outlined text-[24px] text-gray-800 dark:text-white">
                        target
                    </span>
                    <h2 className="text-lg font-bold text-gray-900 dark:text-white">Daily Focus</h2>
                </div>
                <span className={`text-xs font-bold px-2 py-1 rounded uppercase tracking-wide ${statusColor}`}>
                    {statusLabel}
                </span>
            </div>

            <div className="flex-1 p-6 flex items-center justify-between gap-6 z-10">
                {loading ? (
                    <div className="flex items-center justify-center w-full py-8">
                        <div className="w-6 h-6 border-2 border-primary border-t-transparent rounded-full animate-spin" />
                    </div>
                ) : (
                    <>
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
                                    strokeDasharray={circumference}
                                    strokeDashoffset={offset}
                                    strokeLinecap="round"
                                />
                            </svg>
                            <div className="absolute inset-0 flex flex-col items-center justify-center">
                                <span className="text-3xl font-bold text-gray-900 dark:text-white">
                                    {pct}<span className="text-sm align-top">%</span>
                                </span>
                                <span className="text-[10px] uppercase text-gray-500 font-semibold tracking-wider">
                                    Done
                                </span>
                            </div>
                        </div>

                        <div className="flex-1 flex flex-col justify-center gap-4">
                            <div>
                                <p className="text-sm font-medium text-gray-500 dark:text-gray-400 mb-1">
                                    Active Habits
                                </p>
                                <div className="flex items-center gap-2">
                                    <h3 className="text-2xl font-mono font-medium text-gray-900 dark:text-white">
                                        {habits.length}
                                    </h3>
                                    {habits.length > 0 && (
                                        <span className="size-2 rounded-full bg-emerald-500 animate-pulse" />
                                    )}
                                </div>
                            </div>
                            <div className="flex flex-col gap-2">
                                <div className="flex items-center justify-between text-sm">
                                    <span className="text-gray-600 dark:text-gray-400">Tasks Completed</span>
                                    <span className="font-bold text-gray-900 dark:text-white">
                                        {doneTasks} / {totalTasks}
                                    </span>
                                </div>
                                <div className="h-1.5 w-full bg-gray-100 dark:bg-gray-700 rounded-full overflow-hidden">
                                    <div
                                        className="h-full bg-emerald-500 rounded-full transition-all duration-500"
                                        style={{ width: `${pct}%` }}
                                    />
                                </div>
                            </div>
                        </div>
                    </>
                )}
            </div>

            <div className="p-6 pt-0 mt-auto z-10">
                <button className="w-full py-3 px-4 bg-primary hover:bg-sky-600 text-white font-semibold rounded-lg shadow-lg shadow-primary/20 transition-all flex items-center justify-center gap-2">
                    <span className="material-symbols-outlined text-[20px]">play_arrow</span>
                    Start Focus Session
                </button>
            </div>

            <div className="absolute top-0 right-0 w-32 h-32 bg-primary/5 rounded-bl-full pointer-events-none z-0" />
        </div>
    );
}
