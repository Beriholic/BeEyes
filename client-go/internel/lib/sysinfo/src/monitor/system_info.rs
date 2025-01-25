use serde::{Deserialize, Serialize};
use sysinfo::System;

#[derive(Debug, Serialize, Deserialize)]
pub struct SystemInfo {
    os_name: String,
    kernel_version: String,
    os_version: String,
}
impl SystemInfo {
    pub fn new() -> SystemInfo {
        SystemInfo {
            os_name: System::name().unwrap(),
            kernel_version: System::kernel_version().unwrap(),
            os_version: System::os_version().unwrap(),
        }
    }
    pub fn to_json(&self) -> String {
        serde_json::to_string(self).unwrap()
    }
}
