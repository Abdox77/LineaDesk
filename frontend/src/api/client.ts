import axios from 'axios';

export const API_BASE = process.env.REACT_APP_API_BASE || 'http://localhost:9000';

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
            localStorage.removeItem('user');
            window.location.href = '/login';
        }

        const data = error.response?.data;
        if (data?.message) {
            error.message = data.message;
            error.fieldErrors = data.data;
        }

        return Promise.reject(error);
    },
);

export default api;
