import React from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import './App.css';
import './index.css';
import { LoginCard } from './pages/LoginCard';
import { RegisterCard } from './pages/RegisterCard';
import { OAuthCallback } from './pages/OAuthCallback';
import { Dashboard } from './pages/Dashboard';

function App() {
    return (
        <BrowserRouter>
            <Routes>
                <Route path='/login' element={<LoginCard />} />
                <Route path='/register' element={<RegisterCard />} />
                <Route path='/oauth/callback' element={<OAuthCallback />} />
                <Route path='/dashboard' element={<Dashboard />} />
                <Route path='/' element={<Navigate to='/login' replace />} />
            </Routes>
        </BrowserRouter>
    );
}

export default App;
