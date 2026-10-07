import { useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import { loginUser } from "../services/authService";

export default function Login() {
  const navigate = useNavigate();
  const location = useLocation();

  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const submit = async (event) => {
    event.preventDefault();
    setError("");

    if (!username.trim() || !password.trim()) {
      setError("Please enter username and password.");
      return;
    }

    setLoading(true);

    try {
      await loginUser(username.trim(), password);
      const destination = location.state?.from || "/dashboard";
      navigate(destination, { replace: true });
    } catch (err) {
      setError(err.message || "Invalid username or password.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="login-page">
      <div className="login-decoration decoration-one" />
      <div className="login-decoration decoration-two" />

      <div className="login-card">
        <div className="login-brand">
          <div className="login-mark">₹</div>
          <div>
            <strong>LoanFlow</strong>
            <span>Loan Management System</span>
          </div>
        </div>

        <div className="login-heading">
          <h1>Welcome back</h1>
          <p>Sign in to manage your loan portfolio.</p>
        </div>

        <form onSubmit={submit} className="login-form">
          <label>
            Username
            <input
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              placeholder="Enter username"
              autoComplete="username"
            />
          </label>

          <label>
            Password
            <input
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              placeholder="Enter password"
              autoComplete="current-password"
            />
          </label>

          {error && <div className="alert error-alert">{error}</div>}

          <button className="primary-button login-submit" disabled={loading}>
            {loading ? "Signing in..." : "Sign In"}
          </button>
        </form>

        <div className="login-hint">
          <span>Demo credentials</span>
          <strong>admin / admin123</strong>
        </div>
      </div>
    </div>
  );
}