import { useEffect, useState } from "react";
import { Link, useParams } from "react-router-dom";
import {
  getLoanDetails,
  getSchedule,
  generateSchedule,
  makeRepayment,
  makeAdditionalPayment,
} from "../services/loanService";
import Loading from "../../../components/Loading";
import StatusBadge from "../../../components/StatusBadge";
import { date, field, money } from "../utils";

export default function LoanDetails() {
  const { loanId } = useParams();

  const [details, setDetails] = useState(null);
  const [schedule, setSchedule] = useState([]);
  const [tab, setTab] = useState("overview");
  const [loading, setLoading] = useState(true);
  const [actionLoading, setActionLoading] = useState(false);
  const [message, setMessage] = useState("");
  const [error, setError] = useState("");

  const [repayment, setRepayment] = useState({
    emiScheduleId: "",
    repaymentAmount: "",
    repaymentDate: new Date().toISOString().slice(0, 10),
  });

  const [extra, setExtra] = useState({
    paymentAmount: "",
    paymentDate: new Date().toISOString().slice(0, 10),
  });

  const load = async () => {
    setLoading(true);
    setError("");

    try {
      const [detailData, scheduleData] = await Promise.all([
        getLoanDetails(loanId),
        getSchedule(loanId).catch(() => []),
      ]);

      setDetails(detailData);
      setSchedule(Array.isArray(scheduleData) ? scheduleData : []);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    load();
  }, [loanId]);

  const refresh = async () => {
    const [detailData, scheduleData] = await Promise.all([
      getLoanDetails(loanId),
      getSchedule(loanId).catch(() => []),
    ]);
    setDetails(detailData);
    setSchedule(Array.isArray(scheduleData) ? scheduleData : []);
  };

  const createSchedule = async () => {
    setActionLoading(true);
    setError("");
    setMessage("");

    try {
      const data = await generateSchedule(loanId);
      setSchedule(Array.isArray(data) ? data : []);
      setMessage("EMI schedule generated successfully.");
    } catch (err) {
      setError(err.message);
    } finally {
      setActionLoading(false);
    }
  };

  const submitRepayment = async (e) => {
    e.preventDefault();
    setActionLoading(true);
    setError("");
    setMessage("");

    try {
      await makeRepayment(loanId, {
        emiScheduleId: Number(repayment.emiScheduleId),
        repaymentAmount: Number(repayment.repaymentAmount),
        repaymentDate: repayment.repaymentDate,
      });

      setRepayment((old) => ({
        ...old,
        emiScheduleId: "",
        repaymentAmount: "",
      }));

      await refresh();
      setMessage("Repayment recorded successfully.");
      setTab("schedule");
    } catch (err) {
      setError(err.message);
    } finally {
      setActionLoading(false);
    }
  };

  const submitExtra = async (e) => {
    e.preventDefault();
    setActionLoading(true);
    setError("");
    setMessage("");

    try {
      await makeAdditionalPayment(loanId, {
        paymentAmount: Number(extra.paymentAmount),
        paymentDate: extra.paymentDate,
      });

      setExtra((old) => ({ ...old, paymentAmount: "" }));

      await refresh();
      setMessage("Additional principal payment recorded.");
      setTab("overview");
    } catch (err) {
      setError(err.message);
    } finally {
      setActionLoading(false);
    }
  };

  if (loading) return <Loading text="Loading loan details..." />;
  if (!details) return <div className="empty-state">Loan not found.</div>;

  const status = field(details, "loanStatus", "status");
  const history = details.repaymentHistory || [];
  const pending = schedule.filter((x) => String(x.paymentStatus).toUpperCase() !== "PAID");

  return (
    <div>
      <div className="detail-top">
        <div>
          <Link className="back-link" to="/loans">← Back to Loans</Link>
          <div className="detail-title-row">
            <div>
              <h2>{field(details, "customerName", "customer")}</h2>
              <p>Loan #{details.loanId}</p>
            </div>
            <StatusBadge status={status} />
          </div>
        </div>

        <button className="secondary-button" onClick={load}>Refresh</button>
      </div>

      {error && <div className="alert error-alert">{error}</div>}
      {message && <div className="alert success-alert">{message}</div>}

      <div className="summary-grid">
        <Summary label="Original Amount" value={money(field(details, "originalLoanAmount", "loanAmount"))} />
        <Summary label="Monthly EMI" value={money(field(details, "emi", "emiAmount", "monthlyEmi"))} />
        <Summary label="Total Interest" value={money(details.totalInterest)} />
        <Summary label="Remaining Principal" value={money(details.remainingPrincipal)} highlight />
      </div>

      <div className="details-grid">
        <section className="panel">
          <div className="panel-header">
            <div>
              <h3>Loan Information</h3>
              <p>Original loan configuration</p>
            </div>
          </div>

          <div className="info-list">
            <Info label="Customer" value={details.customerName} />
            <Info label="Original Loan Amount" value={money(details.originalLoanAmount)} />
            <Info label="Interest Rate" value={`${details.interestRate ?? details.annualInterestRate ?? 0}%`} />
            <Info label="Tenure" value={`${details.tenureMonths ?? details.loanTenureMonths ?? "-"} months`} />
            <Info label="Start Date" value={date(details.startDate)} />
            <Info label="EMI" value={money(details.emi)} />
            <Info label="Total Interest" value={money(details.totalInterest)} />
            <Info label="Total Paid" value={money(details.totalPaid)} />
            <Info label="Principal Paid" value={money(details.principalPaid)} />
            <Info label="Interest Paid" value={money(details.interestPaid)} />
            <Info label="Remaining Principal" value={money(details.remainingPrincipal)} />
            <Info label="Loan Status" value={<StatusBadge status={status} />} />
          </div>
        </section>

        <section className="panel">
          <div className="panel-header">
            <div>
              <h3>Actions</h3>
              <p>Record scheduled and additional payments</p>
            </div>
          </div>

          <div className="action-stack">
            <button className="primary-button full" onClick={() => setTab("repayment")}>
              Record EMI Repayment
            </button>
            <button className="secondary-button full" onClick={() => setTab("extra")}>
              Add Principal Payment
            </button>
            {schedule.length === 0 && (
              <button className="ghost-button full" onClick={createSchedule} disabled={actionLoading}>
                {actionLoading ? "Generating..." : "Generate EMI Schedule"}
              </button>
            )}
          </div>
        </section>
      </div>

      <section className="panel detail-tabs-panel">
        <div className="tabs">
          <button className={tab === "overview" ? "tab active" : "tab"} onClick={() => setTab("overview")}>Repayment History</button>
          <button className={tab === "schedule" ? "tab active" : "tab"} onClick={() => setTab("schedule")}>EMI Schedule</button>
          <button className={tab === "repayment" ? "tab active" : "tab"} onClick={() => setTab("repayment")}>Make Repayment</button>
          <button className={tab === "extra" ? "tab active" : "tab"} onClick={() => setTab("extra")}>Additional Principal</button>
        </div>

        {tab === "overview" && (
          <HistoryTable history={history} schedule={schedule}/>
        )}

        {tab === "schedule" && (
          <ScheduleTable schedule={schedule} />
        )}

        {tab === "repayment" && (
          <PaymentForm
            title="Record EMI Repayment"
            description="Select an unpaid installment and enter the payment amount."
            schedule={pending}
            value={repayment}
            setValue={setRepayment}
            onSubmit={submitRepayment}
            loading={actionLoading}
          />
        )}

        {tab === "extra" && (
          <ExtraPaymentForm
            value={extra}
            setValue={setExtra}
            onSubmit={submitExtra}
            loading={actionLoading}
          />
        )}
      </section>
    </div>
  );
}

