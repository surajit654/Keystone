import Sidebar from "../components/Sidebar";

function Technicians() {
  return (
    <div className="app">
      <Sidebar />

      <main className="main-content">
        <h1>Technicians</h1>

        <p>
          Technician management will appear here.
        </p>

        <div className="page-card">
          <h2>Technicians</h2>
          <p>No technicians available yet.</p>
        </div>
      </main>
    </div>
  );
}

export default Technicians;