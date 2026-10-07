import { Navigate, useLocation } from "react-router-dom";
import { useEffect, useState } from "react";
import { getCurrentUser } from "../modules/auth/services/authService";

export default function ProtectedRoute({ children }) {
  const [status, setStatus] = useState("checking");
  const location = useLocation();

  useEffect(() => {
    let active = true;

    getCurrentUser()
      .then((user) => {
        if (!active) return;
        setStatus(user ? "authenticated" : "unauthenticated");
      })
      .catch(() => {
        if (active) setStatus("unauthenticated");
      });

    return () => {
      active = false;
    };
  }, [location.pathname]);

  if (status === "checking") {
    return <div className="screen-center">Checking session...</div>;
  }

  if (status === "unauthenticated") {
    return <Navigate to="/login" replace state={{ from: location.pathname }} />;
  }

  return children;
}