const STORAGE_KEY_PREFIX = 'lineadesk_activity_log';

function getStorageKey(): string {
    try {
        const raw = localStorage.getItem('user');
        if (raw) {
            const user = JSON.parse(raw);
            if (user.id) return `${STORAGE_KEY_PREFIX}_${user.id}`;
        }
    } catch {}
    return STORAGE_KEY_PREFIX;
}

export type ActivityType =
    | 'task_created'
    | 'task_updated'
    | 'task_completed'
    | 'task_deleted'
    | 'project_created'
    | 'project_updated'
    | 'project_deleted'
    | 'focus_session';

interface ActivityEntry {
    date: string;
    type: ActivityType;
}

function getLog(): ActivityEntry[] {
    try {
        const raw = localStorage.getItem(getStorageKey());
        return raw ? JSON.parse(raw) : [];
    } catch {
        return [];
    }
}

function saveLog(log: ActivityEntry[]): void {
    localStorage.setItem(getStorageKey(), JSON.stringify(log));
}

export function logActivity(type: ActivityType): void {
    const log = getLog();
    log.push({ date: new Date().toISOString().slice(0, 10), type });
    saveLog(log);
}

export function getActivityCounts(): Map<string, number> {
    const log = getLog();
    const counts = new Map<string, number>();
    for (const entry of log) {
        counts.set(entry.date, (counts.get(entry.date) ?? 0) + 1);
    }
    return counts;
}

export function buildHeatmapGrid(): { weeks: number[][]; total: number } {
    const counts = getActivityCounts();
    const today = new Date();

    const dayOfWeek = today.getDay();
    const endDate = new Date(today);
    const startDate = new Date(today);
    startDate.setDate(startDate.getDate() - (51 * 7 + dayOfWeek));

    const weeks: number[][] = [];
    let currentWeek: number[] = [];
    let total = 0;

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

        if (currentWeek.length === 7) {
            weeks.push(currentWeek);
            currentWeek = [];
        }

        cursor.setDate(cursor.getDate() + 1);
    }

    if (currentWeek.length > 0) {
        weeks.push(currentWeek);
    }

    return { weeks, total };
}
