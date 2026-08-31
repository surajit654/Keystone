import Sidebar from "../components/Sidebar";

function WorkOrders() {
  return (
    <div className="app">
      <Sidebar />

      <main className="main-content">
        <h1>Work Orders</h1>

        <p>
          View and manage service requests.
        </p>

        <div className="page-card">
          <h2>Work Orders</h2>

          <p>No work orders available yet.</p>
        </div>
      </main>
    </div>
  );
}

export default WorkOrders;