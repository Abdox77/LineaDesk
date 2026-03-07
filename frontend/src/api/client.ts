import axios from 'axios';

export const API_BASE = 'http://localhost:9000';

const api = axios.create({
    baseURL: API_BASE,
    headers: { 'Content-Type': 'application/json' },
});

api.interceptors.request.use((config) => {
    const token = localStorage.getItem('token');
    if (token) {
        config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
});

api.interceptors.response.use(
    (response) => response,
    (error) => {
        if (error.response?.status === 401) {
            localStorage.removeItem('token');
            window.location.href = '/login';
        }

        // Extract structured error message from backend ExceptionResponse
        const data = error.response?.data;
        if (data?.message) {
            error.message = data.message;
            error.fieldErrors = data.data; // validation field errors map
        }

        return Promise.reject(error);
    },
);

export default api;
