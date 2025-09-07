use crate::models::{CPUInfo, DiskInfo, MemoryInfo, NetworkInfo, SystemInfo};
use serde::Serialize;

#[derive(Serialize, Debug)]
pub struct MachineInfo {
    system_info: SystemInfo,
    cpu_info: CPUInfo,
    memory_info: MemoryInfo,
    network_info: NetworkInfo,
    disk_info: Vec<DiskInfo>,
}

impl MachineInfo {
    pub fn fetch() -> Self {
        Self {
            system_info: SystemInfo::fetch(),
            cpu_info: CPUInfo::fetch(),
            memory_info: MemoryInfo::fetch(),
            network_info: NetworkInfo::fetch(),
            disk_info: DiskInfo::fetch(),
        }
    }
}

