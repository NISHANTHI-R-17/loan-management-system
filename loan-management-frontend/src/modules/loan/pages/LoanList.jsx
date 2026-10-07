import { useEffect, useMemo, useState } from "react";
import { Link } from "react-router-dom";
import { getLoans } from "../services/loanService";
import Loading from "../../../components/Loading";
import StatusBadge from "../../../components/StatusBadge";
import { field, money } from "../utils";

export default function LoanList() {
  const [loans, setLoans] = useState([]);
  const [query, setQuery] = useState("");
  const [status, setStatus] = useState("ALL");
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const load = () => {
    setLoading(true);
    getLoans()
      .then((data) => setLoans(Array.isArray(data) ? data : data.content || []))
      .catch((err) => setError(err.message))
      .finally(() => setLoading(false));
  };

  useEffect(load, []);

  const filtered = useMemo(() => {
    return loans.filter((loan) => {
      const name = String(field(loan, "customerName", "customer") || "").toLowerCase();
      const currentStatus = String(field(loan, "status", "loanStatus") || "").toUpperCase();

      return (
        name.includes(query.toLowerCase()) &&
        (status === "ALL" || currentStatus === status)
      );
    });
  }, [loans, query, status]);

  return (
    <section className="panel">
      <div className="panel-header">
        <div>
          <h3>Loan Accounts</h3>
          <p>Search, filter and open individual loan accounts.</p>
        </div>
        <Link className="primary-button" to="/loans/new">+ Create Loan</Link>
      </div>

      <div className="toolbar">
        <input
          className="search-input"
          placeholder="Search customer name..."
          value={query}
          onChange={(e) => setQuery(e.target.value)}
        />

        <select value={status} onChange={(e) => setStatus(e.target.value)}>
          <option value="ALL">All Statuses</option>
          <option value="ACTIVE">Active</option>
          <option value="COMPLETED">Completed</option>
        </select>

        <button className="secondary-button" onClick={load}>Refresh</button>
      </div>

      {error && <div className="alert error-alert">{error}</div>}

      {loading ? (
        <Loading text="Loading loans..." />
      ) : filtered.length === 0 ? (
        <div className="empty-state">
          <h3>No matching loans</h3>
          <p>Try changing your search or create a new loan.</p>
        </div>
      ) : (
        <div className="table-wrap">
          <table>
            <thead>
              <tr>
                <th>ID</th>
                <th>Customer</th>
                <th>Original Amount</th>
                <th>Interest Rate</th>
                <th>Tenure</th>
                <th>EMI</th>
                <th>Remaining</th>
                <th>Status</th>
                <th />
              </tr>
            </thead>
            <tbody>
              {filtered.map((loan) => (
                <tr key={loan.id}>
                  <td>#{loan.id}</td>
                  <td><strong>{field(loan, "customerName", "customer") || "-"}</strong></td>
                  <td>{money(field(loan, "loanAmount", "originalLoanAmount"))}</td>
                  <td>{field(loan, "annualInterestRate", "interestRate") ?? 0}%</td>
                  <td>{field(loan, "loanTenureMonths", "tenureMonths") ?? "-"} mo</td>
                  <td>{money(field(loan, "emiAmount", "emi", "monthlyEmi"))}</td>
                  <td>{money(field(loan, "remainingPrincipal"))}</td>
                  <td><StatusBadge status={field(loan, "status", "loanStatus")} /></td>
                  <td><Link className="table-link" to={`/loans/${loan.id}`}>Details</Link></td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </section>
  );
}