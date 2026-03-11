import React, { useEffect, useState, useCallback, useMemo, useRef } from 'react';
import { Sidebar } from '../components/Sidebar';
import { useAuth } from '../auth/AuthContext';
import { useToast } from '../components/ToastProvider';
import {
    fetchHabits,
    createHabit,
    updateHabit,
    deleteHabit,
    logHabitDay,
    unlogHabitDay,
    fetchAllHabitLogs,
} from '../api/endpoints';
import type { HabitResponseDto, HabitRequestDto, HabitLogResponseDto, HabitType } from '../api/types';

const HABIT_TYPE_LABELS: Record<HabitType, string> = {
    FITNESS: 'Fitness',
    MENTAL_WELLBEING: 'Wellbeing',
    INTELLECTUAL: 'Intellectual',
};

const HABIT_TYPE_ICONS: Record<HabitType, string> = {
    FITNESS: 'fitness_center',
    MENTAL_WELLBEING: 'self_improvement',
    INTELLECTUAL: 'school',
};

const HABIT_TYPE_COLORS: Record<HabitType, string> = {
    FITNESS: 'text-rose-500',
    MENTAL_WELLBEING: 'text-violet-500',
    INTELLECTUAL: 'text-amber-500',
};

function todayStr(): string {
    return new Date().toISOString().slice(0, 10);
}

function daysAgo(n: number): string {
    const d = new Date();
    d.setDate(d.getDate() - n);
    return d.toISOString().slice(0, 10);
}

