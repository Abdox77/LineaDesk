import React from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import './App.css';
import './index.css';
import { AuthProvider } from './auth/AuthContext';
import { RequireAuth } from './auth/RequireAuth';
import { ThemeProvider } from './components/ThemeProvider';
import { ToastProvider } from './components/ToastProvider';
import { LoginCard } from './pages/LoginCard';
import { RegisterCard } from './pages/RegisterCard';
import { OAuthCallback } from './pages/OAuthCallback';
import { Dashboard } from './pages/Dashboard';
import { ProjectDetails } from './pages/ProjectDetails';
import { Projects } from './pages/Projects';
import { FocusSession } from './pages/FocusSession';
import { Habits } from './pages/Habits';

function App() {
    return (
        <BrowserRouter>
            <ThemeProvider>
                <AuthProvider>
                    <ToastProvider>
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
                            <Route
                                path='/focus/:projectId'
                                element={
                                    <RequireAuth>
                                        <FocusSession />
                                    </RequireAuth>
                                }
                            />
                            <Route
                                path='/habits'
                                element={
                                    <RequireAuth>
                                        <Habits />
                                    </RequireAuth>
                                }
                            />
                            <Route path='/' element={<Navigate to='/login' replace />} />
                        </Routes>
                    </ToastProvider>
                </AuthProvider>
            </ThemeProvider>
        </BrowserRouter>
    );
}

export default App;
