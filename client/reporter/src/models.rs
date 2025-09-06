use serde::{Deserialize, Serialize};

#[derive(Serialize, Deserialize)]
pub struct RestResp {
    pub code: u16,
    pub message: String,
}

impl RestResp {
    pub fn is_success(&self) -> bool {
        self.code == 200
    }
}

