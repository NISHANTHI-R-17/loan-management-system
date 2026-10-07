import { NavLink } from "react-router-dom";

const links = [
  { to: "/dashboard", label: "Dashboard", icon: "▦" },
  { to: "/loans", label: "Loans", icon: "▤" },
  { to: "/loans/new", label: "Create Loan", icon: "+" },
];

export default function Sidebar() {
  return (
    <aside className="sidebar">
      <div className="brand">
        <div className="brand-mark">₹</div>
        <div>
          <strong>LoanFlow</strong>
          <span>Management System</span>
        </div>
      </div>

      <nav className="sidebar-nav">
        <p className="nav-title">MAIN MENU</p>
        {links.map((link) => (
          <NavLink
            key={link.to}
            to={link.to}
            className={({ isActive }) =>
              `nav-link ${isActive ? "active" : ""}`
            }
          >
            <span className="nav-icon">{link.icon}</span>
            {link.label}
          </NavLink>
        ))}
      </nav>

      <div className="sidebar-footer">
        <div className="security-dot" />
        <div>
          <strong>Secure Session</strong>
          <span>Admin access</span>
        </div>
      </div>
    </aside>
  );
}