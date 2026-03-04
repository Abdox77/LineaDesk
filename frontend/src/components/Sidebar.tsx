import React from 'react';
import { Link, useLocation } from 'react-router-dom';
import { DevHubLogo } from './DevHubLogo';

interface SidebarProps {
    displayName?: string;
}

const navItems = [
    { label: 'Dashboard', icon: 'dashboard', href: '/dashboard', filled: true },
    { label: 'Projects', icon: 'folder_open', href: '/projects' },
    { label: 'Analytics', icon: 'timeline', href: '/analytics' },
    { label: 'Habits', icon: 'check_circle', href: '/habits' },
];

const settingsItems = [
    { label: 'Profile', icon: 'manage_accounts', href: '/profile' },
    { label: 'Preferences', icon: 'settings', href: '/preferences' },
];

export function Sidebar({ displayName = 'My Space' }: SidebarProps) {
    const location = useLocation();

    const handleSignOut = () => {
        localStorage.removeItem('token');
        window.location.href = '/login';
    };

    const isActive = (href: string) => location.pathname === href;

    return (
        <aside className="w-[260px] flex-shrink-0 flex flex-col bg-white dark:bg-[#1c2024] border-r border-border-light dark:border-gray-800 transition-colors duration-300">
            {/* Branding */}
            <div className="p-6 pb-2">
                <div className="mb-6">
                    <DevHubLogo size="md" />
                </div>

                {/* Workspace Switcher */}
                <button className="w-full flex items-center justify-between px-3 py-2 bg-background-light dark:bg-background-dark border border-border-light dark:border-gray-700 rounded-lg hover:border-primary/50 transition-colors group">
                    <div className="flex items-center gap-3">
                        <div className="size-6 rounded-full bg-gradient-to-br from-primary to-sky-300 flex-shrink-0" />
                        <span className="text-sm font-medium text-gray-700 dark:text-gray-200 truncate">
                            {displayName}
                        </span>
                    </div>
                    <span className="material-symbols-outlined text-gray-400 group-hover:text-primary text-[18px]">
                        unfold_more
                    </span>
                </button>
            </div>

            {/* Navigation Links */}
            <nav className="flex-1 px-4 py-4 flex flex-col gap-1 overflow-y-auto">
                <p className="px-3 text-xs font-semibold text-gray-400 dark:text-gray-500 uppercase tracking-wider mb-2">
                    Workspace
                </p>

                {navItems.map((item) => (
                    <Link
                        key={item.href}
                        to={item.href}
                        className={`flex items-center gap-3 px-3 py-2 rounded-lg font-medium transition-all ${
                            isActive(item.href)
                                ? 'bg-primary/10 text-primary dark:text-sky-400'
                                : 'text-gray-600 dark:text-gray-400 hover:bg-gray-100 dark:hover:bg-white/5 hover:text-gray-900 dark:hover:text-gray-200'
                        }`}
                    >
                        <span
                            className="material-symbols-outlined text-[20px]"
                            style={isActive(item.href) && item.filled ? { fontVariationSettings: "'FILL' 1" } : undefined}
                        >
                            {item.icon}
                        </span>
                        <span className="text-sm">{item.label}</span>
                    </Link>
                ))}

                <div className="mt-6">
                    <p className="px-3 text-xs font-semibold text-gray-400 dark:text-gray-500 uppercase tracking-wider mb-2">
                        Settings
                    </p>
                    {settingsItems.map((item) => (
                        <Link
                            key={item.href}
                            to={item.href}
                            className={`flex items-center gap-3 px-3 py-2 rounded-lg font-medium transition-all ${
                                isActive(item.href)
                                    ? 'bg-primary/10 text-primary dark:text-sky-400'
                                    : 'text-gray-600 dark:text-gray-400 hover:bg-gray-100 dark:hover:bg-white/5 hover:text-gray-900 dark:hover:text-gray-200'
                            }`}
                        >
                            <span className="material-symbols-outlined text-[20px]">{item.icon}</span>
                            <span className="text-sm">{item.label}</span>
                        </Link>
                    ))}
                </div>
            </nav>

            {/* Bottom Actions */}
            <div className="p-4 border-t border-border-light dark:border-gray-800">
                <button
                    onClick={handleSignOut}
                    className="w-full flex items-center justify-center gap-2 py-2 text-sm font-medium text-gray-500 hover:text-gray-900 dark:text-gray-400 dark:hover:text-white transition-colors"
                >
                    <span className="material-symbols-outlined text-[18px]">logout</span>
                    Sign Out
                </button>
            </div>
        </aside>
    );
}
