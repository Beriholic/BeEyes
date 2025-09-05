use sysinfo::System;

#[derive(Debug)]
pub struct SystemInfo {
    pub system_name: String,
    pub kernal_version: String,
    pub os_version: String,
    pub host_name: String,
    pub cpu_count: usize,
    pub total_memory: u64,
    pub total_swap: u64,
}
impl SystemInfo {
    pub fn new(
        system_name: String,
        kernal_version: String,
        os_version: String,
        host_name: String,
        cpu_count: usize,
        total_memory: u64,
        total_swap: u64,
    ) -> Self {
        Self {
            system_name,
            kernal_version,
            os_version,
            host_name,
            cpu_count,
            total_memory,
            total_swap,
        }
    }
}
pub fn get_system_info() -> SystemInfo {
    let system_info = System::new_all();
    let system_info = SystemInfo::new(
        System::name().unwrap_or("Unknown".to_owned()),
        System::kernel_version().unwrap_or("Unknown".to_owned()),
        System::os_version().unwrap_or("Unknown".to_owned()),
        System::host_name().unwrap_or("Unknown".to_owned()),
        system_info.cpus().len(),
        system_info.total_memory(),
        system_info.total_swap(),
    );
    system_info
}