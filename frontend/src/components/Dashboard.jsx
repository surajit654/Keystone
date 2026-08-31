function Dashboard({ backendMessage }) {
  return (
    <main className="dashboard">
      <h1>Dashboard</h1>

      <div className="status">
        Backend status: {backendMessage}
      </div>

      <div className="dashboard-grid">

        <div className="card">
          <h2>Overview</h2>
          <p>Manage service operations from one place.</p>
        </div>

        <div className="card">
          <h2>Work Orders</h2>
          <p>View and manage service requests.</p>
        </div>

        <div className="card">
          <h2>Technicians</h2>
          <p>Manage technicians and assignments.</p>
        </div>

        <div className="card">
          <h2>Customers</h2>
          <p>Manage customer information.</p>
        </div>

      </div>
    </main>
  );
}

export default Dashboard;