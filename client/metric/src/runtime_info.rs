use crate::models::{CPUInfo, DiskInfo, MemoryInfo, NetworkInfo};
use serde::Serialize;

#[derive(Serialize, Debug)]
pub struct RuntimeInfo {
    pub timestamp: i64,
    pub cpu_info: CPUInfo,
    pub memory_info: MemoryInfo,
    pub disk_info: Vec<DiskInfo>,
    pub network_info: NetworkInfo,
}

impl RuntimeInfo {
    pub fn fetch() -> Self {
        Self {
            timestamp: chrono::Utc::now().timestamp_millis(),
            cpu_info: CPUInfo::fetch(),
            memory_info: MemoryInfo::fetch(),
            disk_info: DiskInfo::fetch(),
            network_info: NetworkInfo::fetch(),
        }
    }
}