export function Habits() {
    const { user } = useAuth();
    const { showToast } = useToast();
    const displayName = user?.username ?? 'Developer';

    const [habits, setHabits] = useState<HabitResponseDto[]>([]);
    const [logs, setLogs] = useState<HabitLogResponseDto[]>([]);
    const [loading, setLoading] = useState(true);
    const [showCreateModal, setShowCreateModal] = useState(false);
    const [editingHabit, setEditingHabit] = useState<HabitResponseDto | null>(null);

    const today = todayStr();
    const thirtyDaysAgo = daysAgo(29);

    const loadData = useCallback(async () => {
        try {
            const [h, l] = await Promise.all([
                fetchHabits(),
                fetchAllHabitLogs(thirtyDaysAgo, today),
            ]);
            setHabits(h);
            setLogs(l);
        } catch {
        } finally {
            setLoading(false);
        }
    }, [thirtyDaysAgo, today]);

    useEffect(() => {
        loadData();
    }, [loadData]);

    const logsByHabit = useMemo(() => {
        const map = new Map<number, Set<string>>();
        for (const log of logs) {
            if (!map.has(log.habitId)) map.set(log.habitId, new Set());
            map.get(log.habitId)!.add(log.date);
        }
        return map;
    }, [logs]);

    const handleToggleToday = async (habit: HabitResponseDto) => {
        const completed = logsByHabit.get(habit.id)?.has(today) ?? false;
        try {
            if (completed) {
                await unlogHabitDay(habit.id, today);
                showToast('Habit unmarked');
            } else {
                await logHabitDay(habit.id, today);
                showToast('Habit completed!');
            }
            await loadData();
        } catch {
            showToast('Failed to update habit', 'error');
        }
    };

    const handleCreateHabit = async (req: HabitRequestDto) => {
        try {
            await createHabit(req);
            showToast('Habit created');
            setShowCreateModal(false);
            await loadData();
        } catch {
            showToast('Failed to create habit', 'error');
        }
    };

    const handleUpdateHabit = async (id: number, req: Partial<HabitRequestDto>) => {
        try {
            await updateHabit(id, req);
            showToast('Habit updated');
            setEditingHabit(null);
            await loadData();
        } catch {
            showToast('Failed to update habit', 'error');
        }
    };

    const handleDeleteHabit = async (id: number) => {
        try {
            await deleteHabit(id);
            showToast('Habit deleted', 'warning');
            await loadData();
        } catch {
            showToast('Failed to delete habit', 'error');
        }
    };

    const getStreak = (habitId: number): number => {
        const dates = logsByHabit.get(habitId);
        if (!dates) return 0;
        let streak = 0;
        const d = new Date();
        while (true) {
            const ds = d.toISOString().slice(0, 10);
            if (dates.has(ds)) {
                streak++;
                d.setDate(d.getDate() - 1);
            } else {
                break;
            }
        }
        return streak;
    };

    const bestStreak = habits.reduce((max, h) => Math.max(max, getStreak(h.id)), 0);


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

    return (
        <div className="flex h-screen w-full bg-background-light dark:bg-background-dark font-display text-gray-900 dark:text-gray-200 overflow-hidden">
            <Sidebar displayName={`${displayName}'s Space`} />

            <main className="flex-1 h-full overflow-y-auto overflow-x-hidden no-scrollbar">
                <div className="max-w-6xl mx-auto px-6 py-8 md:px-10 lg:py-12 flex flex-col gap-8">
                    <header className="flex flex-col md:flex-row md:items-end justify-between gap-4">
                        <div className="flex flex-col gap-1">
                            <h1 className="text-3xl font-bold tracking-tight text-gray-900 dark:text-white leading-tight">
                                Habit Tracker
                            </h1>
                            <p className="text-gray-500 dark:text-gray-400 mt-1">
                                Stay consistent, stay productive.
                            </p>
                        </div>
                        <button
                            onClick={() => setShowCreateModal(true)}
                            className="flex items-center gap-2 px-4 py-2.5 bg-primary text-white rounded-lg text-sm font-semibold shadow-md hover:bg-sky-600 transition-all"
                        >
                            <span className="material-symbols-outlined text-[18px]">add</span>
                            New Habit
                        </button>
                    </header>

                    <div className="grid grid-cols-1 lg:grid-cols-12 gap-6">
                        
                        <div className="lg:col-span-5 flex flex-col gap-6">
                            <section className="bg-white dark:bg-surface-dark border border-gray-200 dark:border-border-dark rounded-xl p-6 shadow-sm flex flex-col" style={{ maxHeight: '420px' }}>
                                <div className="flex items-center justify-between mb-4">
                                    <h3 className="text-lg font-bold text-gray-900 dark:text-white flex items-center gap-2">
                                        <span className="material-symbols-outlined text-primary">checklist</span>
                                        Daily Checklist
                                    </h3>
                                    <span className="text-xs bg-gray-100 dark:bg-surface-dark-alt px-2 py-1 rounded text-gray-500 dark:text-gray-400">
                                        {new Date().toLocaleDateString('en-US', { month: 'short', day: 'numeric', year: 'numeric' })}
                                    </span>
                                </div>

                                {habits.length === 0 ? (
                                    <div className="flex flex-col items-center justify-center py-12 text-gray-400 dark:text-gray-500">
                                        <span className="material-symbols-outlined text-[32px] mb-2">inbox</span>
                                        <p className="text-sm">No habits yet</p>
                                    </div>
                                ) : (
                                    <div className="flex flex-col gap-2 flex-1 overflow-y-auto no-scrollbar">
                                        {habits.map((habit) => {
                                            const done = logsByHabit.get(habit.id)?.has(today) ?? false;
                                            return (
                                                <button
                                                    key={habit.id}
                                                    onClick={() => handleToggleToday(habit)}
                                                    className={`group flex items-center justify-between p-3 rounded-lg border transition-colors cursor-pointer text-left w-full shrink-0 ${
                                                        done
                                                            ? 'bg-primary/10 border-primary/20 dark:border-primary/30'
                                                            : 'bg-gray-50 dark:bg-surface-dark-alt/50 border-transparent hover:bg-gray-100 dark:hover:bg-surface-dark-alt'
                                                    }`}
                                                >
                                                    <div className="flex items-center gap-3">
                                                        <div
                                                            className={`size-5 rounded flex items-center justify-center transition-all flex-shrink-0 ${
                                                                done
                                                                    ? 'bg-primary text-white'
                                                                    : 'border-2 border-gray-300 dark:border-gray-600 text-transparent group-hover:border-primary'
                                                            }`}
                                                        >
                                                            <span className="material-symbols-outlined text-[16px]">check</span>
                                                        </div>
                                                        <div className="flex items-center gap-2">
                                                            <span className={`material-symbols-outlined text-[16px] ${HABIT_TYPE_COLORS[habit.type]}`}>
                                                                {HABIT_TYPE_ICONS[habit.type]}
                                                            </span>
                                                            <span className={`text-sm font-medium ${done ? 'line-through text-gray-400 dark:text-gray-500' : 'text-gray-900 dark:text-gray-100'}`}>
                                                                {habit.habitName}
                                                            </span>
                                                        </div>
                                                    </div>
                                                    <div className="flex items-center gap-2">
                                                        <span className={`text-xs font-bold ${done ? 'text-primary' : 'text-gray-400 dark:text-gray-500'}`}>
                                                            {done ? '✓' : ''}
                                                        </span>
                                                        <button
                                                            onClick={(e) => {
                                                                e.stopPropagation();
                                                                setEditingHabit(habit);
                                                            }}
                                                            className="text-gray-400 hover:text-gray-600 dark:hover:text-gray-300 opacity-0 group-hover:opacity-100 transition-opacity"
                                                        >
                                                            <span className="material-symbols-outlined text-[16px]">more_vert</span>
                                                        </button>
                                                    </div>
                                                </button>
                                            );
                                        })}
                                    </div>
                                )}

                                <button
                                    onClick={() => setShowCreateModal(true)}
                                    className="mt-3 w-full py-2.5 border-2 border-dashed border-gray-200 dark:border-border-dark rounded-lg text-gray-500 dark:text-gray-400 text-sm font-medium hover:border-primary/40 hover:text-primary hover:bg-primary/5 transition-all shrink-0"
                                >
                                    + Add New Habit
                                </button>
                            </section>

                            <div className="bg-primary p-6 rounded-xl text-white shadow-lg shadow-primary/20 relative overflow-hidden">
                                <div className="relative z-10 flex flex-col gap-1">
                                    <span className="text-xs font-bold uppercase tracking-widest opacity-80">
                                        Best Streak
                                    </span>
                                    <div className="flex items-end gap-2">
                                        <span className="text-5xl font-black">{bestStreak}</span>
                                        <span className="text-xl font-bold mb-1">Day{bestStreak !== 1 ? 's' : ''}</span>
                                    </div>
                                    <p className="text-sm mt-2 opacity-90 leading-snug">
                                        {bestStreak >= 7
                                            ? "You're on fire! Keep the momentum going!"
                                            : bestStreak > 0
                                              ? 'Great start! Build your streak day by day.'
                                              : 'Complete a habit today to start your streak!'}
                                    </p>
                                </div>
                                <span className="material-symbols-outlined absolute -right-4 -bottom-4 text-white/10 text-[140px] pointer-events-none">
                                    local_fire_department
                                </span>
                            </div>
                        </div>

                        
                        <div className="lg:col-span-7 flex flex-col gap-6">
                            <section className="bg-white dark:bg-surface-dark border border-gray-200 dark:border-border-dark rounded-xl p-6 shadow-sm">
                                <div className="flex items-center justify-between mb-6">
                                    <h3 className="text-lg font-bold text-gray-900 dark:text-white flex items-center gap-2">
                                        <span className="material-symbols-outlined text-primary">calendar_view_month</span>
                                        Habit Heatmap (Last 30 Days)
                                    </h3>
                                    <div className="flex items-center gap-4 text-xs text-gray-500 dark:text-gray-400">
                                        <div className="flex items-center gap-1.5">
                                            <div className="size-3 bg-gray-200 dark:bg-gray-700 rounded-sm" /> Empty
                                        </div>
                                        <div className="flex items-center gap-1.5">
                                            <div className="size-3 bg-primary rounded-sm" /> Completed
                                        </div>
                                    </div>
                                </div>

                                {habits.length === 0 ? (
                                    <div className="flex flex-col items-center justify-center py-12 text-gray-400 dark:text-gray-500">
                                        <span className="material-symbols-outlined text-[40px] mb-2">check_circle</span>
                                        <p className="text-sm">Create your first habit to see your heatmap</p>
                                    </div>
                                ) : (
                                    <div className="flex flex-col gap-8">
                                        {habits.map((habit) => {
                                            const habitDates = logsByHabit.get(habit.id) ?? new Set<string>();
                                            const completedDays = habitDates.size;
                                            const pct = Math.round((completedDays / 30) * 100);
                                            return (
                                                <HabitHeatmapRow
                                                    key={habit.id}
                                                    habit={habit}
                                                    logDates={habitDates}
                                                    completedDays={completedDays}
                                                    pct={pct}
                                                    today={today}
                                                />
                                            );
                                        })}
                                    </div>
                                )}
                            </section>
                        </div>
                    </div>

                    <div className="mt-4 pt-8 border-t border-gray-200 dark:border-border-dark flex flex-wrap gap-8 items-center text-gray-500 dark:text-gray-400">
                        <div className="flex items-center gap-2">
                            <span className="material-symbols-outlined text-primary">check_circle</span>
                            <span className="text-sm">
                                <span className="font-bold text-gray-900 dark:text-white">{habits.length}</span> Active habit{habits.length !== 1 ? 's' : ''}
                            </span>
                        </div>
                        <div className="flex items-center gap-2">
                            <span className="material-symbols-outlined text-primary">local_fire_department</span>
                            <span className="text-sm">
                                <span className="font-bold text-gray-900 dark:text-white">{bestStreak}</span> Best streak
                            </span>
                        </div>
                        <div className="flex items-center gap-2">
                            <span className="material-symbols-outlined text-primary">auto_graph</span>
                            <span className="text-sm">
                                <span className="font-bold text-gray-900 dark:text-white">{logs.length}</span> Total completions (30d)
                            </span>
                        </div>
                    </div>
                </div>
            </main>

            {(showCreateModal || editingHabit) && (
                <HabitModal
                    habit={editingHabit}
                    onSave={
                        editingHabit
                            ? (req) => handleUpdateHabit(editingHabit.id, req)
                            : handleCreateHabit
                    }
                    onDelete={editingHabit ? () => handleDeleteHabit(editingHabit.id) : undefined}
                    onClose={() => {
                        setShowCreateModal(false);
                        setEditingHabit(null);
                    }}
                />
            )}
        </div>
    );
}

