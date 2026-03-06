import React, { useEffect, useState, useCallback } from 'react';
import { useParams, Link } from 'react-router-dom';
import { fetchProject, updateProject, createTask, updateTask, deleteTask } from '../api/endpoints';
import type { ProjectResponseDto, ProjectState, TaskResponseDto, TaskState, TaskImportance, TaskRequestDto } from '../api/types';
import { TaskModal } from '../components/TaskModal';
import { EditProjectModal } from '../components/EditProjectModal';
import { logActivity } from '../api/activityTracker';

const STATE_LABELS: Record<TaskState, string> = {
    PENDING: 'Todo',
    IN_PROGRESS: 'In Progress',
    FINISHED: 'Done',
};

const STATE_ORDER: TaskState[] = ['PENDING', 'IN_PROGRESS', 'FINISHED'];

const IMPORTANCE_COLORS: Record<TaskImportance, string> = {
    NORMAL: 'text-slate-500 bg-slate-500/10',
    MEDIUM: 'text-amber-500 bg-amber-500/10',
    IMPORTANT: 'text-primary bg-primary/10',
    CRUCIAL: 'text-red-500 bg-red-500/10',
};

const PROJECT_STATE_STYLES: Record<string, string> = {
    PENDING: 'bg-slate-500/20 text-slate-600 dark:text-slate-400 border-slate-500/30',
    IN_PROGRESS: 'bg-primary/20 text-primary border-primary/30',
    FINISHED: 'bg-emerald-500/20 text-emerald-600 dark:text-emerald-400 border-emerald-500/30',
};

const PROJECT_STATE_LABELS: Record<string, string> = {
    PENDING: 'Pending',
    IN_PROGRESS: 'In Progress',
    FINISHED: 'Finished',
};

function formatDuration(minutes: number): string {
    if (minutes <= 0) return '';
    const h = Math.floor(minutes / 60);
    const m = minutes % 60;
    if (h === 0) return `${m}m`;
    if (m === 0) return `${h}.0h`;
    return `${h}.${Math.round((m / 60) * 10)}h`;
}

