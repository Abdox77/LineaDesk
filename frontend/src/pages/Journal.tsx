import React, { useEffect, useState, useCallback, useRef } from 'react';
import { Sidebar } from '../components/Sidebar';
import { useAuth } from '../auth/AuthContext';
import { useToast } from '../components/ToastProvider';
import {
    fetchJournals,
    createJournal,
    createPage,
    updatePage,
    deletePage,
} from '../api/endpoints';
import type {
    JournalResponseDto,
    PageResponseDto,
} from '../api/types';

function formatDate(iso: string): string {
    const d = new Date(iso);
    return d.toLocaleDateString('en-US', { month: 'short', day: 'numeric', year: 'numeric' });
}

function formatDateLong(iso: string): string {
    const d = new Date(iso);
    return d.toLocaleDateString('en-US', { weekday: 'long', month: 'short', day: 'numeric', year: 'numeric' });
}

function excerpt(content: string, len = 80): string {
    if (!content) return '';
    const plain = content.replace(/[#*_>\-`~\[\]()]/g, '').trim();
    return plain.length > len ? plain.slice(0, len) + '…' : plain;
}

export function Journal() {
    const { user } = useAuth();
    const { showToast } = useToast();
    const displayName = user?.username ?? 'Developer';

    const [loading, setLoading] = useState(true);

    const [activeJournal, setActiveJournal] = useState<JournalResponseDto | null>(null);
    const [activePage, setActivePage] = useState<PageResponseDto | null>(null);

    const [title, setTitle] = useState('');
    const [content, setContent] = useState('');
    const [saving, setSaving] = useState(false);
    const saveTimerRef = useRef<ReturnType<typeof setTimeout> | null>(null);

    const [search, setSearch] = useState('');

    const [quickThought, setQuickThought] = useState('');

    const loadJournals = useCallback(async () => {
        try {
            const data = await fetchJournals();
            return data;
        } catch {
            showToast('Failed to load journals', 'error');
            return [];
        } finally {
            setLoading(false);
        }
    }, [showToast]);

    useEffect(() => {
        loadJournals().then((data) => {
            if (data.length > 0) {
                setActiveJournal(data[0]);
                const pages = data[0].pages ?? [];
                if (pages.length > 0) {
                    const sorted = [...pages].sort(
                        (a, b) => new Date(b.updatedAt).getTime() - new Date(a.updatedAt).getTime()
                    );
                    selectPage(sorted[0]);
                }
            }
        });
    }, []);

    const selectPage = (page: PageResponseDto) => {
        setActivePage(page);
        setTitle(page.title);
        setContent(page.content ?? '');
    };

    const scheduleAutoSave = useCallback(
        (pageId: number, newTitle: string, newContent: string) => {
            if (saveTimerRef.current) clearTimeout(saveTimerRef.current);
            saveTimerRef.current = setTimeout(async () => {
                try {
                    setSaving(true);
                    await updatePage(pageId, { title: newTitle || 'Untitled', content: newContent });
                    const fresh = await loadJournals();
                    if (activeJournal) {
                        const j = fresh.find((j) => j.id === activeJournal.id);
                        if (j) setActiveJournal(j);
                    }
                } catch {
                    showToast('Auto-save failed', 'error');
                } finally {
                    setSaving(false);
                }
            }, 1000);
        },
        [activeJournal, loadJournals, showToast]
    );

    const handleTitleChange = (val: string) => {
        setTitle(val);
        if (activePage) scheduleAutoSave(activePage.id, val, content);
    };

    const handleContentChange = (val: string) => {
        setContent(val);
        if (activePage) scheduleAutoSave(activePage.id, title, val);
    };

    const ensureJournal = async (): Promise<JournalResponseDto> => {
        if (activeJournal) return activeJournal;
        const j = await createJournal({ name: 'My Journal' });
        const fresh = await loadJournals();
        const found = fresh.find((x) => x.id === j.id) ?? j;
        setActiveJournal(found);
        return found;
    };

    const handleNewPage = async () => {
        try {
            const journal = await ensureJournal();
            const page = await createPage({
                title: 'Untitled',
                content: '',
                journalId: journal.id,
            });
            const fresh = await loadJournals();
            const j = fresh.find((x) => x.id === journal.id);
            if (j) setActiveJournal(j);
            selectPage(page);
            showToast('New entry created');
        } catch {
            showToast('Failed to create entry', 'error');
        }
    };

    const handleDeletePage = async (pageId: number) => {
        try {
            await deletePage(pageId);
            showToast('Entry deleted', 'warning');
            const fresh = await loadJournals();
            if (activeJournal) {
                const j = fresh.find((x) => x.id === activeJournal.id);
                if (j) {
                    setActiveJournal(j);
                    const pages = j.pages ?? [];
                    if (pages.length > 0) {
                        const sorted = [...pages].sort(
                            (a, b) => new Date(b.updatedAt).getTime() - new Date(a.updatedAt).getTime()
                        );
                        selectPage(sorted[0]);
                    } else {
                        setActivePage(null);
                        setTitle('');
                        setContent('');
                    }
                }
            }
        } catch {
            showToast('Failed to delete entry', 'error');
        }
    };

    const handleSaveQuickThought = async () => {
        if (!quickThought.trim()) return;
        try {
            const journal = await ensureJournal();
            await createPage({
                title: 'Quick Thought',
                content: quickThought.trim(),
                journalId: journal.id,
            });
            setQuickThought('');
            const fresh = await loadJournals();
            const j = fresh.find((x) => x.id === journal.id);
            if (j) setActiveJournal(j);
            showToast('Thought saved ⚡');
        } catch {
            showToast('Failed to save thought', 'error');
        }
    };

    const allPages: PageResponseDto[] = activeJournal?.pages ?? [];
    const sortedPages = [...allPages].sort(
        (a, b) => new Date(b.updatedAt).getTime() - new Date(a.updatedAt).getTime()
    );
    const filteredPages = search
        ? sortedPages.filter(
              (p) =>
                  p.title.toLowerCase().includes(search.toLowerCase()) ||
                  (p.content ?? '').toLowerCase().includes(search.toLowerCase())
          )
        : sortedPages;

    const quickThoughts = sortedPages.filter((p) => p.title === 'Quick Thought');

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

            
            <div className="w-80 flex flex-col border-r border-gray-200 dark:border-border-dark bg-gray-50/50 dark:bg-background-dark shrink-0">
                <div className="p-6 flex justify-between items-center">
                    <h2 className="font-bold text-lg text-gray-900 dark:text-white">Journal</h2>
                    <button
                        onClick={handleNewPage}
                        className="p-1.5 hover:bg-primary/10 rounded-md text-primary transition-colors"
                        title="New entry"
                    >
                        <span className="material-symbols-outlined">add_box</span>
                    </button>
                </div>

                <div className="px-4 mb-4">
                    <div className="relative">
                        <span className="material-symbols-outlined absolute left-3 top-1/2 -translate-y-1/2 text-gray-400 text-sm">
                            search
                        </span>
                        <input
                            type="text"
                            value={search}
                            onChange={(e) => setSearch(e.target.value)}
                            className="w-full bg-white dark:bg-surface-dark border border-gray-200 dark:border-border-dark rounded-lg pl-9 pr-4 py-2 text-xs focus:ring-1 focus:ring-primary outline-none text-gray-900 dark:text-gray-100 placeholder:text-gray-400"
                            placeholder="Search entries..."
                        />
                    </div>
                </div>

                <div className="flex-1 overflow-y-auto no-scrollbar px-3 space-y-1 pb-4">
                    {filteredPages.length === 0 && (
                        <div className="flex flex-col items-center justify-center py-12 text-gray-400 dark:text-gray-500">
                            <span className="material-symbols-outlined text-[32px] mb-2">edit_note</span>
                            <p className="text-xs">No entries yet</p>
                            <button
                                onClick={handleNewPage}
                                className="mt-3 text-xs text-primary font-bold hover:underline"
                            >
                                Create your first entry
                            </button>
                        </div>
                    )}

                    {filteredPages.map((page) => {
                        const isActive = activePage?.id === page.id;
                        return (
                            <button
                                key={page.id}
                                onClick={() => selectPage(page)}
                                className={`w-full text-left p-3 rounded-xl transition-all ${
                                    isActive
                                        ? 'bg-primary text-white shadow-lg shadow-primary/20'
                                        : 'hover:bg-gray-200 dark:hover:bg-surface-dark-alt cursor-pointer border border-transparent hover:border-gray-200 dark:hover:border-border-dark'
                                }`}
                            >
                                <p
                                    className={`text-[10px] font-medium ${
                                        isActive ? 'opacity-80' : 'text-gray-500'
                                    }`}
                                >
                                    {formatDate(page.updatedAt)}
                                </p>
                                <h3
                                    className={`font-bold text-sm truncate ${
                                        isActive ? '' : 'text-gray-800 dark:text-gray-200'
                                    }`}
                                >
                                    {page.title || 'Untitled'}
                                </h3>
                                <p
                                    className={`text-[11px] mt-1 truncate ${
                                        isActive ? 'opacity-70' : 'text-gray-500'
                                    }`}
                                >
                                    {excerpt(page.content)}
                                </p>
                            </button>
                        );
                    })}
                </div>
            </div>

            <main className="flex-1 flex flex-col bg-white dark:bg-background-dark relative overflow-hidden">
                
                <header className="h-14 border-b border-gray-200 dark:border-border-dark flex items-center justify-between px-8 shrink-0">
                    <div className="flex items-center gap-4">
                        <span className="text-xs text-gray-500 font-medium flex items-center gap-1">
                            <span className="material-symbols-outlined text-sm">
                                {saving ? 'sync' : 'cloud_done'}
                            </span>
                            {saving ? 'Saving…' : 'Saved'}
                        </span>
                    </div>
                    <div className="flex items-center gap-3">
                        {activePage && (
                            <button
                                onClick={() => handleDeletePage(activePage.id)}
                                className="p-2 hover:bg-red-50 dark:hover:bg-red-500/10 rounded-lg text-gray-400 hover:text-red-500 transition-colors"
                                title="Delete entry"
                            >
                                <span className="material-symbols-outlined text-[20px]">delete</span>
                            </button>
                        )}
                    </div>
                </header>

                {activePage ? (
                    <div className="flex-1 overflow-y-auto no-scrollbar">
                        <div className="max-w-3xl mx-auto px-8 py-12">
                            <div className="mb-8">
                                <div className="flex items-center gap-2 text-gray-500 text-xs font-medium mb-4 uppercase tracking-widest">
                                    <span className="material-symbols-outlined text-sm">calendar_today</span>
                                    {formatDateLong(activePage.createdAt)}
                                </div>

                                <input
                                    type="text"
                                    value={title}
                                    onChange={(e) => handleTitleChange(e.target.value)}
                                    className="w-full bg-transparent border-none p-0 text-4xl md:text-5xl font-black tracking-tight focus:ring-0 placeholder:text-gray-300 dark:placeholder:text-gray-700 outline-none mb-6 text-gray-900 dark:text-white"
                                    placeholder="Entry title…"
                                />

                                <div className="relative min-h-[500px]">
                                    <textarea
                                        value={content}
                                        onChange={(e) => handleContentChange(e.target.value)}
                                        className="w-full h-full min-h-[500px] bg-transparent border-none p-0 focus:ring-0 text-lg leading-relaxed text-gray-700 dark:text-gray-300 placeholder:text-gray-300 dark:placeholder:text-gray-700 resize-none font-medium outline-none"
                                        placeholder="Start writing your thoughts..."
                                    />
                                </div>
                            </div>
                        </div>
                    </div>
                ) : (
                    <div className="flex-1 flex flex-col items-center justify-center text-gray-400 dark:text-gray-500 gap-4">
                        <span className="material-symbols-outlined text-[64px]">edit_note</span>
                        <p className="text-lg font-semibold">No entry selected</p>
                        <button
                            onClick={handleNewPage}
                            className="mt-2 px-5 py-2.5 bg-primary text-white rounded-lg text-sm font-bold hover:bg-primary/90 transition-colors shadow-lg shadow-primary/20"
                        >
                            Create New Entry
                        </button>
                    </div>
                )}

                
                {activePage && (
                    <div className="absolute bottom-8 left-1/2 -translate-x-1/2 bg-white dark:bg-surface-dark border border-gray-200 dark:border-border-dark shadow-2xl rounded-full px-6 py-2 flex items-center gap-6 z-10">
                        <button className="text-gray-400 hover:text-primary transition-colors">
                            <span className="material-symbols-outlined">format_bold</span>
                        </button>
                        <button className="text-gray-400 hover:text-primary transition-colors">
                            <span className="material-symbols-outlined">format_italic</span>
                        </button>
                        <button className="text-gray-400 hover:text-primary transition-colors">
                            <span className="material-symbols-outlined">format_list_bulleted</span>
                        </button>
                        <div className="w-px h-6 bg-gray-200 dark:bg-border-dark" />
                        <button className="text-gray-400 hover:text-primary transition-colors">
                            <span className="material-symbols-outlined">link</span>
                        </button>
                        <button className="text-gray-400 hover:text-primary transition-colors">
                            <span className="material-symbols-outlined">code</span>
                        </button>
                    </div>
                )}
            </main>

            
            <aside className="w-72 flex-col border-l border-gray-200 dark:border-border-dark bg-gray-50/30 dark:bg-background-dark/30 shrink-0 hidden xl:flex">
                <div className="p-6">
                    <h2 className="text-sm font-bold flex items-center gap-2 text-gray-900 dark:text-white">
                        <span className="material-symbols-outlined text-primary text-lg">bolt</span>
                        Quick Thoughts
                    </h2>
                    <p className="text-[10px] text-gray-500 mt-1 uppercase tracking-wider">
                        Fleeting ideas to process later
                    </p>
                </div>

                <div className="px-4 mb-4">
                    <div className="bg-white dark:bg-surface-dark border border-gray-200 dark:border-border-dark rounded-xl p-3 focus-within:border-primary transition-all">
                        <textarea
                            value={quickThought}
                            onChange={(e) => setQuickThought(e.target.value)}
                            className="w-full bg-transparent border-none p-0 text-xs focus:ring-0 resize-none h-16 text-gray-900 dark:text-gray-100 placeholder:text-gray-400 outline-none"
                            placeholder="Capture a quick thought..."
                        />
                        <div className="flex justify-end items-center mt-2 pt-2 border-t border-gray-100 dark:border-border-dark">
                            <button
                                onClick={handleSaveQuickThought}
                                disabled={!quickThought.trim()}
                                className="text-[10px] font-bold bg-primary px-3 py-1 rounded-md text-white disabled:opacity-40 hover:bg-primary/90 transition-colors"
                            >
                                Save
                            </button>
                        </div>
                    </div>
                </div>

                <div className="flex-1 overflow-y-auto no-scrollbar px-4 space-y-4 pb-8">
                    {quickThoughts.length === 0 && (
                        <p className="text-xs text-gray-400 text-center mt-8">No quick thoughts yet</p>
                    )}
                    {quickThoughts.map((qt) => (
                        <div
                            key={qt.id}
                            className="group relative bg-white dark:bg-surface-dark border border-gray-200 dark:border-border-dark rounded-xl p-4 hover:shadow-md transition-all"
                        >
                            <p className="text-xs text-gray-700 dark:text-gray-300 leading-relaxed italic">
                                "{excerpt(qt.content, 120)}"
                            </p>
                            <div className="flex items-center justify-between mt-3">
                                <span className="text-[9px] text-gray-400">
                                    {formatDate(qt.createdAt)}
                                </span>
                                <div className="flex gap-2 opacity-0 group-hover:opacity-100 transition-opacity">
                                    <button
                                        onClick={() => {
                                            selectPage(qt);
                                        }}
                                        className="material-symbols-outlined text-[14px] text-gray-400 hover:text-primary"
                                        title="Open"
                                    >
                                        open_in_new
                                    </button>
                                    <button
                                        onClick={() => handleDeletePage(qt.id)}
                                        className="material-symbols-outlined text-[14px] text-gray-400 hover:text-red-500"
                                        title="Delete"
                                    >
                                        delete
                                    </button>
                                </div>
                            </div>
                        </div>
                    ))}
                </div>

                <div className="p-4 border-t border-gray-200 dark:border-border-dark flex items-center justify-between">
                    <span className="text-[10px] text-gray-500 font-bold uppercase tracking-widest">
                        {quickThoughts.length} Quick thought{quickThoughts.length !== 1 ? 's' : ''}
                    </span>
                </div>
            </aside>
        </div>
    );
}