interface HabitHeatmapRowProps {
    habit: HabitResponseDto;
    logDates: Set<string>;
    completedDays: number;
    pct: number;
    today: string;
}

function HabitHeatmapRow({ habit, logDates, completedDays, pct, today }: HabitHeatmapRowProps) {
    const days: { date: string; completed: boolean; isToday: boolean }[] = [];
    for (let i = 0; i <= 29; i++) {
        const d = new Date();
        d.setDate(d.getDate() - i);
        const ds = d.toISOString().slice(0, 10);
        days.push({ date: ds, completed: logDates.has(ds), isToday: ds === today });
    }

    const scrollRef = useRef<HTMLDivElement>(null);
    const isDragging = useRef(false);
    const startX = useRef(0);
    const scrollLeft = useRef(0);

    const onMouseDown = (e: React.MouseEvent) => {
        isDragging.current = true;
        startX.current = e.pageX - (scrollRef.current?.offsetLeft ?? 0);
        scrollLeft.current = scrollRef.current?.scrollLeft ?? 0;
        if (scrollRef.current) scrollRef.current.style.cursor = 'grabbing';
    };

    const onMouseMove = (e: React.MouseEvent) => {
        if (!isDragging.current || !scrollRef.current) return;
        e.preventDefault();
        const x = e.pageX - scrollRef.current.offsetLeft;
        const walk = (x - startX.current) * 1.5;
        scrollRef.current.scrollLeft = scrollLeft.current - walk;
    };

    const onMouseUp = () => {
        isDragging.current = false;
        if (scrollRef.current) scrollRef.current.style.cursor = 'grab';
    };

    return (
        <div className="space-y-3">
            <div className="flex items-center justify-between">
                <div className="flex items-center gap-2">
                    <span className={`material-symbols-outlined text-[18px] ${HABIT_TYPE_COLORS[habit.type]}`}>
                        {HABIT_TYPE_ICONS[habit.type]}
                    </span>
                    <span className="font-semibold text-sm text-gray-900 dark:text-gray-100">{habit.habitName}</span>
                    <span className="text-[10px] px-1.5 py-0.5 rounded bg-gray-100 dark:bg-surface-dark-alt text-gray-500 dark:text-gray-400 font-medium">
                        {HABIT_TYPE_LABELS[habit.type]}
                    </span>
                </div>
                <span className="text-xs font-medium text-primary">
                    {completedDays}/30 days &bull; {pct}%
                </span>
            </div>
            <div
                ref={scrollRef}
                className="overflow-x-auto pb-2 pt-1 pl-1 no-scrollbar cursor-grab select-none"
                onMouseDown={onMouseDown}
                onMouseMove={onMouseMove}
                onMouseUp={onMouseUp}
                onMouseLeave={onMouseUp}
            >
                <div className="flex gap-1 min-w-max pr-1">
                    {days.map((day) => (
                        <div
                            key={day.date}
                            title={`${day.date}${day.completed ? ' ✓' : ''}`}
                            className={`w-[18px] h-[18px] rounded-sm transition-colors flex-shrink-0 ${
                                day.completed ? 'bg-primary' : 'bg-gray-200 dark:bg-gray-700'
                            } ${
                                day.isToday
                                    ? 'ring-2 ring-primary/50 ring-offset-1 ring-offset-white dark:ring-offset-surface-dark'
                                    : ''
                            }`}
                        />
                    ))}
                </div>
            </div>
        </div>
    );
}

