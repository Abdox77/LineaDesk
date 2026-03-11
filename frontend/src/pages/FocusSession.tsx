import React, { useEffect, useState, useCallback, useRef } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { fetchProject, updateTask, updateProject } from '../api/endpoints';
import type { ProjectResponseDto, TaskResponseDto, TaskState, PomodoroSettings } from '../api/types';
import { logActivity } from '../api/activityTracker';
import { useToast } from '../components/ToastProvider';

const FOCUS_MINUTES = 25;
const SHORT_BREAK_MINUTES = 5;
const LONG_BREAK_MINUTES = 15;
const SESSIONS_BEFORE_LONG_BREAK = 4;

type TimerMode = 'focus' | 'short-break' | 'long-break';
type TimerState = 'idle' | 'running' | 'paused';

const MODE_LABELS: Record<TimerMode, string> = {
    'focus': 'Focus Session',
    'short-break': 'Short Break',
    'long-break': 'Long Break',
};

function pad(n: number): string {
    return n.toString().padStart(2, '0');
}

export function FocusSession() {
    const { projectId } = useParams<{ projectId: string }>();
    const navigate = useNavigate();
    const { showToast } = useToast();

    const [project, setProject] = useState<ProjectResponseDto | null>(null);
    const [loading, setLoading] = useState(true);
    const [currentTaskIndex, setCurrentTaskIndex] = useState(0);

    const [mode, setMode] = useState<TimerMode>('focus');
    const [timerState, setTimerState] = useState<TimerState>('idle');
    const [secondsLeft, setSecondsLeft] = useState(FOCUS_MINUTES * 60);
    const [completedSessions, setCompletedSessions] = useState(0);
    const [taskCompleted, setTaskCompleted] = useState(false);
    const [showSettings, setShowSettings] = useState(false);
    const [showTaskList, setShowTaskList] = useState(false);

    const loadSettings = (): PomodoroSettings => {
        try {
            const raw = localStorage.getItem('pomodoroSettings');
            if (raw) return JSON.parse(raw);
        } catch {}
        return {
            focusMinutes: FOCUS_MINUTES,
            shortBreakMinutes: SHORT_BREAK_MINUTES,
            longBreakMinutes: LONG_BREAK_MINUTES,
            sessionsBeforeLongBreak: SESSIONS_BEFORE_LONG_BREAK,
        };
    };

    const saveSettings = (s: PomodoroSettings) => {
        localStorage.setItem('pomodoroSettings', JSON.stringify(s));
    };

    const [settings, setSettings] = useState<PomodoroSettings>(loadSettings);

    const intervalRef = useRef<ReturnType<typeof setInterval> | null>(null);

    const totalSeconds = mode === 'focus'
        ? settings.focusMinutes * 60
        : mode === 'short-break'
            ? settings.shortBreakMinutes * 60
            : settings.longBreakMinutes * 60;

    const progressPct = totalSeconds > 0 ? ((totalSeconds - secondsLeft) / totalSeconds) * 100 : 0;

    const loadProject = useCallback(async () => {
        if (!projectId) return;
        try {
            const p = await fetchProject(Number(projectId));
            setProject(p);
        } catch {
        } finally {
            setLoading(false);
        }
    }, [projectId]);

    useEffect(() => {
        loadProject();
    }, [loadProject]);

    const activeTasks = (project?.tasks ?? []).filter(t => t.state !== 'FINISHED');
    const currentTask: TaskResponseDto | undefined = activeTasks[currentTaskIndex];

    useEffect(() => {
        if (timerState === 'running') {
            intervalRef.current = setInterval(() => {
                setSecondsLeft(prev => {
                    if (prev <= 1) {
                        clearInterval(intervalRef.current!);
                        handleTimerComplete();
                        return 0;
                    }
                    return prev - 1;
                });
            }, 1000);
        } else {
            if (intervalRef.current) clearInterval(intervalRef.current);
        }
        return () => {
            if (intervalRef.current) clearInterval(intervalRef.current);
        };
    }, [timerState]);

    const handleTimerComplete = () => {
        setTimerState('idle');
        if (mode === 'focus') {
            const newCompleted = completedSessions + 1;
            setCompletedSessions(newCompleted);
            logActivity('focus_session');
            showToast('Focus session completed! 🎯');
            if (project) {
                updateProject(project.projectId, { sessions: (project.sessions ?? 0) + 1 }).catch(() => {});
            }
            if (newCompleted % settings.sessionsBeforeLongBreak === 0) {
                setMode('long-break');
                setSecondsLeft(settings.longBreakMinutes * 60);
            } else {
                setMode('short-break');
                setSecondsLeft(settings.shortBreakMinutes * 60);
            }
        } else {
            setMode('focus');
            setSecondsLeft(settings.focusMinutes * 60);
        }
    };

    const startTimer = () => {
        if (timerState === 'idle') {
            if (secondsLeft === 0) {
                setSecondsLeft(totalSeconds);
            }
        }
        setTimerState('running');
    };

    const pauseTimer = () => {
        setTimerState('paused');
    };

    const resumeTimer = () => {
        setTimerState('running');
    };

    const handleCompleteEarly = async () => {
        if (intervalRef.current) clearInterval(intervalRef.current);
        setTimerState('idle');

        if (mode === 'focus') {
            const newCompleted = completedSessions + 1;
            setCompletedSessions(newCompleted);
            logActivity('focus_session');
            if (project) {
                updateProject(project.projectId, { sessions: (project.sessions ?? 0) + 1 }).catch(() => {});
            }
            setMode('short-break');
            setSecondsLeft(settings.shortBreakMinutes * 60);
        } else {
            setMode('focus');
            setSecondsLeft(settings.focusMinutes * 60);
        }
    };

    const handleCompleteTask = async () => {
        if (!currentTask) return;
        try {
            await updateTask(currentTask.id, { ...currentTask, state: 'FINISHED' as TaskState });
            logActivity('task_completed');
            setTaskCompleted(true);
            setTimeout(() => {
                setTaskCompleted(false);
                loadProject().then(() => {
                    if (currentTaskIndex >= activeTasks.length - 1) {
                        setCurrentTaskIndex(0);
                    }
                });
            }, 1500);
        } catch {}
    };

    const handleExit = () => {
        if (intervalRef.current) clearInterval(intervalRef.current);
        navigate(`/project/${projectId}`);
    };

    const nextTask = () => {
        if (currentTaskIndex < activeTasks.length - 1) {
            setCurrentTaskIndex(prev => prev + 1);
        }
    };

    const prevTask = () => {
        if (currentTaskIndex > 0) {
            setCurrentTaskIndex(prev => prev - 1);
        }
    };

    const minutes = Math.floor(secondsLeft / 60);
    const seconds = secondsLeft % 60;
    const timeDisplay = `${pad(minutes)}:${pad(seconds)}`;

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
                <span className="material-symbols-outlined text-[48px] text-text-secondary">folder_off</span>
                <p className="text-text-secondary text-lg">Project not found</p>
                <button onClick={() => navigate('/projects')} className="text-primary font-semibold hover:underline">
                    Back to Projects
                </button>
            </div>
        );
    }

    return (
        <div className="font-display bg-background-light dark:bg-background-dark text-gray-900 dark:text-neutral-400 min-h-screen flex flex-col relative overflow-hidden selection:bg-primary/30">
            <div className="absolute inset-0 z-0 focus-gradient pointer-events-none opacity-60 dark:opacity-20" />

            <div className="absolute top-0 left-0 w-full h-[2px] bg-gray-200 dark:bg-neutral-900 z-50">
                <div
                    className="h-full bg-primary shadow-[0_0_15px_rgba(56,189,248,0.5)] transition-all duration-1000 ease-linear"
                    style={{ width: `${progressPct}%` }}
                />
            </div>

            <header className="relative z-10 flex items-center justify-between w-full px-6 md:px-8 py-5">
                <div className="flex items-center gap-4">
                    <div className="flex items-center gap-2 text-gray-400 dark:text-neutral-600 cursor-default select-none">
                        <span className="material-symbols-outlined text-xl">bolt</span>
                        <span className="text-xs font-bold tracking-widest uppercase">
                            Session {completedSessions + 1}
                        </span>
                    </div>
                    {completedSessions > 0 && (
                        <div className="flex items-center gap-1.5 text-xs font-medium text-accent-pop">
                            <span className="material-symbols-outlined text-[16px]">check_circle</span>
                            {completedSessions} done
                        </div>
                    )}
                </div>
                <div className="flex items-center gap-2">
                    <button
                        onClick={() => setShowSettings(true)}
                        className="group flex items-center justify-center w-10 h-10 rounded-full hover:bg-gray-200/50 dark:hover:bg-white/5 transition-all text-gray-400 hover:text-gray-900 dark:text-neutral-600 dark:hover:text-white"
                        title="Timer Settings"
                    >
                        <span className="material-symbols-outlined text-xl">settings</span>
                    </button>
                    <button
                        onClick={() => setShowTaskList(!showTaskList)}
                        className={`group flex items-center justify-center w-10 h-10 rounded-full hover:bg-gray-200/50 dark:hover:bg-white/5 transition-all ${showTaskList ? 'text-primary' : 'text-gray-400 hover:text-gray-900 dark:text-neutral-600 dark:hover:text-white'}`}
                        title="Task List"
                    >
                        <span className="material-symbols-outlined text-xl">checklist</span>
                    </button>
                    <button
                        onClick={handleExit}
                        className="group flex items-center justify-center w-10 h-10 rounded-full hover:bg-gray-200/50 dark:hover:bg-white/5 transition-all text-gray-400 hover:text-gray-900 dark:text-neutral-600 dark:hover:text-white"
                        title="Exit Focus Mode"
                    >
                        <span className="material-symbols-outlined text-2xl transition-transform group-hover:rotate-90">
                            fullscreen_exit
                        </span>
                    </button>
                </div>
            </header>

            <main className="relative z-10 flex-grow flex flex-col items-center justify-center w-full max-w-4xl mx-auto px-4 -mt-8">
                <div className="flex flex-col items-center mb-16 md:mb-20">
                    <div className="relative group cursor-default">
                        <h1 className="text-[100px] md:text-[140px] leading-none font-extrabold tracking-tighter text-gray-800 dark:text-gray-200 tabular-nums select-none drop-shadow-sm dark:drop-shadow-[0_0_50px_rgba(255,255,255,0.08)]">
                            {timeDisplay}
                        </h1>
                        <div className="absolute -bottom-8 left-1/2 transform -translate-x-1/2 opacity-0 group-hover:opacity-100 transition-opacity duration-300 whitespace-nowrap">
                            <span className="text-xs font-bold text-primary uppercase tracking-[0.2em] bg-primary/5 px-4 py-1.5 rounded-full border border-primary/10">
                                {MODE_LABELS[mode]}
                            </span>
                        </div>
                    </div>

                    {timerState === 'idle' && (
                        <div className="flex items-center gap-2 mt-12">
                            {(['focus', 'short-break', 'long-break'] as TimerMode[]).map(m => (
                                <button
                                    key={m}
                                    onClick={() => {
                                        setMode(m);
                                        setSecondsLeft(
                                            m === 'focus' ? settings.focusMinutes * 60
                                                : m === 'short-break' ? settings.shortBreakMinutes * 60
                                                    : settings.longBreakMinutes * 60
                                        );
                                    }}
                                    className={`px-4 py-2 rounded-full text-xs font-bold transition-all ${
                                        mode === m
                                            ? 'bg-primary/10 text-primary border border-primary/20'
                                            : 'text-gray-400 dark:text-neutral-600 hover:text-gray-600 dark:hover:text-neutral-400 border border-transparent'
                                    }`}
                                >
                                    {MODE_LABELS[m]}
                                </button>
                            ))}
                        </div>
                    )}
                </div>

                {currentTask && (
                    <div className="w-full max-w-[520px] transform transition-all hover:-translate-y-1 hover:shadow-2xl duration-500 ease-out">
                        <div className={`bg-white dark:bg-surface-dark rounded-xl shadow-xl border border-gray-100 dark:border-neutral-800 p-0 overflow-hidden group relative transition-all duration-300 ${taskCompleted ? 'scale-95 opacity-50' : ''}`}>
                            <div className="absolute top-0 left-0 w-1 h-full bg-primary/80 group-hover:bg-primary transition-colors" />

                            <div className="p-5 md:p-6 flex items-start gap-4 md:gap-5">
                                <div className="pt-1 flex-shrink-0">
                                    <div className="relative w-6 h-6">
                                        <input
                                            className="peer h-6 w-6 cursor-pointer appearance-none rounded border-2 border-gray-300 dark:border-neutral-700 transition-all checked:border-primary checked:bg-primary hover:border-primary dark:hover:border-primary focus:ring-0 focus:ring-offset-0 bg-transparent"
                                            type="checkbox"
                                            checked={taskCompleted}
                                            onChange={handleCompleteTask}
                                        />
                                        <span className="material-symbols-outlined absolute top-0 left-0 pointer-events-none text-white text-[20px] leading-6 w-6 text-center opacity-0 peer-checked:opacity-100 transition-opacity transform peer-checked:scale-100 scale-50 duration-200">
                                            check
                                        </span>
                                    </div>
                                </div>

                                <div className="flex-grow min-w-0">
                                    <h2 className="text-lg md:text-xl font-bold text-gray-800 dark:text-neutral-300 leading-tight mb-3 truncate pr-2">
                                        {currentTask.taskName}
                                    </h2>
                                    <div className="flex items-center gap-3 flex-wrap">
                                        {currentTask.description && (
                                            <span className="text-xs text-gray-500 dark:text-neutral-500 line-clamp-1">
                                                {currentTask.description}
                                            </span>
                                        )}
                                        {currentTask.duration > 0 && (
                                            <span className="inline-flex items-center gap-1 px-2 py-0.5 rounded-md bg-gray-100 dark:bg-neutral-900 text-gray-500 dark:text-neutral-500 border border-gray-200 dark:border-neutral-800 text-xs font-mono">
                                                <span className="material-symbols-outlined text-[14px]">schedule</span>
                                                {currentTask.duration}m
                                            </span>
                                        )}
                                    </div>
                                </div>

                                {activeTasks.length > 1 && (
                                    <div className="flex flex-col gap-1 shrink-0">
                                        <button
                                            onClick={prevTask}
                                            disabled={currentTaskIndex === 0}
                                            className="text-gray-400 dark:text-neutral-600 hover:text-primary disabled:opacity-30 transition-colors p-0.5"
                                        >
                                            <span className="material-symbols-outlined text-[18px]">expand_less</span>
                                        </button>
                                        <span className="text-[10px] font-mono text-center text-gray-400 dark:text-neutral-600">
                                            {currentTaskIndex + 1}/{activeTasks.length}
                                        </span>
                                        <button
                                            onClick={nextTask}
                                            disabled={currentTaskIndex >= activeTasks.length - 1}
                                            className="text-gray-400 dark:text-neutral-600 hover:text-primary disabled:opacity-30 transition-colors p-0.5"
                                        >
                                            <span className="material-symbols-outlined text-[18px]">expand_more</span>
                                        </button>
                                    </div>
                                )}
                            </div>
                        </div>
                    </div>
                )}

                {activeTasks.length === 0 && (
                    <div className="text-center text-gray-400 dark:text-neutral-600">
                        <span className="material-symbols-outlined text-[40px] mb-2">task_alt</span>
                        <p className="text-sm font-medium">All tasks completed!</p>
                        <button onClick={handleExit} className="text-primary text-sm font-semibold mt-3 hover:underline">
                            Back to Project
                        </button>
                    </div>
                )}
            </main>

            <footer className="relative z-10 w-full flex justify-center pb-8 md:pb-12">
                <div className="flex items-center gap-1 p-1.5 bg-white dark:bg-surface-dark border border-gray-200 dark:border-neutral-800 rounded-full shadow-[0_4px_20px_rgba(0,0,0,0.08)] dark:shadow-none backdrop-blur-sm">
                    {timerState === 'idle' ? (
                        <button
                            onClick={startTimer}
                            className="flex items-center gap-2 px-6 py-3 rounded-full bg-primary/10 hover:bg-primary/20 text-primary transition-colors group"
                        >
                            <span className="material-symbols-outlined text-[20px] group-hover:scale-110 transition-transform">
                                play_arrow
                            </span>
                            <span className="text-sm font-bold">Start</span>
                        </button>
                    ) : timerState === 'running' ? (
                        <button
                            onClick={pauseTimer}
                            className="flex items-center gap-2 px-6 py-3 rounded-full hover:bg-gray-50 dark:hover:bg-neutral-900 transition-colors text-gray-600 dark:text-neutral-400 group"
                        >
                            <span className="material-symbols-outlined text-[20px] group-hover:text-primary transition-colors">
                                pause
                            </span>
                            <span className="text-sm font-bold">Pause</span>
                        </button>
                    ) : (
                        <button
                            onClick={resumeTimer}
                            className="flex items-center gap-2 px-6 py-3 rounded-full bg-primary/10 hover:bg-primary/20 text-primary transition-colors group"
                        >
                            <span className="material-symbols-outlined text-[20px] group-hover:scale-110 transition-transform">
                                play_arrow
                            </span>
                            <span className="text-sm font-bold">Resume</span>
                        </button>
                    )}

                    {(timerState === 'running' || timerState === 'paused') && (
                        <>
                            <div className="w-px h-5 bg-gray-200 dark:bg-neutral-800 mx-1" />
                            <button
                                onClick={handleCompleteEarly}
                                className="flex items-center gap-2 px-6 py-3 rounded-full hover:bg-green-50 dark:hover:bg-neutral-900/50 text-gray-600 dark:text-neutral-400 hover:text-green-600 dark:hover:text-green-400 transition-colors group"
                            >
                                <span className="material-symbols-outlined text-[20px] text-green-500 group-hover:scale-110 transition-transform">
                                    check_circle
                                </span>
                                <span className="text-sm font-bold">Complete Early</span>
                            </button>
                        </>
                    )}

                    {timerState === 'idle' && (
                        <>
                            <div className="w-px h-5 bg-gray-200 dark:bg-neutral-800 mx-1" />
                            <button
                                onClick={handleExit}
                                className="flex items-center gap-2 px-6 py-3 rounded-full hover:bg-gray-50 dark:hover:bg-neutral-900 text-gray-500 dark:text-neutral-500 hover:text-gray-700 dark:hover:text-neutral-300 transition-colors"
                            >
                                <span className="material-symbols-outlined text-[20px]">arrow_back</span>
                                <span className="text-sm font-bold">Exit</span>
                            </button>
                        </>
                    )}
                </div>
            </footer>

            {showTaskList && (
                <div className="fixed right-0 top-0 h-full w-80 bg-white dark:bg-surface-dark border-l border-gray-200 dark:border-neutral-800 shadow-2xl z-50 flex flex-col">
                    <div className="flex items-center justify-between px-5 py-4 border-b border-gray-200 dark:border-neutral-800">
                        <h3 className="text-sm font-bold text-gray-900 dark:text-gray-100">Task Queue</h3>
                        <button onClick={() => setShowTaskList(false)} className="text-gray-400 hover:text-gray-600 dark:hover:text-gray-300">
                            <span className="material-symbols-outlined text-[20px]">close</span>
                        </button>
                    </div>
                    <div className="flex-1 overflow-y-auto no-scrollbar">
                        {activeTasks.length === 0 ? (
                            <div className="flex flex-col items-center justify-center h-full text-gray-400 dark:text-neutral-600">
                                <span className="material-symbols-outlined text-[32px] mb-2">task_alt</span>
                                <p className="text-sm">All tasks completed!</p>
                            </div>
                        ) : (
                            activeTasks.map((task, idx) => (
                                <button
                                    key={task.id}
                                    onClick={() => { setCurrentTaskIndex(idx); setShowTaskList(false); }}
                                    className={`w-full text-left px-5 py-3.5 border-b border-gray-100 dark:border-neutral-800/50 flex items-center gap-3 transition-colors ${
                                        idx === currentTaskIndex
                                            ? 'bg-primary/10 border-l-2 border-l-primary'
                                            : 'hover:bg-gray-50 dark:hover:bg-neutral-900/50'
                                    }`}
                                >
                                    <div className={`w-6 h-6 rounded-full flex items-center justify-center text-xs font-bold flex-shrink-0 ${
                                        idx === currentTaskIndex
                                            ? 'bg-primary text-white'
                                            : 'bg-gray-100 dark:bg-neutral-800 text-gray-500 dark:text-neutral-400'
                                    }`}>
                                        {idx + 1}
                                    </div>
                                    <div className="flex-1 min-w-0">
                                        <p className={`text-sm font-medium truncate ${
                                            idx === currentTaskIndex
                                                ? 'text-primary'
                                                : 'text-gray-800 dark:text-gray-200'
                                        }`}>
                                            {task.taskName}
                                        </p>
                                        {task.description && (
                                            <p className="text-xs text-gray-500 dark:text-neutral-500 truncate mt-0.5">
                                                {task.description}
                                            </p>
                                        )}
                                    </div>
                                    {idx === currentTaskIndex && (
                                        <span className="material-symbols-outlined text-primary text-[16px] flex-shrink-0">arrow_back</span>
                                    )}
                                </button>
                            ))
                        )}
                    </div>
                </div>
            )}

            {showSettings && (
                <PomodoroSettingsModal
                    settings={settings}
                    onSave={(s) => {
                        setSettings(s);
                        saveSettings(s);
                        if (timerState === 'idle') {
                            setSecondsLeft(
                                mode === 'focus' ? s.focusMinutes * 60
                                    : mode === 'short-break' ? s.shortBreakMinutes * 60
                                        : s.longBreakMinutes * 60
                            );
                        }
                        setShowSettings(false);
                    }}
                    onClose={() => setShowSettings(false)}
                />
            )}
        </div>
    );
}

