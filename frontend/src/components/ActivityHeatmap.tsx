import React, { useMemo, useRef } from 'react';
import { getActivityCounts } from '../api/activityTracker';

const LEVEL_CLASSES: Record<number, string> = {
    0: 'bg-[#ebedf0] dark:bg-[#2d333b]',
    1: 'bg-[#9be9a8] dark:bg-[#0e4429]',
    2: 'bg-[#40c463] dark:bg-[#006d32]',
    3: 'bg-[#30a14e] dark:bg-[#26a641]',
    4: 'bg-[#216e39] dark:bg-[#39d353]',
};

const DAY_LABELS = ['', 'Mon', '', 'Wed', '', 'Fri', ''];

interface ActivityHeatmapProps {
    refreshKey?: number;
    scrollTrigger?: number;
}

export function ActivityHeatmap({ refreshKey, scrollTrigger }: ActivityHeatmapProps) {
    const { weeks, total, monthLabels, dates } = useMemo(() => buildHeatmapGridEnhanced(), [refreshKey]);
    const scrollRef = useRef<HTMLDivElement>(null);

    React.useEffect(() => {
        if (scrollTrigger && scrollRef.current) {
            scrollRef.current.scrollTo({ left: scrollRef.current.scrollWidth, behavior: 'smooth' });
        }
    }, [scrollTrigger]);

    const now = new Date();
    const monthStart = `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, '0')}-01`;
    const todayStr = now.toISOString().slice(0, 10);
    const counts = useMemo(() => getActivityCounts(), [refreshKey]);
    const thisMonthTotal = useMemo(() => {
        let s = 0;
        counts.forEach((v, k) => { if (k >= monthStart && k <= todayStr) s += v; });
        return s;
    }, [counts, monthStart, todayStr]);

    const todayCount = counts.get(todayStr) ?? 0;

    return (
        <section className="bg-white dark:bg-[#161b22] rounded-xl border border-gray-200 dark:border-[#30363d] shadow-sm">
            <div className="flex items-center justify-between px-6 pt-5 pb-3">
                <div className="flex items-center gap-3">
                    <div className="w-9 h-9 rounded-lg bg-emerald-100 dark:bg-emerald-900/20 flex items-center justify-center">
                        <span className="material-symbols-outlined text-emerald-600 dark:text-emerald-400 text-[20px]">local_fire_department</span>
                    </div>
                    <div>
                        <h2 className="text-base font-bold text-gray-900 dark:text-white leading-tight">Activity Log</h2>
                        <p className="text-xs text-gray-500 dark:text-gray-400">
                            {total.toLocaleString()} contribution{total !== 1 ? 's' : ''} in the last year
                        </p>
                    </div>
                </div>
                <div className="flex items-center gap-4">
                    <div className="hidden md:flex items-center gap-4 text-xs">
                        <div className="flex flex-col items-center px-3 py-1.5 rounded-lg bg-gray-50 dark:bg-[#21262d]">
                            <span className="font-bold text-gray-900 dark:text-white text-sm">{todayCount}</span>
                            <span className="text-gray-500 dark:text-gray-400">Today</span>
                        </div>
                        <div className="flex flex-col items-center px-3 py-1.5 rounded-lg bg-gray-50 dark:bg-[#21262d]">
                            <span className="font-bold text-gray-900 dark:text-white text-sm">{thisMonthTotal}</span>
                            <span className="text-gray-500 dark:text-gray-400">This month</span>
                        </div>
                    </div>
                    <div className="flex items-center gap-1.5 text-[11px] text-gray-400 dark:text-gray-500">
                        <span>Less</span>
                        {[0, 1, 2, 3, 4].map((level) => (
                            <div key={level} className={`w-[11px] h-[11px] rounded-[2px] ${LEVEL_CLASSES[level]}`} />
                        ))}
                        <span>More</span>
                    </div>
                </div>
            </div>

            <div className="px-6 pb-5">
                <div className="flex gap-0">
                    <div className="flex flex-col gap-[3px] pr-2 pt-[22px]">
                        {DAY_LABELS.map((label, i) => (
                            <div key={i} className="h-[11px] flex items-center">
                                <span className="text-[10px] text-gray-400 dark:text-gray-500 font-medium leading-none w-6 text-right">
                                    {label}
                                </span>
                            </div>
                        ))}
                    </div>

                    <div className="flex-1 overflow-x-auto no-scrollbar" ref={scrollRef}>
                        <div className="relative">
                            <div className="relative" style={{ height: '14px', marginBottom: '4px' }}>
                                {monthLabels.map((ml, i) => (
                                    <span
                                        key={i}
                                        className="absolute text-[10px] text-gray-400 dark:text-gray-500 font-medium whitespace-nowrap"
                                        style={{ left: `${ml.weekIndex * 14}px`, top: 0 }}
                                    >
                                        {ml.label}
                                    </span>
                                ))}
                            </div>
                            <div className="flex gap-[3px]">
                                {weeks.map((week, wi) => (
                                    <div key={wi} className="flex flex-col gap-[3px]">
                                        {week.map((cell, di) => {
                                            const dateStr = dates[wi]?.[di] ?? '';
                                            const count = dateStr ? (counts.get(dateStr) ?? 0) : 0;
                                            const isToday = dateStr === todayStr;
                                            return (
                                                <div
                                                    key={di}
                                                    title={dateStr ? `${count} contribution${count !== 1 ? 's' : ''} on ${dateStr}` : ''}
                                                    className={`w-[11px] h-[11px] rounded-[2px] transition-colors ${LEVEL_CLASSES[cell] ?? LEVEL_CLASSES[0]} ${isToday ? 'ring-1 ring-gray-400 dark:ring-gray-500 ring-offset-1 ring-offset-white dark:ring-offset-[#161b22]' : ''}`}
                                                />
                                            );
                                        })}
                                    </div>
                                ))}
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </section>
    );
}


