import { NavLink } from "react-router-dom";

function Sidebar() {
  return (
    <aside className="sidebar">
      <h2>KEYSTONE</h2>

      <nav>
        <NavLink to="/dashboard">
          Dashboard
        </NavLink>

        <NavLink to="/work-orders">
          Work Orders
        </NavLink>

        <NavLink to="/technicians">
          Technicians
        </NavLink>

        <NavLink to="/customers">
          Customers
        </NavLink>
      </nav>
    </aside>
  );
}

export default Sidebar;