import React, { useState } from 'react';
import { Sidebar } from '../components/Sidebar';
import { useAuth } from '../auth/AuthContext';
import { useToast } from '../components/ToastProvider';
import { useTheme } from '../components/ThemeProvider';
import { updateProfile, changePassword } from '../api/endpoints';
import type { PomodoroSettings } from '../api/types';

const DEFAULT_POMODORO: PomodoroSettings = {
    focusMinutes: 25,
    shortBreakMinutes: 5,
    longBreakMinutes: 15,
    sessionsBeforeLongBreak: 4,
};

function loadPomodoroSettings(): PomodoroSettings {
    try {
        const raw = localStorage.getItem('pomodoroSettings');
        if (raw) return JSON.parse(raw);
    } catch {}
    return DEFAULT_POMODORO;
}

export function Settings() {
    const { user, logout, updateUser } = useAuth();
    const { showToast } = useToast();
    const { theme, toggleTheme } = useTheme();
    const displayName = user?.username ?? 'Developer';

    const [pomodoro, setPomodoro] = useState<PomodoroSettings>(loadPomodoroSettings);

    const [editingProfile, setEditingProfile] = useState(false);
    const [profileName, setProfileName] = useState(displayName);
    const [profileEmail, setProfileEmail] = useState(user?.email ?? '');
    const [profileSaving, setProfileSaving] = useState(false);

    const [editingPassword, setEditingPassword] = useState(false);
    const [currentPassword, setCurrentPassword] = useState('');
    const [newPassword, setNewPassword] = useState('');
    const [confirmPassword, setConfirmPassword] = useState('');
    const [passwordSaving, setPasswordSaving] = useState(false);

    const handleSaveProfile = async () => {
        const dto: Record<string, string> = {};
        if (profileName !== displayName) dto.username = profileName;
        if (profileEmail !== user?.email) dto.email = profileEmail;
        if (Object.keys(dto).length === 0) {
            setEditingProfile(false);
            return;
        }
        setProfileSaving(true);
        try {
            const updated = await updateProfile(dto);
            updateUser(updated);
            showToast('Profile updated');
            setEditingProfile(false);
        } catch (e: any) {
            showToast(e?.message ?? 'Failed to update profile');
        } finally {
            setProfileSaving(false);
        }
    };

    const handleChangePassword = async () => {
        if (newPassword !== confirmPassword) {
            showToast('Passwords do not match');
            return;
        }
        if (newPassword.length < 6) {
            showToast('Password must be at least 6 characters');
            return;
        }
        setPasswordSaving(true);
        try {
            await changePassword({ currentPassword, newPassword });
            showToast('Password changed');
            setEditingPassword(false);
            setCurrentPassword('');
            setNewPassword('');
            setConfirmPassword('');
        } catch (e: any) {
            showToast(e?.message ?? 'Failed to change password');
        } finally {
            setPasswordSaving(false);
        }
    };

    const updatePomodoro = (patch: Partial<PomodoroSettings>) => {
        const next = { ...pomodoro, ...patch };
        setPomodoro(next);
        localStorage.setItem('pomodoroSettings', JSON.stringify(next));
        showToast('Settings saved');
    };

    const resetPomodoro = () => {
        setPomodoro(DEFAULT_POMODORO);
        localStorage.setItem('pomodoroSettings', JSON.stringify(DEFAULT_POMODORO));
        showToast('Timer reset to defaults');
    };

    return (
        <div className="flex h-screen w-full bg-background-light dark:bg-background-dark font-display text-gray-900 dark:text-gray-200">
            <Sidebar displayName={`${displayName}'s Space`} />

            <div className="flex-1 flex flex-col overflow-hidden">
                <header className="flex items-center px-8 py-4 border-b border-gray-200 dark:border-border-dark bg-white/50 dark:bg-surface-dark/50 backdrop-blur-sm shrink-0">
                    <h1 className="text-lg font-bold text-gray-900 dark:text-white">Settings</h1>
                </header>

                <main className="flex-1 overflow-y-auto no-scrollbar px-8 py-8">
                    <div className="max-w-2xl mx-auto flex flex-col gap-8">

                        {/* Profile */}
                        <section className="rounded-xl border border-gray-200 dark:border-border-dark bg-white dark:bg-surface-dark p-6">
                            <h2 className="text-sm font-bold text-gray-900 dark:text-white uppercase tracking-wider mb-5 flex items-center gap-2">
                                <span className="material-symbols-outlined text-[18px] text-primary">person</span>
                                Profile
                            </h2>
                            <div className="flex items-center gap-5 mb-6">
                                <div className="size-16 rounded-full bg-gradient-to-br from-primary to-sky-300 flex items-center justify-center text-white text-2xl font-bold flex-shrink-0">
                                    {displayName.slice(0, 1).toUpperCase()}
                                </div>
                                {editingProfile ? (
                                    <div className="flex flex-col gap-3 flex-1">
                                        <div>
                                            <label className="text-xs text-gray-500 dark:text-gray-400 mb-1 block">Username</label>
                                            <input
                                                type="text"
                                                value={profileName}
                                                onChange={(e) => setProfileName(e.target.value)}
                                                className="w-full px-3 py-2 rounded-lg border border-gray-200 dark:border-border-dark bg-gray-50 dark:bg-surface-dark-alt text-sm text-gray-900 dark:text-white focus:outline-none focus:ring-2 focus:ring-primary/40"
                                            />
                                        </div>
                                        <div>
                                            <label className="text-xs text-gray-500 dark:text-gray-400 mb-1 block">Email</label>
                                            <input
                                                type="email"
                                                value={profileEmail}
                                                onChange={(e) => setProfileEmail(e.target.value)}
                                                className="w-full px-3 py-2 rounded-lg border border-gray-200 dark:border-border-dark bg-gray-50 dark:bg-surface-dark-alt text-sm text-gray-900 dark:text-white focus:outline-none focus:ring-2 focus:ring-primary/40"
                                            />
                                        </div>
                                        <div className="flex gap-2 mt-1">
                                            <button
                                                onClick={handleSaveProfile}
                                                disabled={profileSaving}
                                                className="px-4 py-2 rounded-lg bg-primary text-white text-sm font-semibold hover:bg-primary/90 transition-colors disabled:opacity-50"
                                            >
                                                {profileSaving ? 'Saving…' : 'Save'}
                                            </button>
                                            <button
                                                onClick={() => { setEditingProfile(false); setProfileName(displayName); setProfileEmail(user?.email ?? ''); }}
                                                className="px-4 py-2 rounded-lg border border-gray-200 dark:border-border-dark text-sm font-medium hover:bg-gray-50 dark:hover:bg-surface-dark-alt transition-colors"
                                            >
                                                Cancel
                                            </button>
                                        </div>
                                    </div>
                                ) : (
                                    <div className="flex-1 flex items-center justify-between">
                                        <div>
                                            <p className="text-lg font-bold text-gray-900 dark:text-white">{displayName}</p>
                                            <p className="text-sm text-gray-500 dark:text-gray-400">{user?.email ?? '—'}</p>
                                        </div>
                                        <button
                                            onClick={() => setEditingProfile(true)}
                                            className="flex items-center gap-1.5 px-3 py-1.5 rounded-lg border border-gray-200 dark:border-border-dark text-sm font-medium text-gray-600 dark:text-gray-300 hover:bg-gray-50 dark:hover:bg-surface-dark-alt transition-colors"
                                        >
                                            <span className="material-symbols-outlined text-[16px]">edit</span>
                                            Edit
                                        </button>
                                    </div>
                                )}
                            </div>
                        </section>

                        {/* Password */}
                        <section className="rounded-xl border border-gray-200 dark:border-border-dark bg-white dark:bg-surface-dark p-6">
                            <h2 className="text-sm font-bold text-gray-900 dark:text-white uppercase tracking-wider mb-5 flex items-center gap-2">
                                <span className="material-symbols-outlined text-[18px] text-primary">lock</span>
                                Password
                            </h2>
                            {editingPassword ? (
                                <div className="flex flex-col gap-3 max-w-sm">
                                    <div>
                                        <label className="text-xs text-gray-500 dark:text-gray-400 mb-1 block">Current password</label>
                                        <input
                                            type="password"
                                            value={currentPassword}
                                            onChange={(e) => setCurrentPassword(e.target.value)}
                                            className="w-full px-3 py-2 rounded-lg border border-gray-200 dark:border-border-dark bg-gray-50 dark:bg-surface-dark-alt text-sm text-gray-900 dark:text-white focus:outline-none focus:ring-2 focus:ring-primary/40"
                                        />
                                    </div>
                                    <div>
                                        <label className="text-xs text-gray-500 dark:text-gray-400 mb-1 block">New password</label>
                                        <input
                                            type="password"
                                            value={newPassword}
                                            onChange={(e) => setNewPassword(e.target.value)}
                                            className="w-full px-3 py-2 rounded-lg border border-gray-200 dark:border-border-dark bg-gray-50 dark:bg-surface-dark-alt text-sm text-gray-900 dark:text-white focus:outline-none focus:ring-2 focus:ring-primary/40"
                                        />
                                    </div>
                                    <div>
                                        <label className="text-xs text-gray-500 dark:text-gray-400 mb-1 block">Confirm new password</label>
                                        <input
                                            type="password"
                                            value={confirmPassword}
                                            onChange={(e) => setConfirmPassword(e.target.value)}
                                            className="w-full px-3 py-2 rounded-lg border border-gray-200 dark:border-border-dark bg-gray-50 dark:bg-surface-dark-alt text-sm text-gray-900 dark:text-white focus:outline-none focus:ring-2 focus:ring-primary/40"
                                        />
                                    </div>
                                    <div className="flex gap-2 mt-1">
                                        <button
                                            onClick={handleChangePassword}
                                            disabled={passwordSaving}
                                            className="px-4 py-2 rounded-lg bg-primary text-white text-sm font-semibold hover:bg-primary/90 transition-colors disabled:opacity-50"
                                        >
                                            {passwordSaving ? 'Saving…' : 'Update Password'}
                                        </button>
                                        <button
                                            onClick={() => { setEditingPassword(false); setCurrentPassword(''); setNewPassword(''); setConfirmPassword(''); }}
                                            className="px-4 py-2 rounded-lg border border-gray-200 dark:border-border-dark text-sm font-medium hover:bg-gray-50 dark:hover:bg-surface-dark-alt transition-colors"
                                        >
                                            Cancel
                                        </button>
                                    </div>
                                </div>
                            ) : (
                                <div className="flex items-center justify-between">
                                    <div>
                                        <p className="text-sm font-medium text-gray-900 dark:text-gray-100">Change your password</p>
                                        <p className="text-xs text-gray-500 dark:text-gray-400">Use a strong password you don't use elsewhere</p>
                                    </div>
                                    <button
                                        onClick={() => setEditingPassword(true)}
                                        className="flex items-center gap-1.5 px-3 py-1.5 rounded-lg border border-gray-200 dark:border-border-dark text-sm font-medium text-gray-600 dark:text-gray-300 hover:bg-gray-50 dark:hover:bg-surface-dark-alt transition-colors"
                                    >
                                        <span className="material-symbols-outlined text-[16px]">edit</span>
                                        Change
                                    </button>
                                </div>
                            )}
                        </section>

                        {/* Appearance */}
                        <section className="rounded-xl border border-gray-200 dark:border-border-dark bg-white dark:bg-surface-dark p-6">
                            <h2 className="text-sm font-bold text-gray-900 dark:text-white uppercase tracking-wider mb-5 flex items-center gap-2">
                                <span className="material-symbols-outlined text-[18px] text-primary">palette</span>
                                Appearance
                            </h2>
                            <div className="flex items-center justify-between">
                                <div>
                                    <p className="text-sm font-medium text-gray-900 dark:text-gray-100">Theme</p>
                                    <p className="text-xs text-gray-500 dark:text-gray-400">Switch between light and dark mode</p>
                                </div>
                                <button
                                    onClick={toggleTheme}
                                    className="flex items-center gap-2 px-4 py-2 rounded-lg border border-gray-200 dark:border-border-dark hover:bg-gray-50 dark:hover:bg-surface-dark-alt transition-colors text-sm font-medium"
                                >
                                    <span className="material-symbols-outlined text-[18px]">
                                        {theme === 'dark' ? 'light_mode' : 'dark_mode'}
                                    </span>
                                    {theme === 'dark' ? 'Light' : 'Dark'}
                                </button>
                            </div>
                        </section>

                        {/* Pomodoro Timer */}
                        <section className="rounded-xl border border-gray-200 dark:border-border-dark bg-white dark:bg-surface-dark p-6">
                            <div className="flex items-center justify-between mb-5">
                                <h2 className="text-sm font-bold text-gray-900 dark:text-white uppercase tracking-wider flex items-center gap-2">
                                    <span className="material-symbols-outlined text-[18px] text-primary">timer</span>
                                    Focus Timer
                                </h2>
                                <button
                                    onClick={resetPomodoro}
                                    className="text-xs text-gray-400 hover:text-primary transition-colors font-medium"
                                >
                                    Reset to defaults
                                </button>
                            </div>
                            <div className="grid grid-cols-1 sm:grid-cols-2 gap-5">
                                <SettingNumber
                                    label="Focus duration"
                                    detail="Minutes per focus session"
                                    value={pomodoro.focusMinutes}
                                    min={1}
                                    max={120}
                                    onChange={(v) => updatePomodoro({ focusMinutes: v })}
                                />
                                <SettingNumber
                                    label="Short break"
                                    detail="Minutes between sessions"
                                    value={pomodoro.shortBreakMinutes}
                                    min={1}
                                    max={30}
                                    onChange={(v) => updatePomodoro({ shortBreakMinutes: v })}
                                />
                                <SettingNumber
                                    label="Long break"
                                    detail="Minutes after a cycle"
                                    value={pomodoro.longBreakMinutes}
                                    min={1}
                                    max={60}
                                    onChange={(v) => updatePomodoro({ longBreakMinutes: v })}
                                />
                                <SettingNumber
                                    label="Sessions per cycle"
                                    detail="Before a long break"
                                    value={pomodoro.sessionsBeforeLongBreak}
                                    min={1}
                                    max={10}
                                    onChange={(v) => updatePomodoro({ sessionsBeforeLongBreak: v })}
                                />
                            </div>
                        </section>

                        {/* Sign Out */}
                        <section className="rounded-xl border border-red-200 dark:border-red-900/30 bg-white dark:bg-surface-dark p-6">
                            <div className="flex items-center justify-between">
                                <div>
                                    <p className="text-sm font-medium text-gray-900 dark:text-gray-100">Sign out</p>
                                    <p className="text-xs text-gray-500 dark:text-gray-400">Log out of your account on this device</p>
                                </div>
                                <button
                                    onClick={logout}
                                    className="flex items-center gap-2 px-4 py-2 rounded-lg bg-red-50 dark:bg-red-900/20 text-red-600 dark:text-red-400 text-sm font-semibold hover:bg-red-100 dark:hover:bg-red-900/30 transition-colors"
                                >
                                    <span className="material-symbols-outlined text-[18px]">logout</span>
                                    Sign Out
                                </button>
                            </div>
                        </section>

                    </div>
                </main>
            </div>
        </div>
    );
}

