const API_URL = "http://localhost:8081/api/auth";

async function readResponse(response) {
  const text = await response.text();
  let data = {};
  try {
    data = text ? JSON.parse(text) : {};
  } catch {
    data = { message: text };
  }

  if (!response.ok) {
    throw new Error(data.message || "Request failed");
  }

  return data;
}

export async function loginUser(username, password) {
  const response = await fetch(`${API_URL}/login`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    credentials: "include",
    body: JSON.stringify({ username, password }),
  });

  return readResponse(response);
}

export async function logoutUser() {
  const response = await fetch(`${API_URL}/logout`, {
    method: "POST",
    credentials: "include",
  });

  return readResponse(response);
}

export async function getCurrentUser() {
  const response = await fetch(`${API_URL}/me`, {
    method: "GET",
    credentials: "include",
  });

  if (!response.ok) return null;
  return response.json();
}