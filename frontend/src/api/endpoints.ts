import api from './client';
import type {
    ApiResponse,
    ProjectResponseDto,
    ProjectRequestDto,
    HabitResponseDto,
    HabitRequestDto,
    HabitLogResponseDto,
    TaskResponseDto,
    TaskRequestDto,
    TaskReorderItem,
    BulkTaskStateDto,
    GitHubCommitDto,
    GitHubPullRequestDto,
    ProjectInviteResponseDto,
    ProjectMemberResponseDto,
    JournalResponseDto,
    JournalRequestDto,
    PageResponseDto,
    PageRequestDto,
    LoginResponse,
    UpdateProfileDto,
    ChangePasswordDto,
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

export async function reorderTasks(items: TaskReorderItem[]): Promise<void> {
    await api.put('/api/tasks/reorder', items);
}

export async function bulkDeleteTasks(taskIds: number[]): Promise<void> {
    await api.delete('/api/tasks/bulk', { data: taskIds });
}

export async function bulkUpdateTaskState(dto: BulkTaskStateDto): Promise<void> {
    await api.put('/api/tasks/bulk-state', dto);
}

export async function fetchHabits(): Promise<HabitResponseDto[]> {
    const { data } = await api.get<ApiResponse<HabitResponseDto[]>>('/api/habits');
    return data.data ?? [];
}

export async function createHabit(req: HabitRequestDto): Promise<HabitResponseDto> {
    const { data } = await api.post<ApiResponse<HabitResponseDto>>('/api/habit', req);
    return data.data;
}

export async function updateHabit(id: number, req: Partial<HabitRequestDto>): Promise<HabitResponseDto> {
    const { data } = await api.put<ApiResponse<HabitResponseDto>>(`/api/habit/${id}`, req);
    return data.data;
}

export async function deleteHabit(id: number): Promise<void> {
    await api.delete(`/api/habit/${id}`);
}

export async function logHabitDay(habitId: number, date: string): Promise<HabitLogResponseDto> {
    const { data } = await api.post<ApiResponse<HabitLogResponseDto>>(`/api/habit/${habitId}/log?date=${date}`);
    return data.data;
}

export async function unlogHabitDay(habitId: number, date: string): Promise<void> {
    await api.delete(`/api/habit/${habitId}/log?date=${date}`);
}

export async function fetchHabitLogs(habitId: number, from: string, to: string): Promise<HabitLogResponseDto[]> {
    const { data } = await api.get<ApiResponse<HabitLogResponseDto[]>>(`/api/habit/${habitId}/logs?from=${from}&to=${to}`);
    return data.data ?? [];
}

export async function fetchAllHabitLogs(from: string, to: string): Promise<HabitLogResponseDto[]> {
    const { data } = await api.get<ApiResponse<HabitLogResponseDto[]>>(`/api/habits/logs?from=${from}&to=${to}`);
    return data.data ?? [];
}

export async function generateProjectInvite(projectId: number): Promise<{ token: string; expiresAt: string }> {
    const { data } = await api.post<ApiResponse<{ token: string; expiresAt: string }>>(`/api/project/${projectId}/invite`);
    return data.data;
}

export async function joinProjectByToken(token: string): Promise<ProjectResponseDto> {
    const { data } = await api.post<ApiResponse<ProjectResponseDto>>(`/api/projects/join?token=${token}`);
    return data.data;
}

export async function fetchProjectMembers(projectId: number): Promise<ProjectMemberResponseDto[]> {
    const { data } = await api.get<ApiResponse<ProjectMemberResponseDto[]>>(`/api/project/${projectId}/members`);
    return data.data ?? [];
}

export async function removeProjectMember(projectId: number, userId: number): Promise<void> {
    await api.delete(`/api/project/${projectId}/member/${userId}`);
}

export async function fetchGitHubCommits(owner: string, repo: string): Promise<GitHubCommitDto[]> {
    try {
        const res = await fetch(`https://api.github.com/repos/${owner}/${repo}/commits?per_page=10`);
        if (!res.ok) return [];
        const commits = await res.json();
        return commits.map((c: any) => ({
            sha: c.sha?.slice(0, 7) ?? '',
            message: c.commit?.message?.split('\n')[0] ?? '',
            authorName: c.commit?.author?.name ?? c.author?.login ?? 'Unknown',
            authorAvatar: c.author?.avatar_url ?? '',
            date: c.commit?.author?.date ?? '',
            url: c.html_url ?? '',
        }));
    } catch {
        return [];
    }
}

export async function fetchGitHubPullRequests(owner: string, repo: string): Promise<GitHubPullRequestDto[]> {
    try {
        const res = await fetch(`https://api.github.com/repos/${owner}/${repo}/pulls?state=all&per_page=10`);
        if (!res.ok) return [];
        const prs = await res.json();
        return prs.map((pr: any) => ({
            number: pr.number,
            title: pr.title ?? '',
            state: pr.state ?? 'open',
            authorName: pr.user?.login ?? 'Unknown',
            authorAvatar: pr.user?.avatar_url ?? '',
            createdAt: pr.created_at ?? '',
            url: pr.html_url ?? '',
        }));
    } catch {
        return [];
    }
}


export async function fetchJournals(): Promise<JournalResponseDto[]> {
    const { data } = await api.get<ApiResponse<JournalResponseDto[]>>('/api/journals');
    return data.data ?? [];
}

export async function fetchJournal(id: number): Promise<JournalResponseDto> {
    const { data } = await api.get<ApiResponse<JournalResponseDto>>(`/api/journal/${id}`);
    return data.data;
}

export async function createJournal(req: JournalRequestDto): Promise<JournalResponseDto> {
    const { data } = await api.post<ApiResponse<JournalResponseDto>>('/api/journal', req);
    return data.data;
}

export async function updateJournal(id: number, req: Partial<JournalRequestDto>): Promise<JournalResponseDto> {
    const { data } = await api.put<ApiResponse<JournalResponseDto>>(`/api/journal/${id}`, req);
    return data.data;
}

export async function deleteJournal(id: number): Promise<void> {
    await api.delete(`/api/journal/${id}`);
}


export async function fetchPagesByJournal(journalId: number): Promise<PageResponseDto[]> {
    const { data } = await api.get<ApiResponse<PageResponseDto[]>>(`/api/journal/${journalId}/pages`);
    return data.data ?? [];
}

export async function fetchPage(id: number): Promise<PageResponseDto> {
    const { data } = await api.get<ApiResponse<PageResponseDto>>(`/api/page/${id}`);
    return data.data;
}

export async function createPage(req: PageRequestDto): Promise<PageResponseDto> {
    const { data } = await api.post<ApiResponse<PageResponseDto>>('/api/page', req);
    return data.data;
}

export async function updatePage(id: number, req: Partial<PageRequestDto>): Promise<PageResponseDto> {
    const { data } = await api.put<ApiResponse<PageResponseDto>>(`/api/page/${id}`, req);
    return data.data;
}

export async function deletePage(id: number): Promise<void> {
    await api.delete(`/api/page/${id}`);
}

export async function updateProfile(dto: UpdateProfileDto): Promise<LoginResponse> {
    const { data } = await api.put<ApiResponse<LoginResponse>>('/api/user/profile', dto);
    return data.data;
}

export async function changePassword(dto: ChangePasswordDto): Promise<void> {
    await api.put('/api/user/password', dto);
}
