import api from './client';
import type {
    ApiResponse,
    ProjectResponseDto,
    ProjectRequestDto,
    HabitResponseDto,
    TaskResponseDto,
    TaskRequestDto,
} from './types';

export async function fetchProjects(): Promise<ProjectResponseDto[]> {
    const { data } = await api.get<ApiResponse<ProjectResponseDto[]>>('/api/projects');
    return data.data ?? [];
}

export async function fetchProject(id: number): Promise<ProjectResponseDto> {
    const { data } = await api.get<ApiResponse<ProjectResponseDto>>(`/api/project/${id}`);
    return data.data;
}

export async function createProject(req: ProjectRequestDto): Promise<ProjectResponseDto> {
    const { data } = await api.post<ApiResponse<ProjectResponseDto>>('/api/project', req);
    return data.data;
}

export async function updateProject(id: number, req: Partial<ProjectRequestDto>): Promise<ProjectResponseDto> {
    const { data } = await api.put<ApiResponse<ProjectResponseDto>>(`/api/project/${id}`, req);
    return data.data;
}

export async function deleteProject(id: number): Promise<void> {
    await api.delete(`/api/project/${id}`);
}

export async function createTask(req: TaskRequestDto): Promise<TaskResponseDto> {
    const { data } = await api.post<ApiResponse<TaskResponseDto>>('/api/task', req);
    return data.data;
}

export async function updateTask(id: number, req: Partial<TaskRequestDto>): Promise<TaskResponseDto> {
    const { data } = await api.put<ApiResponse<TaskResponseDto>>(`/api/task/${id}`, req);
    return data.data;
}

export async function deleteTask(id: number): Promise<void> {
    await api.delete(`/api/task/${id}`);
}

export async function fetchHabits(): Promise<HabitResponseDto[]> {
    const { data } = await api.get<ApiResponse<HabitResponseDto[]>>('/api/habits');
    return data.data ?? [];
}
