import React from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import './App.css';
import './index.css';
import { AuthProvider } from './auth/AuthContext';
import { RequireAuth } from './auth/RequireAuth';
import { LoginCard } from './pages/LoginCard';
import { RegisterCard } from './pages/RegisterCard';
import { OAuthCallback } from './pages/OAuthCallback';
import { Dashboard } from './pages/Dashboard';
import { ProjectDetails } from './pages/ProjectDetails';
import { Projects } from './pages/Projects';

function App() {
    return (
        <BrowserRouter>
            <AuthProvider>
                <Routes>
                    <Route path='/login' element={<LoginCard />} />
                    <Route path='/register' element={<RegisterCard />} />
                    <Route path='/oauth/callback' element={<OAuthCallback />} />
                    <Route
                        path='/dashboard'
                        element={
                            <RequireAuth>
                                <Dashboard />
                            </RequireAuth>
                        }
                    />
                    <Route
                        path='/project/:id'
                        element={
                            <RequireAuth>
                                <ProjectDetails />
                            </RequireAuth>
                        }
                    />
                    <Route
                        path='/projects'
                        element={
                            <RequireAuth>
                                <Projects />
                            </RequireAuth>
                        }
                    />
                    <Route path='/' element={<Navigate to='/login' replace />} />
                </Routes>
            </AuthProvider>
        </BrowserRouter>
    );
}

export default App;
