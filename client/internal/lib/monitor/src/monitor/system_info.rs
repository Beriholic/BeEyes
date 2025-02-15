use serde::{Deserialize, Serialize};
use sysinfo::System;

#[derive(Debug, Serialize, Deserialize)]
pub struct SystemInfo {
    os_name: String,
    kernel_version: String,
    os_version: String,
    cpu_arch: String,
}
impl SystemInfo {
    pub fn new() -> SystemInfo {
        SystemInfo {
            os_name: System::name().unwrap_or("unknown".to_owned()),
            kernel_version: System::kernel_version().unwrap_or("unknown".to_owned()),
            os_version: System::os_version().unwrap_or("unknown".to_owned()),
            cpu_arch: System::cpu_arch(),
        }
    }
    pub fn to_json(&self) -> String {
        serde_json::to_string(self).unwrap()
    }
}
