export interface ApiResponse<T> {
    success: boolean;
    message: string;
    data: T;
}

export interface ExceptionResponse {
    success: boolean;
    message: string;
    data?: Record<string, string>;
}

export interface LoginResponse {
    id: number;
    email: string;
    jwtToken: string;
    username: string;
}

export interface ProjectResponseDto {
    projectId: number;
    projectName: string;
    description: string;
    githubLink: string;
    sessions: number;
    state: ProjectState;
    tasks: TaskResponseDto[];
    members: ProjectMemberResponseDto[];
}

export type ProjectState = 'PENDING' | 'IN_PROGRESS' | 'FINISHED';

export interface ProjectRequestDto {
    projectName: string;
    description?: string;
    githubLink?: string;
    sessions?: number;
    state?: ProjectState;
}

export type TaskState = 'PENDING' | 'IN_PROGRESS' | 'FINISHED';
export type TaskImportance = 'NORMAL' | 'MEDIUM' | 'IMPORTANT' | 'CRUCIAL';

export interface TaskResponseDto {
    id: number;
    taskName: string;
    projectId: number;
    description: string;
    duration: number;
    state: TaskState;
    importance: TaskImportance;
    sortOrder: number;
    dueDate: string | null;
    parentTaskId: number | null;
    subtasks: TaskResponseDto[];
    assigneeId: number | null;
    assigneeUsername: string | null;
}

export interface TaskRequestDto {
    taskName: string;
    projectId: number;
    description?: string;
    duration?: number;
    state?: TaskState;
    importance?: TaskImportance;
    sortOrder?: number;
    dueDate?: string | null;
    parentTaskId?: number | null;
    assigneeId?: number | null;
}

export interface TaskReorderItem {
    id: number;
    sortOrder: number;
}

export interface BulkTaskStateDto {
    taskIds: number[];
    state: TaskState;
}

export type HabitType = 'FITNESS' | 'MENTAL_WELLBEING' | 'INTELLECTUAL';

export interface HabitResponseDto {
    id: number;
    habitName: string;
    type: HabitType;
    streaks: number;
}

export interface HabitRequestDto {
    habitName: string;
    type?: HabitType;
    streaks?: number;
}

export interface HabitLogResponseDto {
    id: number;
    habitId: number;
    date: string;
    completed: boolean;
}

export type JournalVisibility = 'PUBLIC' | 'PRIVATE';

export interface PageResponseDto {
    id: number;
    title: string;
    content: string;
    journalId: number;
    createdAt: string;
    updatedAt: string;
}

export interface PageRequestDto {
    title: string;
    content?: string;
    journalId: number;
}

export interface JournalResponseDto {
    id: number;
    name: string;
    visibility: JournalVisibility;
    pages: PageResponseDto[];
}

export interface JournalRequestDto {
    name: string;
    visibility?: JournalVisibility;
}

export interface ActivityRequestDto {
    activityType: ActivityType;
    description?: string;
}

export interface ActivityResponseDto {
    id: number;
    activityType: ActivityType;
    description: string;
    timestamp: string;
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

export interface PomodoroSettings {
    focusMinutes: number;
    shortBreakMinutes: number;
    longBreakMinutes: number;
    sessionsBeforeLongBreak: number;
}

export interface GitHubCommitDto {
    sha: string;
    message: string;
    authorName: string;
    authorAvatar: string;
    date: string;
    url: string;
}

export interface GitHubPullRequestDto {
    number: number;
    title: string;
    state: string;
    authorName: string;
    authorAvatar: string;
    createdAt: string;
    url: string;
}

export interface ProjectInviteResponseDto {
    id: number;
    token: string;
    status: string;
    projectId: number;
    projectName: string;
    createdAt: string;
    expiresAt: string;
}

export interface ProjectMemberResponseDto {
    id: number;
    userId: number;
    username: string;
    email: string;
    role: string;
}

export interface UpdateProfileDto {
    username?: string;
    email?: string;
}

export interface ChangePasswordDto {
    currentPassword: string;
    newPassword: string;
}

