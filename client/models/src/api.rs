//! API response models

use serde::{Deserialize, Serialize};

/// Standard REST API response structure
#[derive(Serialize, Deserialize, Debug, Clone)]
pub struct RestResp {
    /// HTTP status code
    pub code: u16,
    /// Response message
    pub message: String,
}

impl RestResp {
    /// Create a new successful response
    pub fn success() -> Self {
        Self {
            code: 0,
            message: "Success".to_string(),
        }
    }

    /// Create a new error response
    pub fn error(code: u16, message: impl Into<String>) -> Self {
        Self {
            code,
            message: message.into(),
        }
    }

    /// Check if the response indicates success
    pub fn is_success(&self) -> bool {
        self.code == 0
    }

    /// Check if the response indicates an error
    pub fn is_error(&self) -> bool {
        self.code != 0
    }
}