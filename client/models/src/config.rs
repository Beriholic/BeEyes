//! Configuration models for the BeEyes application

use serde::{Deserialize, Serialize};

/// Main configuration structure for BeEyes
#[derive(Debug, Serialize, Deserialize)]
pub struct BeEyesConfig {
    /// Server URL for API communication
    pub url: String,
    /// Authentication token
    pub token: String,
    /// Whether to include detailed information in reports
    pub detail: bool,
}

impl Default for BeEyesConfig {
    fn default() -> Self {
        Self {
            url: String::new(),
            token: String::new(),
            detail: false,
        }
    }
}