interface HabitModalProps {
    habit: HabitResponseDto | null;
    onSave: (req: HabitRequestDto) => Promise<void>;
    onDelete?: () => Promise<void>;
    onClose: () => void;
}

function HabitModal({ habit, onSave, onDelete, onClose }: HabitModalProps) {
    const [name, setName] = useState(habit?.habitName ?? '');
    const [type, setType] = useState<HabitType>(habit?.type ?? 'INTELLECTUAL');
    const [saving, setSaving] = useState(false);

    const handleSave = async () => {
        if (name.trim().length < 3) return;
        setSaving(true);
        await onSave({ habitName: name.trim(), type });
        setSaving(false);
    };

    return (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40 backdrop-blur-sm" onClick={onClose}>
            <div
                className="bg-white dark:bg-surface-dark border border-gray-200 dark:border-border-dark rounded-xl shadow-2xl w-full max-w-md mx-4 p-6"
                onClick={(e) => e.stopPropagation()}
            >
                <div className="flex items-center justify-between mb-6">
                    <h3 className="text-lg font-bold text-gray-900 dark:text-white">
                        {habit ? 'Edit Habit' : 'New Habit'}
                    </h3>
                    <button onClick={onClose} className="text-gray-400 hover:text-gray-600 dark:hover:text-gray-300">
                        <span className="material-symbols-outlined">close</span>
                    </button>
                </div>

                <div className="flex flex-col gap-4">
                    <div>
                        <label className="text-sm font-semibold text-gray-700 dark:text-gray-300 mb-1 block">Habit Name</label>
                        <input
                            type="text"
                            value={name}
                            onChange={(e) => setName(e.target.value)}
                            onKeyDown={(e) => e.key === 'Enter' && handleSave()}
                            placeholder="e.g. LeetCode, Exercise, Read Docs..."
                            autoFocus
                            className="w-full px-3 py-2.5 rounded-lg border border-gray-200 dark:border-border-dark bg-white dark:bg-surface-dark-alt text-sm text-gray-900 dark:text-gray-100 placeholder:text-gray-400 outline-none focus:border-primary focus:ring-1 focus:ring-primary"
                        />
                    </div>
                    <div>
                        <label className="text-sm font-semibold text-gray-700 dark:text-gray-300 mb-1 block">Category</label>
                        <div className="flex gap-2">
                            {(['FITNESS', 'MENTAL_WELLBEING', 'INTELLECTUAL'] as HabitType[]).map((t) => (
                                <button
                                    key={t}
                                    type="button"
                                    onClick={() => setType(t)}
                                    className={`flex items-center gap-1.5 px-3 py-2 rounded-lg text-xs font-bold transition-all border ${
                                        type === t
                                            ? 'bg-primary/10 text-primary border-primary/20'
                                            : 'text-gray-500 dark:text-gray-400 border-gray-200 dark:border-border-dark hover:border-primary/40'
                                    }`}
                                >
                                    <span className={`material-symbols-outlined text-[16px] ${HABIT_TYPE_COLORS[t]}`}>
                                        {HABIT_TYPE_ICONS[t]}
                                    </span>
                                    {HABIT_TYPE_LABELS[t]}
                                </button>
                            ))}
                        </div>
                    </div>
                </div>

                <div className="flex items-center justify-between mt-6 pt-4 border-t border-gray-200 dark:border-border-dark">
                    {habit && onDelete ? (
                        <button
                            onClick={onDelete}
                            className="text-red-500 text-sm font-medium hover:underline"
                        >
                            Delete Habit
                        </button>
                    ) : (
                        <div />
                    )}
                    <div className="flex gap-2">
                        <button
                            onClick={onClose}
                            className="px-4 py-2 rounded-lg border border-gray-200 dark:border-border-dark text-sm font-medium text-gray-600 dark:text-gray-400 hover:bg-gray-50 dark:hover:bg-surface-dark-alt transition-colors"
                        >
                            Cancel
                        </button>
                        <button
                            onClick={handleSave}
                            disabled={name.trim().length < 3 || saving}
                            className="px-4 py-2 rounded-lg bg-primary text-white text-sm font-semibold hover:bg-primary/90 transition-all shadow-lg shadow-primary/20 disabled:opacity-50"
                        >
                            {saving ? 'Saving...' : habit ? 'Update' : 'Create'}
                        </button>
                    </div>
                </div>
            </div>
        </div>
    );
}
