import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider, useAuth } from './context/AuthContext';

import Login from './pages/Login';
import DashboardPage from './pages/DashboardPage';
import WorkOrders from './pages/WorkOrders';
import Technicians from './pages/Technicians';
import Customers from './pages/Customers';
import CustomerPortal from './pages/CustomerPortal';

import './App.css';

function ProtectedRoute({ children, allowedRoles }) {
  const { user, isAuthenticated } = useAuth();
  if (!isAuthenticated) {
    return <Navigate to="/login" replace />;
  }
  if (allowedRoles && !allowedRoles.includes(user?.role)) {
    if (user?.role === 'CUSTOMER') {
      return <Navigate to="/portal" replace />;
    }
    if (user?.role === 'TECHNICIAN') {
      return <Navigate to="/technicians" replace />;
    }
    return <Navigate to="/work-orders" replace />;
  }
  return children;
}

function HomeRedirect() {
  const { user, isAuthenticated } = useAuth();
  if (!isAuthenticated) {
    return <Navigate to="/login" replace />;
  }
  if (user?.role === 'CUSTOMER') {
    return <Navigate to="/portal" replace />;
  }
  if (user?.role === 'TECHNICIAN') {
    return <Navigate to="/technicians" replace />;
  }
  return <Navigate to="/dashboard" replace />;
}

function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <Routes>
          <Route path="/" element={<HomeRedirect />} />
          <Route path="/login" element={<Login />} />

          <Route
            path="/portal"
            element={
              <ProtectedRoute allowedRoles={['CUSTOMER', 'MANAGER']}>
                <CustomerPortal />
              </ProtectedRoute>
            }
          />

          <Route
            path="/dashboard"
            element={
              <ProtectedRoute allowedRoles={['MANAGER', 'DISPATCHER']}>
                <DashboardPage />
              </ProtectedRoute>
            }
          />

          <Route
            path="/work-orders"
            element={
              <ProtectedRoute allowedRoles={['DISPATCHER', 'MANAGER', 'TECHNICIAN']}>
                <WorkOrders />
              </ProtectedRoute>
            }
          />

          <Route
            path="/technicians"
            element={
              <ProtectedRoute allowedRoles={['DISPATCHER', 'MANAGER', 'TECHNICIAN']}>
                <Technicians />
              </ProtectedRoute>
            }
          />

          <Route
            path="/customers"
            element={
              <ProtectedRoute allowedRoles={['DISPATCHER', 'MANAGER']}>
                <Customers />
              </ProtectedRoute>
            }
          />

          <Route path="*" element={<HomeRedirect />} />
        </Routes>
      </BrowserRouter>
    </AuthProvider>
  );
}

export default App;