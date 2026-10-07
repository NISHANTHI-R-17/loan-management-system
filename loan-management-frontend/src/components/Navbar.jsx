import { useLocation, useNavigate } from "react-router-dom";
import { useState } from "react";
import { logoutUser } from "../modules/auth/services/authService";

const titles = {
  "/dashboard": ["Dashboard", "Overview of your loan portfolio"],
  "/loans": ["Loans", "Manage all customer loans"],
  "/loans/new": ["Create Loan", "Create a new loan account"],
};

export default function Navbar() {
  const location = useLocation();
  const navigate = useNavigate();
  const [busy, setBusy] = useState(false);

  const [title, subtitle] =
    titles[location.pathname] || ["Loan Details", "Review loan information"];

  const logout = async () => {
    setBusy(true);
    try {
      await logoutUser();
    } finally {
      navigate("/login", { replace: true });
      setBusy(false);
    }
  };

  return (
    <header className="topbar">
      <div>
        <h1>{title}</h1>
        <p>{subtitle}</p>
      </div>

      <div className="topbar-actions">
        <div className="admin-chip">
          <div className="avatar">A</div>
          <div>
            <strong>Admin</strong>
            <span>Administrator</span>
          </div>
        </div>

        <button className="logout-button" onClick={logout} disabled={busy}>
          {busy ? "..." : "Logout"}
        </button>
      </div>
    </header>
  );
}