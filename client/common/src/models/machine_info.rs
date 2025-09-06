use crate::models::metric::{CPUInfo, DiskInfo, MemoryInfo, NetworkInfo, SystemInfo};

pub struct MachineInfo {
    pub system_info: SystemInfo,
    pub cpu_info: CPUInfo,
    pub memory_info: MemoryInfo,
    pub network_info: NetworkInfo,
    pub disk_info: DiskInfo,
}