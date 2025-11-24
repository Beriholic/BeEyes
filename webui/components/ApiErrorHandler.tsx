"use client";

import { useEffect } from "react";
import { ApiError } from "@/api/core/ApiError";

/**
 * Global API Error Handler Component
 * Intercepts 401 errors and redirects to login page
 */
export function ApiErrorHandler() {
  useEffect(() => {
    // Create a global error handler for unhandled promise rejections
    const handleUnhandledRejection = (event: PromiseRejectionEvent) => {
      const error = event.reason;

      // Check if this is a 401 API error
      if (error instanceof ApiError && error.status === 401) {
        // Prevent the default error handling
        event.preventDefault();

        // Redirect to login page
        window.location.href = "/login";
      }
    };

    // Add the event listener
    window.addEventListener("unhandledrejection", handleUnhandledRejection);

    // Cleanup on unmount
    return () => {
      window.removeEventListener(
        "unhandledrejection",
        handleUnhandledRejection
      );
    };
  }, []);

  // This component doesn't render anything
  return null;
}
