import { useEffect, useState } from "react";
import Sidebar from "../components/Sidebar";

function DashboardPage() {

  const [backendStatus, setBackendStatus] = useState("Checking backend...");

  useEffect(() => {

    fetch("http://localhost:8080/api/hello")
      .then((response) => response.text())
      .then((data) => {
        setBackendStatus(data);
      })
      .catch((error) => {
        console.error(error);
        setBackendStatus("Could not connect to backend");
      });

  }, []);

  return (
    <div className="app">

      <Sidebar />

      <main className="main-content">

        <h1>Dashboard</h1>

        <p>
          Backend status: {backendStatus}
        </p>

        <h2>Overview</h2>

        <p>
          Manage service operations from one place.
        </p>

      </main>

    </div>
  );
}

export default DashboardPage;