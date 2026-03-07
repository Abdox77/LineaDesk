import React, { useEffect, useState, useCallback } from 'react';
import { useParams, Link, useNavigate } from 'react-router-dom';
import { Sidebar } from '../components/Sidebar';
import { useAuth } from '../auth/AuthContext';
import { fetchProject, updateProject, createTask, updateTask, deleteTask } from '../api/endpoints';
import type { ProjectResponseDto, ProjectState, TaskResponseDto, TaskState, TaskRequestDto } from '../api/types';
import { TaskModal } from '../components/TaskModal';
import { EditProjectModal } from '../components/EditProjectModal';
import { logActivity } from '../api/activityTracker';

/* ── Helpers ── */

const PROJECT_STATE_LABELS: Record<string, string> = {
    PENDING: 'Pending',
    IN_PROGRESS: 'Active',
    FINISHED: 'Finished',
};

const PROJECT_STATE_STYLES: Record<string, string> = {
    PENDING: 'bg-gray-100 dark:bg-surface-dark-alt text-gray-700 dark:text-gray-300 border-gray-200 dark:border-border-dark',
    IN_PROGRESS: 'bg-green-100 dark:bg-green-900/20 text-green-700 dark:text-green-400 border-green-200 dark:border-green-800/50',
    FINISHED: 'bg-emerald-100 dark:bg-emerald-900/20 text-emerald-700 dark:text-emerald-400 border-emerald-200 dark:border-emerald-800/50',
};

function formatDuration(minutes: number): string {
    if (minutes <= 0) return '—';
    const h = Math.floor(minutes / 60);
    const m = minutes % 60;
    if (h === 0) return `${m}m`;
    if (m === 0) return `${h}h`;
    return `${h}h ${m}m`;
}

function formatDurationLong(minutes: number): string {
    if (minutes <= 0) return '0m';
    const h = Math.floor(minutes / 60);
    const m = minutes % 60;
    if (h === 0) return `${m}m`;
    if (m === 0) return `${h}h 0m`;
    return `${h}h ${m}m`;
}

/* ── Status Badge ── */

function StatusBadge({ state }: { state: TaskState }) {
    if (state === 'IN_PROGRESS') {
        return (
            <span className="inline-flex items-center gap-1.5 px-2.5 py-1 rounded-full text-xs font-medium bg-blue-50 text-blue-700 border border-blue-100 dark:bg-blue-900/20 dark:text-blue-400 dark:border-blue-800/30">
                <span className="w-1.5 h-1.5 rounded-full bg-blue-500 dark:bg-blue-400 animate-pulse" />
                In Progress
            </span>
        );
    }
    if (state === 'FINISHED') {
        return (
            <span className="inline-flex items-center gap-1.5 px-2.5 py-1 rounded-full text-xs font-medium bg-emerald-50 text-emerald-700 border border-emerald-100 dark:bg-emerald-900/20 dark:text-emerald-400 dark:border-emerald-800/30">
                <span className="material-symbols-outlined text-[14px]">check_circle</span>
                Done
            </span>
        );
    }
    return (
        <span className="inline-flex items-center px-2.5 py-1 rounded-full text-xs font-medium bg-gray-100 text-gray-700 border border-gray-200 dark:bg-surface-dark-alt dark:text-gray-300 dark:border-border-dark">
            Todo
        </span>
    );
}

/* ── Main Component ── */

