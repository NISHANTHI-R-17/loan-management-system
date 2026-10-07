const API_URL = "http://localhost:8081/api/loans";

async function request(path, options = {}) {
  const response = await fetch(`${API_URL}${path}`, {
    credentials: "include",
    headers: {
      "Content-Type": "application/json",
      ...(options.headers || {}),
    },
    ...options,
  });

  const text = await response.text();
  let data = {};
  try {
    data = text ? JSON.parse(text) : {};
  } catch {
    data = { message: text };
  }

  if (response.status === 401) {
    throw new Error("SESSION_EXPIRED");
  }

  if (!response.ok) {
    throw new Error(data.message || "Request failed");
  }

  return data;
}

export const getLoans = () => request("");

export const getLoan = (loanId) => request(`/${loanId}`);

export const createLoan = (payload) =>
  request("", {
    method: "POST",
    body: JSON.stringify(payload),
  });

export const getLoanDetails = (loanId) =>
  request(`/${loanId}/details`);

export const getSchedule = (loanId) =>
  request(`/${loanId}/schedule`);

export const generateSchedule = (loanId) =>
  request(`/${loanId}/schedule`, {
    method: "POST",
  });

export const getRepayments = (loanId) =>
  request(`/${loanId}/repayments`);

export const makeRepayment = (loanId, payload) =>
  request(`/${loanId}/repayments`, {
    method: "POST",
    body: JSON.stringify(payload),
  });

export const getAdditionalPayments = (loanId) =>
  request(`/${loanId}/additional-payments`);

export const makeAdditionalPayment = (loanId, payload) =>
  request(`/${loanId}/additional-payments`, {
    method: "POST",
    body: JSON.stringify(payload),
  });