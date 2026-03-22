import React from 'react';
import { Link, useLocation } from 'react-router-dom';
import { LineaDeskLogo } from './LineaDeskLogo';
import { useTheme } from './ThemeProvider';

interface SidebarProps {
    displayName?: string;
}

const navItems = [
    { label: 'Dashboard', icon: 'pie_chart', href: '/dashboard' },
    { label: 'Projects', icon: 'folder', href: '/projects' },
    { label: 'Journal', icon: 'edit_note', href: '/journal' },
    { label: 'Habits', icon: 'check_circle', href: '/habits' },
];

const settingsItems = [
    { label: 'Settings', icon: 'settings', href: '/settings' },
];

export function Sidebar({ displayName = 'My Space' }: SidebarProps) {
    const location = useLocation();
    const { theme, toggleTheme } = useTheme();

    const handleSignOut = () => {
        localStorage.removeItem('token');
        window.location.href = '/login';
    };

    const isActive = (href: string) =>
        location.pathname === href || location.pathname.startsWith(href + '/');

    return (
        <aside className="w-20 lg:w-64 flex-shrink-0 flex flex-col bg-surface-light dark:bg-surface-dark border-r border-border-light dark:border-border-dark transition-all duration-300">
            <div className="flex flex-col gap-6 p-4">
                <div className="flex items-center gap-3 px-2">
                    <LineaDeskLogo size="md" showText={false} className="lg:hidden" />
                    <LineaDeskLogo size="md" className="hidden lg:flex" />
                </div>

                <nav className="flex flex-col gap-1">
                    {navItems.map((item) => {
                        const active = isActive(item.href);
                        return (
                            <Link
                                key={item.href}
                                to={item.href}
                                className={`flex items-center gap-3 px-3 py-2.5 rounded-lg font-medium transition-all ${
                                    active
                                        ? 'bg-primary/10 dark:bg-primary/20 text-primary dark:text-white border border-transparent dark:border-border-dark'
                                        : 'text-gray-500 dark:text-text-secondary hover:bg-gray-100 dark:hover:bg-surface-dark-alt border border-transparent'
                                }`}
                            >
                                <span
                                    className={`material-symbols-outlined text-[24px] transition-colors ${
                                        active ? 'text-primary' : 'group-hover:text-primary'
                                    }`}
                                >
                                    {item.icon}
                                </span>
                                <span className={`text-sm hidden lg:block ${active ? 'font-bold' : 'font-medium'}`}>
                                    {item.label}
                                </span>
                            </Link>
                        );
                    })}
                </nav>
            </div>

            <div className="mt-auto p-4 border-t border-border-light dark:border-border-dark">
                <button
                    onClick={toggleTheme}
                    className="flex items-center gap-3 px-3 py-2.5 rounded-lg font-medium transition-colors group text-gray-500 dark:text-text-secondary hover:bg-gray-100 dark:hover:bg-surface-dark-alt w-full mb-1"
                >
                    <span className="material-symbols-outlined text-[24px] group-hover:text-primary transition-colors">
                        {theme === 'dark' ? 'light_mode' : 'dark_mode'}
                    </span>
                    <span className="text-sm hidden lg:block">
                        {theme === 'dark' ? 'Light Mode' : 'Dark Mode'}
                    </span>
                </button>

                {settingsItems.map((item) => {
                    const active = isActive(item.href);
                    return (
                        <Link
                            key={item.href}
                            to={item.href}
                            className={`flex items-center gap-3 px-3 py-2.5 rounded-lg font-medium transition-colors group ${
                                active
                                    ? 'bg-primary/10 text-primary dark:text-sky-400'
                                    : 'text-gray-500 dark:text-text-secondary hover:bg-gray-100 dark:hover:bg-surface-dark-alt'
                            }`}
                        >
                            <span className="material-symbols-outlined text-[24px] group-hover:text-primary transition-colors">
                                {item.icon}
                            </span>
                            <span className="text-sm hidden lg:block">{item.label}</span>
                        </Link>
                    );
                })}

                <div className="mt-4 flex items-center gap-3 px-3">
                    <div className="size-8 rounded-full bg-gradient-to-br from-primary to-sky-300 flex-shrink-0 border border-transparent dark:border-border-dark" />
                    <div className="hidden lg:block min-w-0">
                        <p className="text-xs font-bold text-gray-900 dark:text-gray-100 truncate">
                            {displayName}
                        </p>
                        <button
                            onClick={handleSignOut}
                            className="text-[10px] text-gray-500 dark:text-text-secondary hover:text-primary transition-colors"
                        >
                            Sign Out
                        </button>
                    </div>
                </div>
            </div>
        </aside>
    );
}
