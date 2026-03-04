export interface ApiResponse<T> {
    success: boolean;
    message: string;
    data: T;
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
    tasks: TaskResponseDto[];
}

export type TaskState = 'TODO' | 'IN_PROGRESS' | 'DONE';
export type TaskImportance = 'LOW' | 'MEDIUM' | 'HIGH';

export interface TaskResponseDto {
    id: number;
    taskName: string;
    projectId: number;
    description: string;
    state: TaskState;
    importance: TaskImportance;
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
