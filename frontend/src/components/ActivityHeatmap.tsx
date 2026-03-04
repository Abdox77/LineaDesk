import React from 'react';

// Generate deterministic heatmap data (52 weeks x 7 days)
function generateHeatmapData(): number[][] {
    const seed = [
        [0,1,0,0,2,0,0],[3,4,2,0,1,0,0],[0,0,1,1,0,3,2],[4,3,0,1,0,0,0],
        [1,0,2,3,0,1,2],[0,0,1,4,2,0,0],[3,2,2,0,0,0,0],[0,0,0,1,2,3,0],
        [2,1,4,1,0,0,0],[0,0,1,1,0,3,2],[1,0,2,3,0,1,2],[0,0,1,4,2,0,0],
        [3,2,2,0,0,0,0],[0,0,0,1,2,3,0],[4,3,0,1,0,0,0],[0,0,0,1,2,3,0],
        [4,3,0,1,0,0,0],[1,0,2,3,0,1,2],[0,0,1,4,2,0,0],[3,2,2,0,0,0,0],
        [0,0,0,1,2,3,0],[2,1,4,1,0,0,0],[0,0,1,1,0,3,2],[1,0,2,3,0,1,2],
        [0,1,0,0,2,0,0],[3,4,2,0,1,0,0],[0,0,1,1,0,3,2],[4,3,0,1,0,0,0],
        [1,0,2,3,0,1,2],[0,0,1,4,2,0,0],[3,2,2,0,0,0,0],[0,0,0,1,2,3,0],
        [2,1,4,1,0,0,0],[0,0,1,1,0,3,2],[1,0,2,3,0,1,2],[0,0,1,4,2,0,0],
        [3,2,2,0,0,0,0],[0,0,0,1,2,3,0],[4,3,0,1,0,0,0],[0,0,0,1,2,3,0],
        [4,3,0,1,0,0,0],[1,0,2,3,0,1,2],[0,0,1,4,2,0,0],[3,2,2,0,0,0,0],
        [0,0,0,1,2,3,0],[2,1,4,1,0,0,0],[0,0,1,1,0,3,2],[1,0,2,3,0,1,2],
        [0,1,0,0,2,0,0],[3,4,2,0,1,0,0],[0,0,1,1,0,3,2],[0,1,0,0,0,0,0],
    ];
    return seed;
}

const LEVEL_CLASSES: Record<number, string> = {
    0: 'bg-git-level-0 dark:bg-dark-git-level-0',
    1: 'bg-git-level-1 dark:bg-dark-git-level-1',
    2: 'bg-git-level-2 dark:bg-dark-git-level-2',
    3: 'bg-git-level-3 dark:bg-dark-git-level-3',
    4: 'bg-git-level-4 dark:bg-dark-git-level-4',
};

export function ActivityHeatmap() {
    const weeks = generateHeatmapData();

    return (
        <section className="bg-card-light dark:bg-card-dark rounded-xl border border-border-light dark:border-gray-800 shadow-sm p-6">
            <div className="flex items-center justify-between mb-6">
                <div className="flex flex-col">
                    <h2 className="text-lg font-bold text-gray-900 dark:text-white">Activity Log</h2>
                    <p className="text-sm text-gray-500 dark:text-gray-400">
                        1,243 contributions in the last year
                    </p>
                </div>
                <div className="flex items-center gap-2 text-xs text-gray-500 dark:text-gray-400">
                    <span>Less</span>
                    <div className="flex gap-1">
                        {[0, 1, 2, 3, 4].map((level) => (
                            <div key={level} className={`w-3 h-3 rounded-sm ${LEVEL_CLASSES[level]}`} />
                        ))}
                    </div>
                    <span>More</span>
                </div>
            </div>

            <div className="w-full overflow-x-auto hide-scrollbar pb-2">
                <div className="min-w-[700px] flex gap-1 h-[112px]">
                    {weeks.map((week, wi) => (
                        <div
                            key={wi}
                            className={`flex flex-col gap-1 ${wi === weeks.length - 1 ? 'opacity-50' : ''}`}
                        >
                            {week.map((level, di) => (
                                <div
                                    key={di}
                                    className={`w-3 h-3 rounded-sm ${LEVEL_CLASSES[level] ?? LEVEL_CLASSES[0]}`}
                                />
                            ))}
                        </div>
                    ))}
                </div>
            </div>
        </section>
    );
}
