import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { createLoan } from "../services/loanService";

const initial = {
  customerName: "",
  loanAmount: "",
  annualInterestRate: "",
  loanTenureMonths: "",
  startDate: new Date().toISOString().slice(0, 10),
};

export default function CreateLoan() {
  const navigate = useNavigate();
  const [form, setForm] = useState(initial);
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const change = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
    setError("");
  };

  const submit = async (e) => {
    e.preventDefault();
    setError("");

    if (!form.customerName.trim()) return setError("Customer name is required.");
    if (Number(form.loanAmount) <= 0) return setError("Loan amount must be greater than 0.");
    if (Number(form.annualInterestRate) < 0) return setError("Interest rate cannot be negative.");
    if (Number(form.loanTenureMonths) <= 0) return setError("Tenure must be greater than 0.");
    if (!form.startDate) return setError("Start date is required.");

    setLoading(true);

    try {
      const created = await createLoan({
        customerName: form.customerName.trim(),
        loanAmount: Number(form.loanAmount),
        annualInterestRate: Number(form.annualInterestRate),
        loanTenureMonths: Number(form.loanTenureMonths),
        startDate: form.startDate,
      });

      navigate(`/loans/${created.id}`);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <section className="form-panel">
      <div className="form-intro">
        <div className="section-icon">₹</div>
        <div>
          <h2>Create New Loan</h2>
          <p>Enter the loan details. EMI and totals are calculated by the backend.</p>
        </div>
      </div>

      {error && <div className="alert error-alert">{error}</div>}

      <form onSubmit={submit}>
        <div className="form-grid">
          <label>
            Customer Name
            <input name="customerName" value={form.customerName} onChange={change} placeholder="Enter customer name" />
          </label>

          <label>
            Loan Amount
            <input type="number" min="0.01" step="0.01" name="loanAmount" value={form.loanAmount} onChange={change} placeholder="100000" />
          </label>

          <label>
            Annual Interest Rate (%)
            <input type="number" min="0" step="0.01" name="annualInterestRate" value={form.annualInterestRate} onChange={change} placeholder="12" />
          </label>

          <label>
            Loan Tenure (Months)
            <input type="number" min="1" name="loanTenureMonths" value={form.loanTenureMonths} onChange={change} placeholder="12" />
          </label>

          <label>
            Start Date
            <input type="date" name="startDate" value={form.startDate} onChange={change} />
          </label>
        </div>

        <div className="form-actions">
          <button type="button" className="secondary-button" onClick={() => navigate("/loans")}>Cancel</button>
          <button className="primary-button" disabled={loading}>
            {loading ? "Creating..." : "Create Loan"}
          </button>
        </div>
      </form>
    </section>
  );
}