function buildHeatmapGridEnhanced(): {
    weeks: number[][];
    dates: string[][];
    total: number;
    monthLabels: { label: string; weekIndex: number }[];
} {
    const counts = getActivityCounts();
    const today = new Date();
    const dayOfWeek = today.getDay();
    const endDate = new Date(today);
    const startDate = new Date(today);
    startDate.setDate(startDate.getDate() - (51 * 7 + dayOfWeek));

    const weeks: number[][] = [];
    const dates: string[][] = [];
    let currentWeek: number[] = [];
    let currentWeekDates: string[] = [];
    let total = 0;

    const MONTH_NAMES = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'];
    const monthLabels: { label: string; weekIndex: number }[] = [];
    let lastMonth = -1;

    const cursor = new Date(startDate);
    while (cursor <= endDate) {
        const dateStr = cursor.toISOString().slice(0, 10);
        const count = counts.get(dateStr) ?? 0;
        total += count;

        let level = 0;
        if (count >= 8) level = 4;
        else if (count >= 5) level = 3;
        else if (count >= 3) level = 2;
        else if (count >= 1) level = 1;

        currentWeek.push(level);
        currentWeekDates.push(dateStr);

        const m = cursor.getMonth();
        if (m !== lastMonth && cursor.getDate() <= 7) {
            monthLabels.push({ label: MONTH_NAMES[m], weekIndex: weeks.length });
            lastMonth = m;
        }

        if (currentWeek.length === 7) {
            weeks.push(currentWeek);
            dates.push(currentWeekDates);
            currentWeek = [];
            currentWeekDates = [];
        }

        cursor.setDate(cursor.getDate() + 1);
    }

    if (currentWeek.length > 0) {
        weeks.push(currentWeek);
        dates.push(currentWeekDates);
    }

    return { weeks, dates, total, monthLabels };
}
