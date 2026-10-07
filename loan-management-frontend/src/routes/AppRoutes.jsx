import {
  BrowserRouter,
  Navigate,
  Route,
  Routes,
} from "react-router-dom";

import Login from "../modules/auth/pages/Login";
import ProtectedRoute from "../components/ProtectedRoute";
import AppLayout from "../components/AppLayout";
import Dashboard from "../modules/loan/pages/Dashboard";
import LoanList from "../modules/loan/pages/LoanList";
import CreateLoan from "../modules/loan/pages/CreateLoan";
import LoanDetails from "../modules/loan/pages/LoanDetails";

export default function AppRoutes() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/login" element={<Login />} />

        <Route
          element={
            <ProtectedRoute>
              <AppLayout />
            </ProtectedRoute>
          }
        >
          <Route path="/dashboard" element={<Dashboard />} />
          <Route path="/loans" element={<LoanList />} />
          <Route path="/loans/new" element={<CreateLoan />} />
          <Route path="/loans/:loanId" element={<LoanDetails />} />
        </Route>

        <Route path="/" element={<Navigate to="/dashboard" replace />} />
        <Route path="*" element={<Navigate to="/dashboard" replace />} />
      </Routes>
    </BrowserRouter>
  );
}