interface PomodoroSettingsModalProps {
    settings: PomodoroSettings;
    onSave: (s: PomodoroSettings) => void;
    onClose: () => void;
}

function PomodoroSettingsModal({ settings, onSave, onClose }: PomodoroSettingsModalProps) {
    const [focus, setFocus] = useState(settings.focusMinutes);
    const [shortBreak, setShortBreak] = useState(settings.shortBreakMinutes);
    const [longBreak, setLongBreak] = useState(settings.longBreakMinutes);
    const [rounds, setRounds] = useState(settings.sessionsBeforeLongBreak);

    return (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40 backdrop-blur-sm">
            <div className="bg-white dark:bg-surface-dark border border-gray-200 dark:border-neutral-800 rounded-xl shadow-2xl w-full max-w-sm mx-4 p-6">
                <div className="flex items-center justify-between mb-6">
                    <h3 className="text-lg font-bold text-gray-900 dark:text-white">Timer Settings</h3>
                    <button onClick={onClose} className="text-gray-400 hover:text-gray-600 dark:hover:text-gray-300">
                        <span className="material-symbols-outlined">close</span>
                    </button>
                </div>
                <div className="flex flex-col gap-4">
                    <SettingsField label="Focus Duration" value={focus} onChange={setFocus} min={1} max={120} unit="min" />
                    <SettingsField label="Short Break" value={shortBreak} onChange={setShortBreak} min={1} max={30} unit="min" />
                    <SettingsField label="Long Break" value={longBreak} onChange={setLongBreak} min={1} max={60} unit="min" />
                    <SettingsField label="Sessions before Long Break" value={rounds} onChange={setRounds} min={1} max={10} unit="" />
                </div>
                <div className="flex gap-2 mt-6 pt-4 border-t border-gray-200 dark:border-neutral-800">
                    <button
                        onClick={() => { setFocus(25); setShortBreak(5); setLongBreak(15); setRounds(4); }}
                        className="text-xs text-gray-500 dark:text-gray-400 hover:text-primary transition-colors"
                    >
                        Reset defaults
                    </button>
                    <div className="flex-1" />
                    <button
                        onClick={onClose}
                        className="px-4 py-2 rounded-lg border border-gray-200 dark:border-neutral-800 text-sm font-medium text-gray-600 dark:text-gray-400 hover:bg-gray-50 dark:hover:bg-neutral-900"
                    >
                        Cancel
                    </button>
                    <button
                        onClick={() => onSave({
                            focusMinutes: focus,
                            shortBreakMinutes: shortBreak,
                            longBreakMinutes: longBreak,
                            sessionsBeforeLongBreak: rounds,
                        })}
                        className="px-4 py-2 rounded-lg bg-primary text-white text-sm font-semibold hover:bg-primary-dark transition-colors"
                    >
                        Save
                    </button>
                </div>
            </div>
        </div>
    );
}

