import { BrowserRouter, Routes, Route } from "react-router-dom";

import Login from "./pages/Login";
import DashboardPage from "./pages/DashboardPage";
import WorkOrders from "./pages/WorkOrders";
import Technicians from "./pages/Technicians";
import Customers from "./pages/Customers";

import "./App.css";

function App() {
  return (
    <BrowserRouter>
      <Routes>

        <Route path="/" element={<Login />} />

        <Route path="/login" element={<Login />} />

        <Route path="/dashboard" element={<DashboardPage />} />

        <Route path="/work-orders" element={<WorkOrders />} />

        <Route path="/technicians" element={<Technicians />} />

        <Route path="/customers" element={<Customers />} />

      </Routes>
    </BrowserRouter>
  );
}

export default App;