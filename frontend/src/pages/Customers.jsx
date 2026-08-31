import Sidebar from "../components/Sidebar";

function Customers() {
  return (
    <div className="app">
      <Sidebar />

      <main className="main-content">
        <h1>Customers</h1>

        <p>
          Customer management will appear here.
        </p>

        <div className="page-card">
          <h2>Customers</h2>
          <p>No customers available yet.</p>
        </div>
      </main>
    </div>
  );
}

export default Customers;