function Summary({ label, value, highlight }) {
  return (
    <div className={`summary-card ${highlight ? "highlight" : ""}`}>
      <span>{label}</span>
      <strong>{value}</strong>
    </div>
  );
}

function Info({ label, value }) {
  return (
    <div className="info-row">
      <span>{label}</span>
      <strong>{value}</strong>
    </div>
  );
}

function HistoryTable({ history, schedule }) {
  if (!history.length) {
    return (
      <div className="empty-state compact">
        <h3>No repayments yet</h3>
        <p>Repayment history will appear here after the first EMI payment.</p>
      </div>
    );
  }

  return (
    <div className="table-wrap">
      <table>
        <thead>
          <tr>
            <th>Date</th>
            <th>EMI</th>
            <th>Payment</th>
            <th>Interest Paid</th>
            <th>Principal Paid</th>
            <th>Remaining</th>
          </tr>
        </thead>
        <tbody>
          {history.map((item) => (
            <tr key={item.id}>
              <td>{date(item.date)}</td>
              <td>#{item.installmentNo}</td>
              <td>{money(item.paymentAmount)}</td>
              <td>{money(item.interestPaid)}</td>
              <td>{money(item.principalPaid)}</td>
              <td>{money(item.remainingPrincipal)}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

function ScheduleTable({ schedule }) {
  if (!schedule.length) {
    return (
      <div className="empty-state compact">
        <h3>No EMI schedule generated</h3>
        <p>Generate the schedule from the Actions panel.</p>
      </div>
    );
  }

  return (
    <div className="table-wrap">
      <table>
        <thead>
          <tr>
            <th>Installment</th>
            <th>Due Date</th>
            <th>EMI</th>
            <th>Interest</th>
            <th>Principal</th>
            <th>Remaining</th>
            <th>Status</th>
          </tr>
        </thead>
        <tbody>
          {schedule.map((item) => (
            <tr key={item.id}>
              <td>#{item.installmentNo}</td>
              <td>{date(item.dueDate)}</td>
              <td>{money(item.emiAmount)}</td>
              <td>{money(item.interest)}</td>
              <td>{money(item.principal)}</td>
              <td>{money(item.remainingPrincipal)}</td>
              <td><StatusBadge status={item.paymentStatus} /></td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

function PaymentForm({ title, description, schedule, value, setValue, onSubmit, loading }) {
  return (
    <div className="payment-form">
      <div className="form-intro">
        <div className="section-icon">₹</div>
        <div>
          <h3>{title}</h3>
          <p>{description}</p>
        </div>
      </div>

      <form onSubmit={onSubmit}>
        <div className="form-grid">
          <label>
            EMI Installment
            <select
              required
              value={value.emiScheduleId}
              onChange={(e) => setValue({ ...value, emiScheduleId: e.target.value })}
            >
              <option value="">Select installment</option>
              {schedule.map((item) => (
                <option key={item.id} value={item.id}>
                  EMI #{item.installmentNo} — {money(item.emiAmount)}
                </option>
              ))}
            </select>
          </label>

          <label>
            Payment Amount
            <input
              required
              type="number"
              min="0.01"
              step="0.01"
              value={value.repaymentAmount}
              onChange={(e) => setValue({ ...value, repaymentAmount: e.target.value })}
            />
          </label>

          <label>
            Payment Date
            <input
              required
              type="date"
              value={value.repaymentDate}
              onChange={(e) => setValue({ ...value, repaymentDate: e.target.value })}
            />
          </label>
        </div>

        <button className="primary-button" disabled={loading || !schedule.length}>
          {loading ? "Saving..." : "Save Repayment"}
        </button>
      </form>
    </div>
  );
}

function ExtraPaymentForm({ value, setValue, onSubmit, loading }) {
  return (
    <div className="payment-form">
      <div className="form-intro">
        <div className="section-icon">+</div>
        <div>
          <h3>Additional Principal Payment</h3>
          <p>This payment immediately reduces the outstanding principal.</p>
        </div>
      </div>

      <form onSubmit={onSubmit}>
        <div className="form-grid">
          <label>
            Extra Principal Amount
            <input
              required
              type="number"
              min="0.01"
              step="0.01"
              value={value.paymentAmount}
              onChange={(e) => setValue({ ...value, paymentAmount: e.target.value })}
            />
          </label>

          <label>
            Payment Date
            <input
              required
              type="date"
              value={value.paymentDate}
              onChange={(e) => setValue({ ...value, paymentDate: e.target.value })}
            />
          </label>
        </div>

        <button className="primary-button" disabled={loading}>
          {loading ? "Saving..." : "Save Additional Payment"}
        </button>
      </form>
    </div>
  );
}