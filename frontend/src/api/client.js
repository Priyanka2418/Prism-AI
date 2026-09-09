const BASE_URL = import.meta.env.VITE_API_BASE_URL || "/api/v1";

/**
 * Sanitizes error messages before displaying them to the user.
 * If the message looks like a raw database / stack trace leak,
 * replace it with a generic user-friendly string.
 */
function sanitizeErrorMessage(message) {
  if (!message || typeof message !== "string") {
    return "An unexpected error occurred. Please try again.";
  }
  const technicalPatterns = [
    /ERROR:/i,
    /constraint/i,
    /violates/i,
    /Detail:/i,
    /SQL \[/i,
    /could not execute statement/i,
    /relation "/i,
    /column "/i,
    /Failing row contains/i,
    /org\.hibernate/i,
    /org\.springframework/i,
    /java\./i,
    /com\.aimock/i,
    /JDBC/i,
    /HibernateJdbcException/i,
  ];
  const looksLikeTechnical = technicalPatterns.some((pattern) =>
    pattern.test(message)
  );
  if (looksLikeTechnical) {
    return "An unexpected error occurred. Please try again.";
  }
  return message;
}

export async function request(endpoint, options = {}) {
  // Support both relative /api/v1 prefix and direct fallback if needed
  let url = endpoint.startsWith("http") ? endpoint : `${BASE_URL}${endpoint}`;
  
  const headers = {
    "Content-Type": "application/json",
    ...(options.headers || {}),
  };

  const token =
    sessionStorage.getItem("prism_access_token") ||
    localStorage.getItem("prism_access_token");
  if (token && !headers["Authorization"]) {
    headers["Authorization"] = `Bearer ${token}`;
  }

  const config = {
    ...options,
    headers,
    credentials: "include",
  };

  let response;
  try {
    response = await fetch(url, config);
  } catch (netErr) {
    // If relative proxy failed or direct port needed, retry once with direct URL
    if (!endpoint.startsWith("http") && !url.includes(":8080")) {
      try {
        const directUrl = `http://localhost:8080/api/v1${endpoint}`;
        response = await fetch(directUrl, config);
      } catch {
        throw new Error("Unable to connect to server. Please ensure the backend is running.");
      }
    } else {
      throw new Error("Unable to connect to server. Please ensure the backend is running.");
    }
  }

  if (response.status === 204) {
    return null;
  }

  const contentType = response.headers.get("content-type");
  let data = null;
  if (contentType && contentType.includes("application/json")) {
    try {
      data = await response.json();
    } catch {
      data = null;
    }
  } else {
    try {
      data = await response.text();
    } catch {
      data = null;
    }
  }

  if (!response.ok) {
    const rawMessage =
      (data && typeof data === "object" && (data.message || data.error)) ||
      (typeof data === "string" && data) ||
      `Request failed with status ${response.status}`;

    const errorMessage = sanitizeErrorMessage(rawMessage);
    const error = new Error(errorMessage);
    error.status = response.status;
    error.data = data;
    throw error;
  }

  return data;
}

export default {
  get: (endpoint, options) => request(endpoint, { ...options, method: "GET" }),
  post: (endpoint, body, options) =>
    request(endpoint, { ...options, method: "POST", body: JSON.stringify(body) }),
  put: (endpoint, body, options) =>
    request(endpoint, { ...options, method: "PUT", body: JSON.stringify(body) }),
  patch: (endpoint, body, options) =>
    request(endpoint, { ...options, method: "PATCH", body: body ? JSON.stringify(body) : undefined }),
  delete: (endpoint, options) => request(endpoint, { ...options, method: "DELETE" }),
};
