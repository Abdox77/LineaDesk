import React, { useState } from 'react';
import type { TaskResponseDto, TaskState, TaskImportance, TaskRequestDto } from '../api/types';
import type { ProjectMemberResponseDto } from '../api/types';

interface TaskModalProps {
    projectId: number;
    task: TaskResponseDto | null;
    defaultState: TaskState;
    members?: ProjectMemberResponseDto[];
    onSave: (data: TaskRequestDto) => Promise<void>;
    onClose: () => void;
}

const IMPORTANCE_OPTIONS: { value: TaskImportance; label: string }[] = [
    { value: 'NORMAL', label: 'Normal' },
    { value: 'MEDIUM', label: 'Medium' },
    { value: 'IMPORTANT', label: 'Important' },
    { value: 'CRUCIAL', label: 'Crucial' },
];

const STATE_OPTIONS: { value: TaskState; label: string }[] = [
    { value: 'PENDING', label: 'Todo' },
    { value: 'IN_PROGRESS', label: 'In Progress' },
    { value: 'FINISHED', label: 'Done' },
];

export function TaskModal({ projectId, task, defaultState, members = [], onSave, onClose }: TaskModalProps) {
    const isEdit = task !== null;
    const [taskName, setTaskName] = useState(task?.taskName ?? '');
    const [description, setDescription] = useState(task?.description ?? '');
    const [duration, setDuration] = useState(task?.duration ?? 0);
    const [state, setState] = useState<TaskState>(task?.state ?? defaultState);
    const [importance, setImportance] = useState<TaskImportance>(task?.importance ?? 'NORMAL');
    const [dueDate, setDueDate] = useState(task?.dueDate ?? '');
    const [assigneeId, setAssigneeId] = useState<number | null>(task?.assigneeId ?? null);
    const [saving, setSaving] = useState(false);
    const [error, setError] = useState('');

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        if (taskName.trim().length < 3) {
            setError('Task name must be at least 3 characters');
            return;
        }
        setSaving(true);
        setError('');
        try {
            await onSave({
                taskName: taskName.trim(),
                projectId,
                description: description.trim(),
                duration,
                state,
                importance,
                dueDate: dueDate || null,
                assigneeId: assigneeId ?? null,
            });
        } catch (err: any) {
            const msg = err?.message || 'Failed to save task';
            setError(msg);
            setSaving(false);
        }
    };

    return (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50 backdrop-blur-sm" onClick={onClose}>
            <div
                className="bg-white dark:bg-[#161b22] border border-slate-200 dark:border-slate-700 rounded-xl shadow-2xl w-full max-w-md mx-4"
                onClick={(e) => e.stopPropagation()}
            >
                <div className="flex items-center justify-between p-6 pb-0">
                    <h2 className="text-lg font-bold text-slate-900 dark:text-white">
                        {isEdit ? 'Edit Task' : 'New Task'}
                    </h2>
                    <button onClick={onClose} className="text-slate-400 hover:text-slate-600 dark:hover:text-white transition-colors">
                        <span className="material-symbols-outlined">close</span>
                    </button>
                </div>

                <form onSubmit={handleSubmit} className="p-6 flex flex-col gap-5">
                    {error && (
                        <div className="bg-red-50 dark:bg-red-900/20 border border-red-200 dark:border-red-800 text-red-700 dark:text-red-300 px-4 py-2 rounded-lg text-sm">
                            {error}
                        </div>
                    )}

                    <div className="flex flex-col gap-1.5">
                        <label className="text-sm font-semibold text-slate-700 dark:text-slate-300">Task Name</label>
                        <input
                            className="form-input w-full rounded-lg border border-slate-300 dark:border-slate-700 bg-slate-50 dark:bg-[#0d1117] h-10 px-3 text-sm text-slate-900 dark:text-white placeholder:text-slate-400 focus:outline-none focus:border-primary focus:ring-1 focus:ring-primary"
                            value={taskName}
                            onChange={(e) => setTaskName(e.target.value)}
                            placeholder="Enter task name"
                            required
                            minLength={3}
                            autoFocus
                        />
                    </div>

                    <div className="flex flex-col gap-1.5">
                        <label className="text-sm font-semibold text-slate-700 dark:text-slate-300">Description</label>
                        <textarea
                            className="form-input w-full rounded-lg border border-slate-300 dark:border-slate-700 bg-slate-50 dark:bg-[#0d1117] px-3 py-2 text-sm text-slate-900 dark:text-white placeholder:text-slate-400 focus:outline-none focus:border-primary focus:ring-1 focus:ring-primary resize-none"
                            value={description}
                            onChange={(e) => setDescription(e.target.value)}
                            placeholder="Task description (optional)"
                            rows={3}
                        />
                    </div>

                    <div className={`grid gap-4 ${isEdit ? 'grid-cols-2' : ''}`}>
                        <div className="flex flex-col gap-1.5">
                            <label className="text-sm font-semibold text-slate-700 dark:text-slate-300">Duration (min)</label>
                            <input
                                type="number"
                                className="form-input w-full rounded-lg border border-slate-300 dark:border-slate-700 bg-slate-50 dark:bg-[#0d1117] h-10 px-3 text-sm text-slate-900 dark:text-white focus:outline-none focus:border-primary focus:ring-1 focus:ring-primary"
                                value={duration}
                                onChange={(e) => setDuration(Math.max(0, Number(e.target.value)))}
                                min={0}
                            />
                        </div>

                        {isEdit && (
                            <div className="flex flex-col gap-1.5">
                                <label className="text-sm font-semibold text-slate-700 dark:text-slate-300">Status</label>
                                <select
                                    className="form-select w-full rounded-lg border border-slate-300 dark:border-slate-700 bg-slate-50 dark:bg-[#0d1117] h-10 px-3 text-sm text-slate-900 dark:text-white focus:outline-none focus:border-primary focus:ring-1 focus:ring-primary"
                                    value={state}
                                    onChange={(e) => setState(e.target.value as TaskState)}
                                >
                                    {STATE_OPTIONS.map((o) => (
                                        <option key={o.value} value={o.value}>{o.label}</option>
                                    ))}
                                </select>
                            </div>
                        )}
                    </div>

                    <div className="flex flex-col gap-1.5">
                        <label className="text-sm font-semibold text-slate-700 dark:text-slate-300">Due Date (optional)</label>
                        <input
                            type="date"
                            className="form-input w-full rounded-lg border border-slate-300 dark:border-slate-700 bg-slate-50 dark:bg-[#0d1117] h-10 px-3 text-sm text-slate-900 dark:text-white focus:outline-none focus:border-primary focus:ring-1 focus:ring-primary"
                            value={dueDate}
                            onChange={(e) => setDueDate(e.target.value)}
                        />
                    </div>

                    <div className="flex flex-col gap-1.5">
                        <label className="text-sm font-semibold text-slate-700 dark:text-slate-300">Importance</label>
                        <div className="flex gap-2">
                            {IMPORTANCE_OPTIONS.map((o) => (
                                <button
                                    key={o.value}
                                    type="button"
                                    onClick={() => setImportance(o.value)}
                                    className={`flex-1 py-2 rounded-lg text-xs font-bold border transition-all ${
                                        importance === o.value
                                            ? 'border-primary bg-primary/10 text-primary'
                                            : 'border-slate-200 dark:border-slate-700 text-slate-500 hover:border-primary/50'
                                    }`}
                                >
                                    {o.label}
                                </button>
                            ))}
                        </div>
                    </div>

                    {members.length > 0 && (
                        <div className="flex flex-col gap-1.5">
                            <label className="text-sm font-semibold text-slate-700 dark:text-slate-300">Assign To</label>
                            <select
                                className="form-select w-full rounded-lg border border-slate-300 dark:border-slate-700 bg-slate-50 dark:bg-[#0d1117] h-10 px-3 text-sm text-slate-900 dark:text-white focus:outline-none focus:border-primary focus:ring-1 focus:ring-primary"
                                value={assigneeId ?? ''}
                                onChange={(e) => setAssigneeId(e.target.value ? Number(e.target.value) : null)}
                            >
                                <option value="">Unassigned</option>
                                {members.map((m) => (
                                    <option key={m.userId} value={m.userId}>{m.username}</option>
                                ))}
                            </select>
                        </div>
                    )}

                    <div className="flex gap-3 pt-2">
                        <button
                            type="button"
                            onClick={onClose}
                            className="flex-1 py-2.5 rounded-lg border border-slate-200 dark:border-slate-700 text-sm font-semibold hover:bg-slate-50 dark:hover:bg-slate-800 transition-colors"
                        >
                            Cancel
                        </button>
                        <button
                            type="submit"
                            disabled={saving}
                            className="flex-1 py-2.5 rounded-lg bg-primary text-white text-sm font-semibold hover:bg-primary/90 transition-all shadow-lg shadow-primary/20 disabled:opacity-60"
                        >
                            {saving ? 'Saving...' : isEdit ? 'Update Task' : 'Create Task'}
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
}
