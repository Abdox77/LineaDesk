import React from 'react';

interface LineaDeskLogoProps {
    size?: 'sm' | 'md' | 'lg';
    showText?: boolean;
    className?: string;
}

export function LineaDeskLogo({ size = 'md', showText = true, className = '' }: LineaDeskLogoProps) {
    const iconSizes = {
        sm: 'text-[20px]',
        md: 'text-[24px]',
        lg: 'text-[28px]',
    };

    const containerSizes = {
        sm: 'p-1',
        md: 'p-1.5',
        lg: 'p-2',
    };

    const textSizes = {
        sm: 'text-base',
        md: 'text-lg',
        lg: 'text-xl',
    };

    return (
        <div className={`flex items-center gap-3 ${className}`}>
            <div className={`bg-primary/10 rounded-lg ${containerSizes[size]}`}>
                <span className={`material-symbols-outlined text-primary ${iconSizes[size]}`}>
                    terminal
                </span>
            </div>
            {showText && (
                <span className={`font-bold ${textSizes[size]} tracking-tight text-gray-900 dark:text-white`}>
                    LineaDesk
                </span>
            )}
        </div>
    );
}
