import React from 'react';
import { Link } from 'react-router-dom';

interface NavBarProps {
    ctaLabel?: string;
    ctaHref?: string;
}

export function NavBar({ ctaLabel = 'Sign In', ctaHref = '/login' }: NavBarProps) {
    return (
        <header className="flex items-center justify-between whitespace-nowrap border-b border-solid border-slate-200 dark:border-[#283339] px-6 md:px-10 py-3 bg-white dark:bg-[#0d1117] sticky top-0 z-50">
            <div className="flex items-center gap-4 text-slate-900 dark:text-white">
                <div className="size-6 text-primary">
                    <svg fill="none" viewBox="0 0 48 48" xmlns="http://www.w3.org/2000/svg">
                        <g clipPath="url(#clip0_navbar)">
                            <path
                                clipRule="evenodd"
                                d="M47.2426 24L24 47.2426L0.757355 24L24 0.757355L47.2426 24ZM12.2426 21H35.7574L24 9.24264L12.2426 21Z"
                                fill="currentColor"
                                fillRule="evenodd"
                            />
                        </g>
                        <defs>
                            <clipPath id="clip0_navbar">
                                <rect fill="white" height="48" width="48" />
                            </clipPath>
                        </defs>
                    </svg>
                </div>
                <h2 className="text-slate-900 dark:text-white text-lg font-bold leading-tight tracking-[-0.015em] font-display">
                    Developer Hub
                </h2>
            </div>
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
