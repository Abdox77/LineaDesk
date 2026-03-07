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
}

export interface TaskReorderItem {
    id: number;
    sortOrder: number;
}

export interface BulkTaskStateDto {
    taskIds: number[];
    state: TaskState;
}

export type HabitType = 'DAILY' | 'WEEKLY' | 'MONTHLY';

export interface HabitResponseDto {
    id: number;
    habitName: string;
    type: HabitType;
    streaks: number;
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

export interface JournalResponseDto {
    id: number;
    name: string;
    visibility: JournalVisibility;
    pages: PageResponseDto[];
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
