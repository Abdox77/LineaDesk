import React from 'react';

interface EmptyStateProps {
    icon: string;
    title: string;
    description?: string;
    actionLabel?: string;
    onAction?: () => void;
}

export function EmptyState({ icon, title, description, actionLabel, onAction }: EmptyStateProps) {
    return (
        <div className="flex flex-col items-center justify-center py-16 gap-3">
            <span className="material-symbols-outlined text-[48px] text-gray-300 dark:text-gray-600">
                {icon}
            </span>
            <p className="text-lg font-medium text-gray-500 dark:text-gray-400">{title}</p>
            {description && (
                <p className="text-sm text-gray-400 dark:text-gray-500 max-w-sm text-center">{description}</p>
            )}
            {actionLabel && onAction && (
                <button
                    onClick={onAction}
                    className="mt-2 px-4 py-2 bg-primary text-white text-sm font-semibold rounded-lg hover:bg-primary-dark transition-colors shadow-lg shadow-primary/20"
                >
                    {actionLabel}
                </button>
            )}
        </div>
    );
}

export function SkeletonRow() {
    return (
        <div className="flex items-center gap-4 px-4 py-3 border-b border-border-light dark:border-border-dark animate-pulse">
            <div className="w-4 h-4 bg-gray-200 dark:bg-gray-700 rounded" />
            <div className="flex-1 flex flex-col gap-1.5">
                <div className="h-4 w-48 bg-gray-200 dark:bg-gray-700 rounded" />
                <div className="h-3 w-32 bg-gray-100 dark:bg-gray-800 rounded" />
            </div>
            <div className="h-6 w-20 bg-gray-200 dark:bg-gray-700 rounded-full" />
            <div className="h-4 w-12 bg-gray-200 dark:bg-gray-700 rounded" />
        </div>
    );
}

export function SkeletonCard() {
    return (
        <div className="flex flex-col bg-card-light dark:bg-card-dark rounded-xl border border-border-light dark:border-gray-800 shadow-sm p-5 gap-3 animate-pulse">
            <div className="flex items-start justify-between">
                <div className="flex items-center gap-3">
                    <div className="size-9 rounded-lg bg-gray-200 dark:bg-gray-700" />
                    <div className="flex flex-col gap-1.5">
                        <div className="h-4 w-32 bg-gray-200 dark:bg-gray-700 rounded" />
                        <div className="h-3 w-20 bg-gray-100 dark:bg-gray-800 rounded" />
                    </div>
                </div>
                <div className="h-5 w-16 bg-gray-200 dark:bg-gray-700 rounded-full" />
            </div>
            <div className="h-1 w-full bg-gray-100 dark:bg-gray-700 rounded-full" />
        </div>
    );
}

export function SkeletonStat() {
    return (
        <div className="p-4 rounded-xl border border-border-light dark:border-border-dark bg-card-light dark:bg-surface-dark flex flex-col gap-2 animate-pulse">
            <div className="h-3 w-16 bg-gray-200 dark:bg-gray-700 rounded" />
            <div className="h-7 w-12 bg-gray-200 dark:bg-gray-700 rounded" />
            <div className="h-3 w-24 bg-gray-100 dark:bg-gray-800 rounded" />
        </div>
    );
}

