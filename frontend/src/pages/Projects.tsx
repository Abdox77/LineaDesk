import React, { useEffect, useState, useCallback } from 'react';
import { Link } from 'react-router-dom';
import { Sidebar } from '../components/Sidebar';
import { useAuth } from '../auth/AuthContext';
import { fetchProjects, createProject, deleteProject } from '../api/endpoints';
import type { ProjectResponseDto, ProjectRequestDto, ProjectState } from '../api/types';
import { CreateProjectModal } from '../components/CreateProjectModal';
import { logActivity } from '../api/activityTracker';
import { useToast } from '../components/ToastProvider';

const STATE_STYLES: Record<ProjectState, string> = {
    PENDING: 'bg-slate-500/15 text-slate-600 dark:text-slate-400 border-slate-500/30',
    IN_PROGRESS: 'bg-primary/15 text-primary border-primary/30',
    FINISHED: 'bg-emerald-500/15 text-emerald-600 dark:text-emerald-400 border-emerald-500/30',
};

const STATE_LABELS: Record<ProjectState, string> = {
    PENDING: 'Pending',
    IN_PROGRESS: 'In Progress',
    FINISHED: 'Finished',
};

export function Projects() {
    const { user } = useAuth();
    const { showToast } = useToast();
    const displayName = user?.username ?? 'Developer';

    const [projects, setProjects] = useState<ProjectResponseDto[]>([]);
    const [loading, setLoading] = useState(true);
    const [createModalOpen, setCreateModalOpen] = useState(false);
    const [filter, setFilter] = useState<ProjectState | 'ALL'>('ALL');

    const loadProjects = useCallback(async () => {
        try {
            const p = await fetchProjects();
            setProjects(p);
        } catch {
        } finally {
            setLoading(false);
        }
    }, []);

    useEffect(() => {
        loadProjects();
    }, [loadProjects]);

    const handleDelete = async (id: number) => {
        await deleteProject(id);
        logActivity('project_deleted');
        showToast('Project deleted', 'warning');
        await loadProjects();
    };

    const filtered = filter === 'ALL' ? projects : projects.filter((p) => p.state === filter);

    const counts = {
        ALL: projects.length,
        PENDING: projects.filter((p) => p.state === 'PENDING').length,
        IN_PROGRESS: projects.filter((p) => p.state === 'IN_PROGRESS').length,
        FINISHED: projects.filter((p) => p.state === 'FINISHED').length,
    };

    return (
        <div className="flex h-screen w-full bg-background-light dark:bg-background-dark font-display text-[#111618] dark:text-gray-200 overflow-hidden">
            <Sidebar displayName={`${displayName}'s Space`} />

            <main className="flex-1 h-full overflow-y-auto overflow-x-hidden relative">
                <div className="max-w-5xl mx-auto px-6 py-8 md:px-10 lg:py-12 flex flex-col gap-8">
                    <header className="flex flex-col md:flex-row md:items-end justify-between gap-4">
                        <div>
                            <h1 className="text-3xl font-bold text-gray-900 dark:text-white tracking-tight leading-tight">
                                Projects
                            </h1>
                            <p className="text-gray-500 dark:text-gray-400 mt-1">
                                {projects.length} project{projects.length !== 1 ? 's' : ''} total
                            </p>
                        </div>
                        <button
                            onClick={() => setCreateModalOpen(true)}
                            className="flex items-center gap-2 px-4 py-2.5 bg-primary text-white rounded-lg text-sm font-semibold shadow-md hover:bg-sky-600 transition-all"
                        >
                            <span className="material-symbols-outlined text-[18px]">add</span>
                            New Project
                        </button>
                    </header>

                    <div className="flex items-center gap-2">
                        {(['ALL', 'PENDING', 'IN_PROGRESS', 'FINISHED'] as const).map((key) => (
                            <button
                                key={key}
                                onClick={() => setFilter(key)}
                                className={`px-3 py-1.5 rounded-lg text-xs font-bold transition-all ${
                                    filter === key
                                        ? 'bg-primary/10 text-primary'
                                        : 'text-gray-500 dark:text-gray-400 hover:bg-gray-100 dark:hover:bg-white/5'
                                }`}
                            >
                                {key === 'ALL' ? 'All' : STATE_LABELS[key]}
                                <span className="ml-1.5 text-[10px] opacity-70">{counts[key]}</span>
                            </button>
                        ))}
                    </div>

                    {loading ? (
                        <div className="flex items-center justify-center py-20">
                            <div className="w-7 h-7 border-2 border-primary border-t-transparent rounded-full animate-spin" />
                        </div>
                    ) : filtered.length === 0 ? (
                        <div className="flex flex-col items-center justify-center py-20 text-gray-400 dark:text-gray-500">
                            <span className="material-symbols-outlined text-[48px] mb-3">inbox</span>
                            <p className="text-base font-medium">
                                {filter === 'ALL' ? 'No projects yet' : `No ${STATE_LABELS[filter].toLowerCase()} projects`}
                            </p>
                            {filter === 'ALL' && (
                                <button
                                    onClick={() => setCreateModalOpen(true)}
                                    className="mt-4 text-primary font-semibold text-sm hover:underline"
                                >
                                    Create your first project
                                </button>
                            )}
                        </div>
                    ) : (
                        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                            {filtered.map((project) => {
                                const taskCount = project.tasks?.length ?? 0;
                                const doneCount = project.tasks?.filter((t) => t.state === 'FINISHED').length ?? 0;
                                const pct = taskCount > 0 ? Math.round((doneCount / taskCount) * 100) : 0;

                                return (
                                    <Link
                                        key={project.projectId}
                                        to={`/project/${project.projectId}`}
                                        className="group flex flex-col bg-card-light dark:bg-card-dark rounded-xl border border-border-light dark:border-gray-800 shadow-sm hover:border-primary/50 transition-all p-5 gap-3"
                                    >
                                        <div className="flex items-start justify-between">
                                            <div className="flex items-center gap-3 min-w-0">
                                                <div className="size-9 rounded-lg bg-primary/10 flex items-center justify-center flex-shrink-0">
                                                    <span className="material-symbols-outlined text-primary text-[18px]">code_blocks</span>
                                                </div>
                                                <div className="min-w-0">
                                                    <h3 className="text-sm font-bold text-gray-900 dark:text-white truncate group-hover:text-primary transition-colors">
                                                        {project.projectName}
                                                    </h3>
                                                    {project.description && (
                                                        <p className="text-xs text-gray-500 dark:text-gray-400 truncate mt-0.5">
                                                            {project.description}
                                                        </p>
                                                    )}
                                                </div>
                                            </div>
                                            {project.state && (
                                                <span className={`px-2 py-0.5 rounded-full text-[10px] font-bold border flex-shrink-0 ${STATE_STYLES[project.state]}`}>
                                                    {STATE_LABELS[project.state]}
                                                </span>
                                            )}
                                        </div>

                                        <div className="flex items-center justify-between text-xs text-gray-500 dark:text-gray-400">
                                            <div className="flex items-center gap-3">
                                                <span className="flex items-center gap-1">
                                                    <span className="material-symbols-outlined text-[14px]">task_alt</span>
                                                    {doneCount}/{taskCount} tasks
                                                </span>
                                                {project.githubLink && (
                                                    <span className="flex items-center gap-1">
                                                        <span className="material-symbols-outlined text-[14px]">link</span>
                                                        GitHub
                                                    </span>
                                                )}
                                            </div>
                                            <span className="font-mono text-[10px]">{pct}%</span>
                                        </div>

                                        {taskCount > 0 && (
                                            <div className="w-full bg-gray-100 dark:bg-gray-700 h-1 rounded-full overflow-hidden">
                                                <div
                                                    className="bg-primary h-full rounded-full transition-all duration-500"
                                                    style={{ width: `${pct}%` }}
                                                />
                                            </div>
                                        )}
                                    </Link>
                                );
                            })}
                        </div>
                    )}
                </div>
            </main>

            {createModalOpen && (
                <CreateProjectModal
                    onSave={async (data: ProjectRequestDto) => {
                        await createProject(data);
                        logActivity('project_created');
                        showToast('Project created successfully');
                        setCreateModalOpen(false);
                        await loadProjects();
                    }}
                    onClose={() => setCreateModalOpen(false)}
                />
            )}
        </div>
    );
}
