import React from 'react';
import { Link } from 'react-router-dom';
import { DevHubLogo } from './DevHubLogo';

interface NavBarProps {
    ctaLabel?: string;
    ctaHref?: string;
}

export function NavBar({ ctaLabel = 'Sign In', ctaHref = '/login' }: NavBarProps) {
    return (
        <header className="flex items-center justify-between whitespace-nowrap border-b border-solid border-slate-200 dark:border-[#283339] px-6 md:px-10 py-3 bg-white dark:bg-[#0d1117] sticky top-0 z-50">
            <Link to="/" className="flex items-center">
                <DevHubLogo size="sm" />
            </Link>
            <div className="flex flex-1 justify-end gap-8">
                <Link
                    to={ctaHref}
                    className="flex min-w-[84px] cursor-pointer items-center justify-center rounded-lg h-10 px-4 bg-primary text-white text-sm font-bold leading-normal tracking-[0.015em] hover:bg-primary/90 transition-all"
                >
                    {ctaLabel}
                </Link>
            </div>
        </header>
    );
}