export function ProjectDetails() {
    const { id } = useParams<{ id: string }>();
    const [project, setProject] = useState<ProjectResponseDto | null>(null);
    const [loading, setLoading] = useState(true);
    const [taskModalOpen, setTaskModalOpen] = useState(false);
    const [editingTask, setEditingTask] = useState<TaskResponseDto | null>(null);
    const [editProjectOpen, setEditProjectOpen] = useState(false);

    const loadProject = useCallback(async () => {
        if (!id) return;
        try {
            const p = await fetchProject(Number(id));
            setProject(p);
        } catch {
        } finally {
            setLoading(false);
        }
    }, [id]);

    useEffect(() => {
        loadProject();
    }, [loadProject]);

    const handleCreateTask = async (data: TaskRequestDto) => {
        await createTask(data);
        logActivity('task_created');
        await loadProject();
    };

    const handleUpdateTask = async (taskId: number, data: Partial<TaskRequestDto>) => {
        const oldTask = project?.tasks?.find((t) => t.id === taskId);
        await updateTask(taskId, data);
        if (data.state === 'FINISHED' && oldTask?.state !== 'FINISHED') {
            logActivity('task_completed');
        } else {
            logActivity('task_updated');
        }
        await loadProject();
    };

    const handleDeleteTask = async (taskId: number) => {
        await deleteTask(taskId);
        logActivity('task_deleted');
        await loadProject();
    };

    const handleUpdateProject = async (data: { projectName: string; description: string; githubLink: string; state?: ProjectState }) => {
        if (!project) return;
        await updateProject(project.projectId, data);
        logActivity('project_updated');
        await loadProject();
    };

    const openNewTask = () => {
        setEditingTask(null);
        setTaskModalOpen(true);
    };

    const openEditTask = (task: TaskResponseDto) => {
        setEditingTask(task);
        setTaskModalOpen(true);
    };

    if (loading) {
        return (
            <div className="min-h-screen flex items-center justify-center bg-background-light dark:bg-background-dark">
                <div className="w-8 h-8 border-4 border-primary border-t-transparent rounded-full animate-spin" />
            </div>
        );
    }

    if (!project) {
        return (
            <div className="min-h-screen flex flex-col items-center justify-center bg-background-light dark:bg-background-dark gap-4">
                <span className="material-symbols-outlined text-[48px] text-slate-400">folder_off</span>
                <p className="text-slate-500 text-lg">Project not found</p>
                <Link to="/dashboard" className="text-primary font-semibold hover:underline">Back to Dashboard</Link>
            </div>
        );
    }

    const tasks = project.tasks ?? [];
    const tasksByState: Record<TaskState, TaskResponseDto[]> = {
        PENDING: tasks.filter((t) => t.state === 'PENDING'),
        IN_PROGRESS: tasks.filter((t) => t.state === 'IN_PROGRESS'),
        FINISHED: tasks.filter((t) => t.state === 'FINISHED'),
    };

    const totalTasks = tasks.length;
    const doneTasks = tasksByState.FINISHED.length;
    const pct = totalTasks > 0 ? Math.round((doneTasks / totalTasks) * 100) : 0;
    const totalDuration = tasks.reduce((sum, t) => sum + (t.duration ?? 0), 0);
    const loggedDuration = tasksByState.FINISHED.reduce((sum, t) => sum + (t.duration ?? 0), 0);

    const columnBadgeStyles: Record<TaskState, string> = {
        PENDING: 'bg-slate-200 dark:bg-slate-800 text-slate-600 dark:text-slate-400',
        IN_PROGRESS: 'bg-primary/20 text-primary',
        FINISHED: 'bg-emerald-500/20 text-emerald-500',
    };

    return (
        <div className="relative flex min-h-screen w-full flex-col overflow-x-hidden bg-background-light dark:bg-background-dark font-display text-slate-900 dark:text-slate-100">
            <header className="flex items-center justify-between whitespace-nowrap border-b border-solid border-slate-200 dark:border-slate-800 px-6 py-3 bg-background-light dark:bg-background-dark sticky top-0 z-50">
                <div className="flex items-center gap-8">
                    <Link to="/dashboard" className="flex items-center gap-4 text-primary">
                        <div className="size-8 flex items-center justify-center rounded-lg bg-primary/10">
                            <span className="material-symbols-outlined text-primary">terminal</span>
                        </div>
                        <h2 className="text-slate-900 dark:text-slate-100 text-lg font-bold leading-tight tracking-tight">DevHub</h2>
                    </Link>
                    <nav className="hidden md:flex items-center gap-6">
                        <Link className="text-primary text-sm font-semibold leading-normal" to="/dashboard">Projects</Link>
                        <Link className="text-slate-600 dark:text-slate-400 text-sm font-medium leading-normal hover:text-primary transition-colors" to="/dashboard">Dashboard</Link>
                    </nav>
                </div>
            </header>

            <main className="flex-1 max-w-7xl mx-auto w-full px-6 py-8 flex flex-col gap-8">
                <section className="flex flex-col gap-4">
                    <nav className="flex items-center gap-2 text-sm text-slate-500 dark:text-slate-400">
                        <Link className="hover:text-primary transition-colors" to="/dashboard">Projects</Link>
                        <span className="material-symbols-outlined text-xs">chevron_right</span>
                        <span className="text-slate-900 dark:text-slate-100 font-medium">{project.projectName}</span>
                    </nav>

                    <div className="flex flex-col md:flex-row md:items-end justify-between gap-6">
                        <div className="flex flex-col gap-3 max-w-2xl">
                            <div className="flex items-center gap-3">
                                <h1 className="text-slate-900 dark:text-slate-100 text-4xl font-extrabold leading-tight tracking-tight">
                                    {project.projectName}
                                </h1>
                                {project.state && (
                                    <span className={`px-2.5 py-0.5 rounded-full text-xs font-bold border flex items-center gap-1 ${PROJECT_STATE_STYLES[project.state] ?? ''}`}>
                                        {project.state === 'IN_PROGRESS' && (
                                            <span className="size-1.5 rounded-full bg-primary animate-pulse" />
                                        )}
                                        {PROJECT_STATE_LABELS[project.state] ?? project.state}
                                    </span>
                                )}
                            </div>
                            {project.description && (
                                <p className="text-slate-600 dark:text-slate-400 text-lg leading-relaxed">
                                    {project.description}
                                </p>
                            )}
                        </div>

                        <div className="flex items-center gap-3">
                            <button
                                onClick={() => setEditProjectOpen(true)}
                                className="flex items-center gap-2 px-4 py-2.5 rounded-lg border border-slate-200 dark:border-slate-800 font-semibold text-sm hover:bg-slate-50 dark:hover:bg-slate-800 transition-colors"
                            >
                                <span className="material-symbols-outlined text-lg">edit</span>
                                Edit Project
                            </button>
                            {project.githubLink && (
                                <a
                                    href={project.githubLink}
                                    target="_blank"
                                    rel="noreferrer"
                                    className="flex items-center gap-2 px-4 py-2.5 rounded-lg border border-slate-200 dark:border-slate-800 font-semibold text-sm hover:bg-slate-50 dark:hover:bg-slate-800 transition-colors"
                                >
                                    <span className="material-symbols-outlined text-lg">link</span>
                                    GitHub Repo
                                </a>
                            )}
                            <button
                                onClick={openNewTask}
                                className="flex items-center gap-2 px-4 py-2.5 rounded-lg bg-primary text-white font-semibold text-sm hover:bg-primary/90 transition-all shadow-lg shadow-primary/20"
                            >
                                <span className="material-symbols-outlined text-lg">add</span>
                                Add Task
                            </button>
                        </div>
                    </div>
                </section>

                <section className="grid grid-cols-1 lg:grid-cols-3 gap-6">
                    {STATE_ORDER.map((state) => {
                        const columnTasks = tasksByState[state];
                        const isFinished = state === 'FINISHED';

                        return (
                            <div
                                key={state}
                                className="flex flex-col gap-4 bg-slate-50/50 dark:bg-slate-900/30 p-4 rounded-xl border border-dashed border-slate-200 dark:border-slate-800"
                            >
                                <div className="flex items-center justify-between px-2">
                                    <div className="flex items-center gap-2">
                                        <span className="text-xs font-bold uppercase tracking-widest text-slate-400">
                                            {STATE_LABELS[state]}
                                        </span>
                                        <span className={`text-[10px] px-1.5 py-0.5 rounded-md ${columnBadgeStyles[state]}`}>
                                            {columnTasks.length}
                                        </span>
                                    </div>
                                </div>

                                <div className={`flex flex-col gap-3 ${isFinished ? 'opacity-60' : ''}`}>
                                    {columnTasks.length === 0 && (
                                        <div className="text-center py-8 text-slate-400 dark:text-slate-600 text-sm">
                                            {state === 'PENDING' ? (
                                                <button onClick={openNewTask} className="flex flex-col items-center gap-2 w-full hover:text-primary transition-colors">
                                                    <span className="material-symbols-outlined text-2xl">add_circle</span>
                                                    <span>Add your first task</span>
                                                </button>
                                            ) : (
                                                'No tasks'
                                            )}
                                        </div>
                                    )}
                                    {columnTasks.map((task) => (
                                        <TaskCard
                                            key={task.id}
                                            task={task}
                                            isFinished={isFinished}
                                            onEdit={() => openEditTask(task)}
                                            onDelete={() => handleDeleteTask(task.id)}
                                            onStateChange={(newState) =>
                                                handleUpdateTask(task.id, { ...task, state: newState })
                                            }
                                        />
                                    ))}
                                </div>
                            </div>
                        );
                    })}
                </section>

                <section className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
                    <StatCard
                        label="Total Progress"
                        value={`${pct}%`}
                        detail={`${doneTasks} of ${totalTasks} tasks done`}
                        barPct={pct}
                    />
                    <StatCard
                        label="Total Tasks"
                        value={String(totalTasks)}
                        detail={`${tasksByState.IN_PROGRESS.length} in progress`}
                        icon="task_alt"
                        iconColor="text-primary"
                    />
                    <StatCard
                        label="Estimated Effort"
                        value={formatDuration(totalDuration)}
                        detail={`${formatDuration(loggedDuration)} completed`}
                        icon="timer"
                        iconColor="text-primary"
                    />
                    <StatCard
                        label="Sessions"
                        value={String(project.sessions ?? 0)}
                        detail="focus sessions logged"
                        icon="bolt"
                        iconColor="text-emerald-500"
                    />
                </section>
            </main>

            {taskModalOpen && (
                <TaskModal
                    projectId={project.projectId}
                    task={editingTask}
                    defaultState={'PENDING'}
                    onSave={async (data: TaskRequestDto) => {
                        if (editingTask) {
                            await handleUpdateTask(editingTask.id, data);
                        } else {
                            await handleCreateTask(data);
                        }
                        setTaskModalOpen(false);
                    }}
                    onClose={() => setTaskModalOpen(false)}
                />
            )}

            {editProjectOpen && project && (
                <EditProjectModal
                    project={project}
                    onSave={async (data: { projectName: string; description: string; githubLink: string; state?: ProjectState }) => {
                        await handleUpdateProject(data);
                        setEditProjectOpen(false);
                    }}
                    onClose={() => setEditProjectOpen(false)}
                />
            )}
        </div>
    );
}