function SettingNumber({ label, detail, value, min, max, onChange }: {
    label: string;
    detail: string;
    value: number;
    min: number;
    max: number;
    onChange: (v: number) => void;
}) {
    return (
        <div className="flex items-center justify-between gap-4 p-3 rounded-lg bg-gray-50 dark:bg-surface-dark-alt">
            <div className="min-w-0">
                <p className="text-sm font-medium text-gray-900 dark:text-gray-100">{label}</p>
                <p className="text-xs text-gray-500 dark:text-gray-400">{detail}</p>
            </div>
            <div className="flex items-center gap-1">
                <button
                    onClick={() => onChange(Math.max(min, value - 1))}
                    className="size-8 rounded-lg border border-gray-200 dark:border-border-dark flex items-center justify-center text-gray-500 hover:text-primary hover:border-primary transition-colors"
                >
                    <span className="material-symbols-outlined text-[18px]">remove</span>
                </button>
                <span className="w-10 text-center text-sm font-bold tabular-nums">{value}</span>
                <button
                    onClick={() => onChange(Math.min(max, value + 1))}
                    className="size-8 rounded-lg border border-gray-200 dark:border-border-dark flex items-center justify-center text-gray-500 hover:text-primary hover:border-primary transition-colors"
                >
                    <span className="material-symbols-outlined text-[18px]">add</span>
                </button>
            </div>
        </div>
    );
}
