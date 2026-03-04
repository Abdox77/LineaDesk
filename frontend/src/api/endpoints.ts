import api from './client';
import type {
    ApiResponse,
    ProjectResponseDto,
    HabitResponseDto,
} from './types';

export async function fetchProjects(): Promise<ProjectResponseDto[]> {
    const { data } = await api.get<ApiResponse<ProjectResponseDto[]>>('/api/projects');
    return data.data ?? [];
}

export async function fetchProject(id: number): Promise<ProjectResponseDto> {
    const { data } = await api.get<ApiResponse<ProjectResponseDto>>(`/api/project/${id}`);
    return data.data;
}

export async function fetchHabits(): Promise<HabitResponseDto[]> {
    const { data } = await api.get<ApiResponse<HabitResponseDto[]>>('/api/habits');
    return data.data ?? [];
}
