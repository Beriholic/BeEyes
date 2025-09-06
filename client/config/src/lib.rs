mod manage;

use serde::{Deserialize, Serialize};

#[derive(Debug, Serialize, Deserialize)]
pub struct BeEyesConfig {
    pub url: String,
    pub token: String,
}

impl BeEyesConfig {
    pub fn default() -> Self {
        Self {
            url: "".to_string(),
            token: "".to_string(),
        }
    }
}

pub use manage::{load_config, save_config, verify_config};