function SettingsField({ label, value, onChange, min, max, unit }: {
    label: string; value: number; onChange: (v: number) => void; min: number; max: number; unit: string;
}) {
    return (
        <div className="flex items-center justify-between">
            <label className="text-sm font-medium text-gray-700 dark:text-gray-300">{label}</label>
            <div className="flex items-center gap-2">
                <button
                    onClick={() => onChange(Math.max(min, value - 1))}
                    className="w-8 h-8 rounded-lg border border-gray-200 dark:border-neutral-800 flex items-center justify-center text-gray-500 hover:text-primary hover:border-primary transition-colors"
                >
                    <span className="material-symbols-outlined text-[16px]">remove</span>
                </button>
                <span className="w-10 text-center text-sm font-bold text-gray-900 dark:text-white tabular-nums">
                    {value}
                </span>
                <button
                    onClick={() => onChange(Math.min(max, value + 1))}
                    className="w-8 h-8 rounded-lg border border-gray-200 dark:border-neutral-800 flex items-center justify-center text-gray-500 hover:text-primary hover:border-primary transition-colors"
                >
                    <span className="material-symbols-outlined text-[16px]">add</span>
                </button>
                {unit && <span className="text-xs text-gray-500 dark:text-gray-400 w-6">{unit}</span>}
            </div>
        </div>
    );
}
