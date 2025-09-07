use serde::Serialize;
use std::collections::HashSet;
use sysinfo::{Disks, Networks, System};

#[derive(Serialize, Debug)]
pub struct CPUInfo {
    pub name: String,
    pub core_count: usize,
    pub usage: f32,
}
impl CPUInfo {
    pub fn fetch() -> Self {
        let mut system = System::new_all();
        system.refresh_cpu_all();
        Self {
            name: system.cpus().first().map_or("Unknown".to_owned(), |cpu| cpu.brand().to_owned()),
            core_count: system.cpus().len(),
            usage: system.cpus().first().map_or(0.0, |cpu| cpu.cpu_usage()),
        }
    }
}

#[derive(Serialize, Debug)]
pub struct MemoryInfo {
    pub total_memory: u64,
    pub used_memory: u64,
    pub free_memory: u64,
    pub total_swap: u64,
    pub used_swap: u64,
    pub free_swap: u64,
    pub percent_memory: f64,
    pub percent_swap: f64,
}

impl MemoryInfo {
    pub fn fetch() -> Self {
        let mut system = System::new_all();
        system.refresh_all();

        let total_memory = system.total_memory();
        let used_memory = system.used_memory();
        let free_memory = total_memory.saturating_sub(used_memory);

        let total_swap = system.total_swap();
        let used_swap = system.used_swap();
        let free_swap = total_swap.saturating_sub(used_swap);

        let percent_memory = if total_memory > 0 {
            (used_memory as f64 / total_memory as f64) * 100.0
        } else {
            0.0
        };

        let percent_swap = if total_swap > 0 {
            (used_swap as f64 / total_swap as f64) * 100.0
        } else {
            0.0
        };

        Self {
            total_memory,
            used_memory,
            free_memory,
            total_swap,
            used_swap,
            free_swap,
            percent_memory,
            percent_swap,
        }
    }
}

#[derive(Serialize, Debug)]
pub struct DiskInfo {
    pub name: String,
    pub file_system: String,
    pub total: u64,
    pub used: u64,
    pub free: u64,
    pub percent: f64,
    pub kind: String,
}

impl DiskInfo {
    pub fn fetch() -> Vec<Self> {
        let disks = Disks::new_with_refreshed_list();
        let mut seen = HashSet::new();
        disks.list().iter().filter_map(|disk| {
            let name = disk.name().to_str().unwrap_or("Unknown").to_owned();
            let total = disk.total_space();
            let free = disk.available_space();
            let used = total.saturating_sub(free);

            if !seen.insert((name.clone(), total, free, used)) {
                return None;
            }

            let file_system = disk.file_system().to_str().unwrap_or("Unknown").to_owned();
            let percent = used as f64 / total as f64;
            let kind = disk.kind().to_string();
            Some(DiskInfo {
                name,
                file_system,
                total,
                used,
                free,
                percent,
                kind,
            })
        }).collect()
    }
}


#[derive(Serialize, Debug)]
pub struct NetworkInterfaceInfo {
    pub name: String,
    pub ipv4: Vec<String>,
    pub ipv6: Vec<String>,
    pub upload_speed: u64,
    pub download_speed: u64,
}

#[derive(Serialize, Debug)]
pub struct NetworkInfo {
    interfaces: Vec<NetworkInterfaceInfo>,
}

impl NetworkInfo {
    pub fn fetch() -> Self {
        let networks = Networks::new_with_refreshed_list();
        let mut interfaces = Vec::new();

        for (interface_name, network) in &networks {
            let mut ipv4 = Vec::new();
            let mut ipv6 = Vec::new();

            network.ip_networks().iter().for_each(|ip| {
                if ip.addr.is_loopback() {
                    return;
                }
                if ip.addr.is_ipv4() {
                    ipv4.push(ip.to_string());
                }
                if ip.addr.is_ipv6() {
                    ipv6.push(ip.to_string());
                }
            });

            interfaces.push(
                NetworkInterfaceInfo {
                    name: interface_name.to_owned(),
                    upload_speed: network.total_transmitted(),
                    download_speed: network.total_received(),
                    ipv4,
                    ipv6,
                }
            )
        }

        Self {
            interfaces,
        }
    }
}

#[derive(Serialize, Debug)]
pub struct SystemInfo {
    pub os_name: String,
    pub kernel_version: String,
    pub os_version: String,
    pub cpu_arch: String,
    pub host_name: String,
}

impl SystemInfo {
    pub fn fetch() -> Self {
        SystemInfo {
            os_name: System::name().unwrap_or("Unknown".to_owned()),
            kernel_version: System::kernel_version().unwrap_or("Unknown".to_owned()),
            os_version: System::os_version().unwrap_or("Unknown".to_owned()),
            cpu_arch: System::cpu_arch(),
            host_name: System::host_name().unwrap_or("Unknown".to_owned()),
        }
    }
}