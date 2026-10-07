import { useEffect, useMemo, useState } from "react";
import { Link } from "react-router-dom";
import { getLoans } from "../services/loanService";
import Loading from "../../../components/Loading";
import ErrorMessage from "../../../components/ErrorMessage";
import StatusBadge from "../../../components/StatusBadge";
import { field, money } from "../utils";

export default function Dashboard() {
  const [loans, setLoans] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    getLoans()
      .then((data) => setLoans(Array.isArray(data) ? data : data.content || []))
      .catch((err) => setError(err.message))
      .finally(() => setLoading(false));
  }, []);

  const stats = useMemo(() => {
    const active = loans.filter(
      (l) => String(field(l, "status", "loanStatus")).toUpperCase() === "ACTIVE"
    ).length;

    const completed = loans.filter(
      (l) =>
        String(field(l, "status", "loanStatus")).toUpperCase() === "COMPLETED"
    ).length;

    const outstanding = loans.reduce(
      (sum, l) =>
        sum + Number(field(l, "remainingPrincipal") || 0),
      0
    );

    return { total: loans.length, active, completed, outstanding };
  }, [loans]);

  if (loading) return <Loading text="Loading dashboard..." />;

  return (
    <div>
      <ErrorMessage message={error} />

      <section className="welcome-banner">
        <div>
          <span className="eyebrow">PORTFOLIO OVERVIEW</span>
          <h2>Good day, Admin 👋</h2>
          <p>Track your loans, repayments and outstanding balances from one place.</p>
        </div>
        <Link className="primary-button" to="/loans/new">
          + Create New Loan
        </Link>
      </section>

      <div className="stats-grid">
        <StatCard label="Total Loans" value={stats.total} icon="▤" />
        <StatCard label="Active Loans" value={stats.active} icon="◉" />
        <StatCard label="Completed" value={stats.completed} icon="✓" />
        <StatCard label="Outstanding Principal" value={money(stats.outstanding)} icon="₹" />
      </div>

      <section className="panel">
        <div className="panel-header">
          <div>
            <h3>Recent Loans</h3>
            <p>Latest loan accounts in the system</p>
          </div>
          <Link className="text-button" to="/loans">View all →</Link>
        </div>

        {loans.length === 0 ? (
          <div className="empty-state">
            <div className="empty-icon">₹</div>
            <h3>No loans yet</h3>
            <p>Create your first loan account to get started.</p>
            <Link className="primary-button" to="/loans/new">Create Loan</Link>
          </div>
        ) : (
          <div className="table-wrap">
            <table>
              <thead>
                <tr>
                  <th>Customer</th>
                  <th>Loan Amount</th>
                  <th>EMI</th>
                  <th>Remaining</th>
                  <th>Status</th>
                  <th />
                </tr>
              </thead>
              <tbody>
                {loans.slice(0, 6).map((loan) => (
                  <tr key={loan.id}>
                    <td>
                      <strong>{field(loan, "customerName", "customer") || "-"}</strong>
                    </td>
                    <td>{money(field(loan, "loanAmount", "originalLoanAmount"))}</td>
                    <td>{money(field(loan, "emiAmount", "emi", "monthlyEmi"))}</td>
                    <td>{money(field(loan, "remainingPrincipal"))}</td>
                    <td><StatusBadge status={field(loan, "status", "loanStatus")} /></td>
                    <td>
                      <Link className="table-link" to={`/loans/${loan.id}`}>View</Link>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </section>
    </div>
  );
}

function StatCard({ label, value, icon }) {
  return (
    <div className="stat-card">
      <div className="stat-icon">{icon}</div>
      <div>
        <span>{label}</span>
        <strong>{value}</strong>
      </div>
    </div>
  );
}