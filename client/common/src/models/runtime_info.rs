use crate::models::metric::{CPUInfo, DiskInfo, MemoryInfo, NetworkInfo};

pub struct RuntimeInfo {
    pub timestamp: i64,
    pub cpu_info: CPUInfo,
    pub memory_info: MemoryInfo,
    pub disk_info: DiskInfo,
    pub network_info: NetworkInfo,
}