export function ProjectDetails() {
    const { id } = useParams<{ id: string }>();
    const navigate = useNavigate();
    const { user } = useAuth();
    const displayName = user?.username ?? 'Developer';

    const [project, setProject] = useState<ProjectResponseDto | null>(null);
    const [loading, setLoading] = useState(true);
    const [taskModalOpen, setTaskModalOpen] = useState(false);
    const [editingTask, setEditingTask] = useState<TaskResponseDto | null>(null);
    const [editProjectOpen, setEditProjectOpen] = useState(false);
    const [selectedTasks, setSelectedTasks] = useState<Set<number>>(new Set());
    const [newTaskName, setNewTaskName] = useState('');
    const [menuOpenId, setMenuOpenId] = useState<number | null>(null);

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

    const handleQuickCreateTask = async () => {
        if (!project || newTaskName.trim().length < 3) return;
        await createTask({
            taskName: newTaskName.trim(),
            projectId: project.projectId,
            state: 'PENDING',
            importance: 'NORMAL',
            duration: 0,
        });
        logActivity('task_created');
        setNewTaskName('');
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

    const toggleTask = (taskId: number) => {
        setSelectedTasks(prev => {
            const next = new Set(prev);
            if (next.has(taskId)) next.delete(taskId);
            else next.add(taskId);
            return next;
        });
    };

    const toggleAll = () => {
        if (!project) return;
        const tasks = project.tasks ?? [];
        if (selectedTasks.size === tasks.length) {
            setSelectedTasks(new Set());
        } else {
            setSelectedTasks(new Set(tasks.map(t => t.id)));
        }
    };

    if (loading) {
        return (
            <div className="flex h-screen w-full bg-background-light dark:bg-background-dark">
                <Sidebar displayName={`${displayName}'s Space`} />
                <div className="flex-1 flex items-center justify-center">
                    <div className="w-8 h-8 border-4 border-primary border-t-transparent rounded-full animate-spin" />
                </div>
            </div>
        );
    }

    if (!project) {
        return (
            <div className="flex h-screen w-full bg-background-light dark:bg-background-dark">
                <Sidebar displayName={`${displayName}'s Space`} />
                <div className="flex-1 flex flex-col items-center justify-center gap-4">
                    <span className="material-symbols-outlined text-[48px] text-text-secondary">folder_off</span>
                    <p className="text-text-secondary text-lg">Project not found</p>
                    <Link to="/projects" className="text-primary font-semibold hover:underline">Back to Projects</Link>
                </div>
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
    const remainingDuration = totalDuration - loggedDuration;
    const estimatedSessions = Math.max(1, Math.ceil(remainingDuration / 25));

    return (
        <div className="flex h-screen w-full bg-background-light dark:bg-background-dark font-display text-text-main">
            <Sidebar displayName={`${displayName}'s Space`} />

            <div className="flex-1 flex flex-col overflow-hidden">
                {/* ── Top header bar ── */}
                <header className="flex items-center justify-between px-8 py-4 border-b border-border-dark bg-surface-dark/50 backdrop-blur-sm shrink-0">
                    <nav className="flex items-center gap-2 text-sm text-text-secondary">
                        <Link className="hover:text-primary transition-colors" to="/projects">Projects</Link>
                        <span className="material-symbols-outlined text-[14px]">chevron_right</span>
                        <span className="text-text-main font-medium">{project.projectName}</span>
                    </nav>
                    <div className="flex items-center gap-3">
                        <button
                            onClick={() => setEditProjectOpen(true)}
                            className="flex items-center gap-2 px-3.5 py-2 rounded-lg border border-border-dark text-text-secondary text-sm font-medium hover:text-text-main hover:border-text-secondary/40 transition-colors"
                        >
                            <span className="material-symbols-outlined text-[18px]">settings</span>
                            Settings
                        </button>
                        {project.githubLink && (
                            <a
                                href={project.githubLink}
                                target="_blank"
                                rel="noreferrer"
                                className="flex items-center gap-2 px-3.5 py-2 rounded-lg border border-border-dark text-text-secondary text-sm font-medium hover:text-text-main hover:border-text-secondary/40 transition-colors"
                            >
                                <span className="material-symbols-outlined text-[18px]">link</span>
                                Repo
                            </a>
                        )}
                    </div>
                </header>

                {/* ── Scrollable content ── */}
                <main className="flex-1 overflow-y-auto px-8 py-8">
                    <div className="max-w-5xl mx-auto flex flex-col gap-8">

                        {/* ── Project title + progress ── */}
                        <section className="flex flex-col gap-4">
                            <div className="flex items-start justify-between gap-6">
                                <div className="flex flex-col gap-2">
                                    <div className="flex items-center gap-3">
                                        <h1 className="text-3xl font-bold tracking-tight">{project.projectName}</h1>
                                        {project.state && (
                                            <span className={`px-2.5 py-0.5 rounded-full text-xs font-bold border flex items-center gap-1.5 ${PROJECT_STATE_STYLES[project.state] ?? ''}`}>
                                                {project.state === 'IN_PROGRESS' && (
                                                    <span className="size-1.5 rounded-full bg-green-400 animate-pulse" />
                                                )}
                                                {PROJECT_STATE_LABELS[project.state] ?? project.state}
                                            </span>
                                        )}
                                    </div>
                                    {project.description && (
                                        <p className="text-text-secondary text-base leading-relaxed max-w-xl">{project.description}</p>
                                    )}
                                </div>
                            </div>

                            {/* Progress bar */}
                            <div className="flex items-center gap-4">
                                <div className="flex-1 h-2 bg-surface-dark-alt rounded-full overflow-hidden">
                                    <div
                                        className="h-full bg-gradient-to-r from-primary to-accent-pop rounded-full transition-all duration-700"
                                        style={{ width: `${pct}%` }}
                                    />
                                </div>
                                <span className="text-sm font-semibold text-text-main tabular-nums">{pct}%</span>
                            </div>
                        </section>

                        {/* ── Focus session cards ── */}
                        <section className="grid grid-cols-1 md:grid-cols-2 gap-4">
                            <button
                                onClick={() => navigate(`/focus/${project.projectId}`)}
                                className="group flex items-center gap-4 p-5 rounded-xl border border-border-dark bg-surface-dark hover:border-primary/50 hover:bg-surface-dark-alt transition-all text-left"
                            >
                                <div className="w-12 h-12 rounded-xl bg-primary/10 flex items-center justify-center group-hover:bg-primary/20 transition-colors">
                                    <span className="material-symbols-outlined text-[28px] text-primary">play_circle</span>
                                </div>
                                <div className="flex flex-col gap-0.5">
                                    <span className="text-sm font-semibold text-text-main group-hover:text-primary transition-colors">Start Focus Session</span>
                                    <span className="text-xs text-text-secondary">25 min Pomodoro with your next task</span>
                                </div>
                                <span className="material-symbols-outlined text-text-secondary ml-auto group-hover:text-primary transition-colors">arrow_forward</span>
                            </button>
                            <div className="flex items-center gap-4 p-5 rounded-xl border border-border-dark bg-surface-dark">
                                <div className="w-12 h-12 rounded-xl bg-accent-pop/10 flex items-center justify-center">
                                    <span className="material-symbols-outlined text-[28px] text-accent-pop">timer</span>
                                </div>
                                <div className="flex flex-col gap-0.5">
                                    <span className="text-sm font-semibold text-text-main">
                                        ~{estimatedSessions} session{estimatedSessions !== 1 ? 's' : ''} remaining
                                    </span>
                                    <span className="text-xs text-text-secondary">
                                        {formatDurationLong(remainingDuration)} of work left &middot; {project.sessions ?? 0} completed
                                    </span>
                                </div>
                            </div>
                        </section>

                        {/* ── Task Queue Table ── */}
                        <section className="flex flex-col gap-3">
                            <div className="flex items-center justify-between">
                                <h2 className="text-lg font-semibold">Task Queue</h2>
                                <button
                                    onClick={openNewTask}
                                    className="flex items-center gap-1.5 px-3.5 py-2 rounded-lg bg-primary text-white text-sm font-semibold hover:bg-primary-dark transition-colors shadow-lg shadow-primary/20"
                                >
                                    <span className="material-symbols-outlined text-[18px]">add</span>
                                    Add Task
                                </button>
                            </div>

                            <div className="rounded-xl border border-border-dark overflow-hidden bg-surface-dark">
                                {/* Table header */}
                                <div className="grid grid-cols-[40px_1fr_120px_100px_48px] items-center px-4 py-3 border-b border-border-dark text-xs font-medium text-text-secondary uppercase tracking-wider">
                                    <div className="flex items-center justify-center">
                                        <input
                                            type="checkbox"
                                            checked={tasks.length > 0 && selectedTasks.size === tasks.length}
                                            onChange={toggleAll}
                                            className="task-checkbox w-4 h-4 rounded border-border-dark cursor-pointer"
                                        />
                                    </div>
                                    <div>Task</div>
                                    <div>Status</div>
                                    <div>Duration</div>
                                    <div />
                                </div>

                                {/* Task rows */}
                                {tasks.length === 0 ? (
                                    <div className="flex flex-col items-center justify-center py-16 gap-3">
                                        <span className="material-symbols-outlined text-[40px] text-text-secondary/50">task_alt</span>
                                        <p className="text-text-secondary text-sm">No tasks yet</p>
                                        <button onClick={openNewTask} className="text-primary text-sm font-medium hover:underline">
                                            Create your first task
                                        </button>
                                    </div>
                                ) : (
                                    tasks.map((task) => (
                                        <TaskRow
                                            key={task.id}
                                            task={task}
                                            selected={selectedTasks.has(task.id)}
                                            menuOpen={menuOpenId === task.id}
                                            onToggleSelect={() => toggleTask(task.id)}
                                            onToggleMenu={() => setMenuOpenId(menuOpenId === task.id ? null : task.id)}
                                            onCloseMenu={() => setMenuOpenId(null)}
                                            onEdit={() => openEditTask(task)}
                                            onDelete={() => handleDeleteTask(task.id)}
                                            onStateChange={(s) => handleUpdateTask(task.id, { ...task, state: s })}
                                        />
                                    ))
                                )}

                                {/* Quick-add row */}
                                <div className="grid grid-cols-[40px_1fr_120px_100px_48px] items-center px-4 py-3 border-t border-border-dark">
                                    <div className="flex items-center justify-center">
                                        <span className="material-symbols-outlined text-[18px] text-text-secondary/40">add</span>
                                    </div>
                                    <input
                                        type="text"
                                        value={newTaskName}
                                        onChange={(e) => setNewTaskName(e.target.value)}
                                        onKeyDown={(e) => e.key === 'Enter' && handleQuickCreateTask()}
                                        placeholder="Quick add a task..."
                                        className="bg-transparent text-sm text-text-main placeholder:text-text-secondary/50 outline-none"
                                    />
                                    <div />
                                    <div />
                                    <div className="flex items-center justify-center">
                                        {newTaskName.trim().length >= 3 && (
                                            <button
                                                onClick={handleQuickCreateTask}
                                                className="text-primary hover:text-primary-dark transition-colors"
                                            >
                                                <span className="material-symbols-outlined text-[20px]">send</span>
                                            </button>
                                        )}
                                    </div>
                                </div>
                            </div>
                        </section>

                        {/* ── Stats row ── */}
                        <section className="grid grid-cols-2 lg:grid-cols-4 gap-4">
                            <StatCard
                                label="Progress"
                                value={`${pct}%`}
                                detail={`${doneTasks}/${totalTasks} done`}
                                barPct={pct}
                            />
                            <StatCard
                                label="Tasks"
                                value={String(totalTasks)}
                                detail={`${tasksByState.IN_PROGRESS.length} active`}
                                icon="task_alt"
                                iconColor="text-primary"
                            />
                            <StatCard
                                label="Effort"
                                value={formatDuration(totalDuration)}
                                detail={`${formatDuration(loggedDuration)} done`}
                                icon="timer"
                                iconColor="text-primary"
                            />
                            <StatCard
                                label="Sessions"
                                value={String(project.sessions ?? 0)}
                                detail="focus sessions"
                                icon="bolt"
                                iconColor="text-accent-pop"
                            />
                        </section>
                    </div>
                </main>
            </div>

            {/* ── Modals ── */}
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

/* ── Task Row (table-row based) ── */

interface TaskRowProps {
    task: TaskResponseDto;
    selected: boolean;
    menuOpen: boolean;
    onToggleSelect: () => void;
    onToggleMenu: () => void;
    onCloseMenu: () => void;
    onEdit: () => void;
    onDelete: () => void;
    onStateChange: (state: TaskState) => void;
}

function TaskRow({ task, selected, menuOpen, onToggleSelect, onToggleMenu, onCloseMenu, onEdit, onDelete, onStateChange }: TaskRowProps) {
    const isFinished = task.state === 'FINISHED';

    return (
        <div
            className={`grid grid-cols-[40px_1fr_120px_100px_48px] items-center px-4 py-3 border-b border-border-dark group hover:bg-surface-dark-alt/50 transition-colors ${selected ? 'bg-primary/5' : ''}`}
        >
            {/* Checkbox */}
            <div className="flex items-center justify-center">
                <input
                    type="checkbox"
                    checked={selected}
                    onChange={onToggleSelect}
                    className="task-checkbox w-4 h-4 rounded border-border-dark cursor-pointer"
                />
            </div>

            {/* Task name + description */}
            <div className="flex flex-col gap-0.5 min-w-0 cursor-pointer" onClick={onEdit}>
                <span className={`text-sm font-medium truncate ${isFinished ? 'line-through text-text-secondary' : 'text-text-main group-hover:text-primary'} transition-colors`}>
                    {task.taskName}
                </span>
                {task.description && (
                    <span className="text-xs text-text-secondary truncate max-w-md">{task.description}</span>
                )}
            </div>

            {/* Status badge */}
            <div>
                <StatusBadge state={task.state} />
            </div>

            {/* Duration */}
            <div className="text-sm text-text-secondary tabular-nums font-mono">
                {formatDuration(task.duration)}
            </div>

            {/* Context menu */}
            <div className="flex items-center justify-center relative">
                <button
                    onClick={onToggleMenu}
                    className="text-text-secondary hover:text-text-main transition-colors opacity-0 group-hover:opacity-100"
                >
                    <span className="material-symbols-outlined text-[20px]">more_vert</span>
                </button>
                {menuOpen && (
                    <>
                        <div className="fixed inset-0 z-10" onClick={onCloseMenu} />
                        <div className="absolute right-0 top-8 z-20 bg-surface-dark border border-border-dark rounded-lg shadow-xl py-1 min-w-[160px]">
                            <button
                                onClick={() => { onEdit(); onCloseMenu(); }}
                                className="w-full px-3 py-2 text-left text-sm hover:bg-surface-dark-alt flex items-center gap-2 text-text-main"
                            >
                                <span className="material-symbols-outlined text-[16px]">edit</span>
                                Edit Task
                            </button>
                            {task.state !== 'IN_PROGRESS' && (
                                <button
                                    onClick={() => { onStateChange('IN_PROGRESS'); onCloseMenu(); }}
                                    className="w-full px-3 py-2 text-left text-sm hover:bg-surface-dark-alt flex items-center gap-2 text-primary"
                                >
                                    <span className="material-symbols-outlined text-[16px]">play_arrow</span>
                                    Start Task
                                </button>
                            )}
                            {task.state !== 'FINISHED' && (
                                <button
                                    onClick={() => { onStateChange('FINISHED'); onCloseMenu(); }}
                                    className="w-full px-3 py-2 text-left text-sm hover:bg-surface-dark-alt flex items-center gap-2 text-accent-pop"
                                >
                                    <span className="material-symbols-outlined text-[16px]">check_circle</span>
                                    Complete
                                </button>
                            )}
                            {task.state !== 'PENDING' && (
                                <button
                                    onClick={() => { onStateChange('PENDING'); onCloseMenu(); }}
                                    className="w-full px-3 py-2 text-left text-sm hover:bg-surface-dark-alt flex items-center gap-2 text-text-secondary"
                                >
                                    <span className="material-symbols-outlined text-[16px]">undo</span>
                                    Move to Todo
                                </button>
                            )}
                            <hr className="my-1 border-border-dark" />
                            <button
                                onClick={() => { onDelete(); onCloseMenu(); }}
                                className="w-full px-3 py-2 text-left text-sm hover:bg-red-900/20 flex items-center gap-2 text-red-400"
                            >
                                <span className="material-symbols-outlined text-[16px]">delete</span>
                                Delete
                            </button>
                        </div>
                    </>
                )}
            </div>
        </div>
    );
}

/* ── Stat Card ── */

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
        <div className="p-4 rounded-xl border border-border-dark bg-surface-dark flex flex-col gap-1.5">
            <div className="flex items-center justify-between">
                <span className="text-xs font-medium text-text-secondary uppercase tracking-wider">{label}</span>
                {icon && (
                    <span className={`material-symbols-outlined text-[16px] ${iconColor ?? 'text-text-secondary'}`}>{icon}</span>
                )}
            </div>
            <span className="text-2xl font-bold tabular-nums">{value || '—'}</span>
            <span className="text-xs text-text-secondary">{detail}</span>
            {barPct !== undefined && (
                <div className="w-full bg-surface-dark-alt h-1.5 rounded-full mt-1 overflow-hidden">
                    <div className="bg-gradient-to-r from-primary to-accent-pop h-full rounded-full transition-all duration-700" style={{ width: `${barPct}%` }} />
                </div>
            )}
        </div>
    );
}
