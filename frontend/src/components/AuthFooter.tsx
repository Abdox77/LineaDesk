import React from 'react';

export function AuthFooter() {
    return (
        <footer className="py-8 px-10 border-t border-slate-200 dark:border-[#21262d] flex flex-col md:flex-row justify-between items-center gap-4">
            <div className="flex items-center gap-4 text-xs text-slate-500 dark:text-[#484f58]">
                <span>© 2026 Developer Hub, Inc.</span>
                <a className="hover:text-primary transition-colors" href="#">Terms</a>
                <a className="hover:text-primary transition-colors" href="#">Privacy</a>
                <a className="hover:text-primary transition-colors" href="#">Cookies</a>
            </div>
            <div className="flex items-center gap-4">
                <a className="text-slate-400 dark:text-[#484f58] hover:text-primary transition-colors" href="#">
                    <span className="material-symbols-outlined !text-[18px]">language</span>
                </a>
                <a className="text-slate-400 dark:text-[#484f58] hover:text-primary transition-colors" href="#">
                    <span className="material-symbols-outlined !text-[18px]">help_outline</span>
                </a>
            </div>
        </footer>
    );
}
