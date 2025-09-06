pub struct SystemInfo {
    pub os_name: String,
    pub os_version: String,
    pub kernel_version: String,
    pub cpu_arch: String,
}
pub struct CPUInfo {
    pub name: String,
    pub core_count: usize,
    pub usage: f64,
}
pub struct MemoryInfo {
    pub total_memory: f64,
    pub used_memory: f64,
    pub free_memory: f64,
    pub total_swap: f64,
    pub used_swap: f64,
    pub free_swap: f64,
    pub percent_memory_usage: f64,
}
pub struct NetworkInfo {
    pub interfaces: Vec<NetworkInterface>,
}
pub struct NetworkInterface {
    pub name: String,
    pub ipv4: Vec<String>,
    pub ipv6: Vec<String>,
    pub upload_speed: f64,
    pub download_speed: f64,
}

pub struct DiskInfo {
    pub total: f64,
    pub used: f64,
    pub free: f64,
    pub percent: f64,
}
