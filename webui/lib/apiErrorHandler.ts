/**
 * API Error Interceptor
 * Handles global API errors, specifically 401 Unauthorized responses
 * by redirecting to the login page.
 */

import { ApiError } from "@/api/core/ApiError";

/**
 * Setup global error handler for API requests
 * This should be called once when the app initializes
 */
export function setupApiErrorHandler() {
  // Store the original Promise.reject
  const originalReject = Promise.reject.bind(Promise);

  // Override Promise.reject to intercept API errors
  Promise.reject = function (reason: any) {
    // Check if this is an ApiError with 401 status
    if (reason instanceof ApiError && reason.status === 401) {
      // Redirect to login page
      if (typeof window !== "undefined") {
        window.location.href = "/login";
      }
    }

    // Call the original reject
    return originalReject(reason);
  };
}

/**
 * Wrapper function to handle API calls with automatic 401 redirect
 * Use this to wrap any API service calls
 */
export async function withApiErrorHandler<T>(apiCall: Promise<T>): Promise<T> {
  try {
    return await apiCall;
  } catch (error) {
    // Check if this is a 401 error
    if (error instanceof ApiError && error.status === 401) {
      // Redirect to login page
      if (typeof window !== "undefined") {
        window.location.href = "/login";
      }
    }
    // Re-throw the error so it can still be handled by the caller
    throw error;
  }
}
