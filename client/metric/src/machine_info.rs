use crate::models::{CPUInfo, MemoryInfo, NetworkInfo, SystemInfo};
use serde::Serialize;

#[derive(Serialize, Debug)]
pub struct MachineInfo {
    system_info: SystemInfo,
    cpu_info: CPUInfo,
    memory_info: MemoryInfo,
    network_info: NetworkInfo,
}

impl MachineInfo {
    pub fn fetch() -> Self {
        Self {
            system_info: SystemInfo::fetch(),
            cpu_info: CPUInfo::fetch(),
            memory_info: MemoryInfo::fetch(),
            network_info: NetworkInfo::fetch(),
        }
    }
}