interface TaskCardProps {
    task: TaskResponseDto;
    isFinished: boolean;
    onEdit: () => void;
    onDelete: () => void;
    onStateChange: (state: TaskState) => void;
}

function TaskCard({ task, isFinished, onEdit, onDelete, onStateChange }: TaskCardProps) {
    const [menuOpen, setMenuOpen] = useState(false);

    const borderClass = task.state === 'IN_PROGRESS' ? 'border-l-4 border-l-primary' : '';
    const importanceLabel = task.importance ?? 'NORMAL';

    return (
        <div
            className={`bg-white dark:bg-slate-800 p-4 rounded-xl border border-slate-200 dark:border-slate-800 shadow-sm hover:border-primary/50 transition-colors group relative ${borderClass} ${isFinished ? 'grayscale' : ''}`}
        >
            <div className="flex justify-between items-start mb-2">
                <span className={`text-[10px] font-bold uppercase tracking-tight px-2 py-0.5 rounded ${IMPORTANCE_COLORS[importanceLabel]}`}>
                    {importanceLabel}
                </span>
                <div className="relative">
                    <button
                        onClick={() => setMenuOpen(!menuOpen)}
                        className="text-slate-400 hover:text-primary transition-colors opacity-0 group-hover:opacity-100"
                    >
                        <span className="material-symbols-outlined text-lg">more_vert</span>
                    </button>
                    {menuOpen && (
                        <>
                            <div className="fixed inset-0 z-10" onClick={() => setMenuOpen(false)} />
                            <div className="absolute right-0 top-6 z-20 bg-white dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-lg shadow-xl py-1 min-w-[140px]">
                                <button
                                    onClick={() => { onEdit(); setMenuOpen(false); }}
                                    className="w-full px-3 py-2 text-left text-sm hover:bg-slate-50 dark:hover:bg-slate-700 flex items-center gap-2"
                                >
                                    <span className="material-symbols-outlined text-sm">edit</span>
                                    Edit
                                </button>
                                {task.state !== 'IN_PROGRESS' && (
                                    <button
                                        onClick={() => { onStateChange('IN_PROGRESS'); setMenuOpen(false); }}
                                        className="w-full px-3 py-2 text-left text-sm hover:bg-slate-50 dark:hover:bg-slate-700 flex items-center gap-2 text-primary"
                                    >
                                        <span className="material-symbols-outlined text-sm">play_arrow</span>
                                        Start
                                    </button>
                                )}
                                {task.state !== 'FINISHED' && (
                                    <button
                                        onClick={() => { onStateChange('FINISHED'); setMenuOpen(false); }}
                                        className="w-full px-3 py-2 text-left text-sm hover:bg-slate-50 dark:hover:bg-slate-700 flex items-center gap-2 text-emerald-500"
                                    >
                                        <span className="material-symbols-outlined text-sm">check_circle</span>
                                        Complete
                                    </button>
                                )}
                                {task.state !== 'PENDING' && (
                                    <button
                                        onClick={() => { onStateChange('PENDING'); setMenuOpen(false); }}
                                        className="w-full px-3 py-2 text-left text-sm hover:bg-slate-50 dark:hover:bg-slate-700 flex items-center gap-2"
                                    >
                                        <span className="material-symbols-outlined text-sm">undo</span>
                                        Move to Todo
                                    </button>
                                )}
                                <hr className="my-1 border-slate-200 dark:border-slate-700" />
                                <button
                                    onClick={() => { onDelete(); setMenuOpen(false); }}
                                    className="w-full px-3 py-2 text-left text-sm hover:bg-red-50 dark:hover:bg-red-900/20 flex items-center gap-2 text-red-500"
                                >
                                    <span className="material-symbols-outlined text-sm">delete</span>
                                    Delete
                                </button>
                            </div>
                        </>
                    )}
                </div>
            </div>

            <h4 className={`font-bold text-sm mb-3 group-hover:text-primary transition-colors cursor-pointer ${isFinished ? 'line-through text-slate-500' : ''}`} onClick={onEdit}>
                {task.taskName}
            </h4>

            {task.description && !isFinished && (
                <p className="text-xs text-slate-500 dark:text-slate-400 mb-3 line-clamp-2">{task.description}</p>
            )}

            <div className="flex items-center justify-between text-xs text-slate-500 dark:text-slate-400">
                {isFinished ? (
                    <div className="flex items-center gap-1 text-emerald-500">
                        <span className="material-symbols-outlined text-sm">check_circle</span>
                        <span>Completed</span>
                    </div>
                ) : task.state === 'IN_PROGRESS' ? (
                    <div className="flex items-center gap-1 text-primary font-bold">
                        <span className="material-symbols-outlined text-sm">autorenew</span>
                        <span>{formatDuration(task.duration)}</span>
                    </div>
                ) : (
                    <div className="flex items-center gap-1">
                        <span className="material-symbols-outlined text-sm">schedule</span>
                        <span>{formatDuration(task.duration)}</span>
                    </div>
                )}
            </div>
        </div>
    );
}

interface StatCardProps {
    label: string;
    value: string;
    detail: string;
    barPct?: number;
    icon?: string;
    iconColor?: string;
}

function StatCard({ label, value, detail, barPct, icon, iconColor }: StatCardProps) {
    return (
        <div className="bg-white dark:bg-slate-800 p-5 rounded-xl border border-slate-200 dark:border-slate-800 flex flex-col gap-2">
            <span className="text-xs font-semibold text-slate-500 dark:text-slate-400">{label}</span>
            <div className="flex items-end gap-2">
                <span className="text-2xl font-bold">{value || '—'}</span>
                <span className="text-slate-500 text-xs font-medium mb-1">{detail}</span>
            </div>
            {barPct !== undefined && (
                <div className="w-full bg-slate-100 dark:bg-slate-700 h-1.5 rounded-full mt-2 overflow-hidden">
                    <div className="bg-primary h-full rounded-full transition-all duration-500" style={{ width: `${barPct}%` }} />
                </div>
            )}
            {icon && (
                <div className="flex items-center gap-1 mt-2">
                    <span className={`material-symbols-outlined text-xs ${iconColor ?? 'text-slate-400'}`}>{icon}</span>
                </div>
            )}
        </div>
    );
}
