//! System metrics and monitoring models

use serde::Serialize;
use std::collections::HashSet;
use std::thread;
use std::time::Duration;
use sysinfo::{Disks, Networks, System};
use std::sync::Mutex;

/// Global System instance for CPU monitoring
/// Initialized once, CPU usage requires baseline + subsequent reads
static SYSTEM: Mutex<Option<System>> = Mutex::new(None);

/// Initialize the global System instance with CPU baseline
/// Must be called before fetching CPU usage
pub fn init_system() {
    let mut system = System::new_all();
    system.refresh_cpu_all();
    // First refresh establishes baseline, sleep then refresh for real values
    thread::sleep(Duration::from_millis(200));
    system.refresh_cpu_all();
    *SYSTEM.lock().unwrap() = Some(system);
}

/// Refresh and get CPU usage percentage
/// Returns 0.0 if system not initialized
fn get_cpu_usage() -> f32 {
    let mut system = SYSTEM.lock().unwrap();
    if let Some(ref mut sys) = *system {
        sys.refresh_cpu_all();
        sys.cpus().first().map_or(0.0, |cpu| cpu.cpu_usage())
    } else {
        0.0
    }
}

/// System information including OS details
#[derive(Serialize, Debug, Clone)]
pub struct SystemInfo {
    pub os_name: String,
    pub kernel_version: String,
    pub os_version: String,
    pub cpu_arch: String,
    pub hostname: String,
}

impl SystemInfo {
    pub fn fetch() -> Self {
        SystemInfo {
            os_name: System::name().unwrap_or_else(|| "Unknown".to_owned()),
            kernel_version: System::kernel_version().unwrap_or_else(|| "Unknown".to_owned()),
            os_version: System::os_version().unwrap_or_else(|| "Unknown".to_owned()),
            cpu_arch: System::cpu_arch(),
            hostname: System::host_name().unwrap_or_else(|| "Unknown".to_owned()),
        }
    }
}

/// CPU information and usage statistics
#[derive(Serialize, Debug, Clone)]
pub struct CPUInfo {
    pub name: String,
    pub core_count: usize,
    pub usage: f32,
}

impl CPUInfo {
    pub fn fetch() -> Self {
        let mut system = SYSTEM.lock().unwrap();
        let (name, core_count) = if let Some(ref mut sys) = *system {
            (
                sys.cpus().first().map_or("Unknown".to_owned(), |cpu| cpu.brand().to_owned()),
                sys.cpus().len(),
            )
        } else {
            // Fallback if not initialized
            let temp_system = System::new_all();
            (
                temp_system.cpus().first().map_or("Unknown".to_owned(), |cpu| cpu.brand().to_owned()),
                temp_system.cpus().len(),
            )
        };
        drop(system); // Release lock before getting CPU usage

        Self {
            name,
            core_count,
            usage: get_cpu_usage(),
        }
    }
}

/// Memory information including RAM and swap
#[derive(Serialize, Debug, Clone)]
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

/// Disk information and usage statistics
#[derive(Serialize, Debug, Clone)]
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
        disks
            .list()
            .iter()
            .filter_map(|disk| {
                let name = disk.name().to_str().unwrap_or("Unknown").to_owned();
                let total = disk.total_space();
                let free = disk.available_space();
                let used = total.saturating_sub(free);

                if !seen.insert((name.clone(), total, free, used)) {
                    return None;
                }

                let file_system = disk.file_system().to_str().unwrap_or("Unknown").to_owned();
                let percent = if total > 0 {
                    used as f64 / total as f64
                } else {
                    0.0
                };
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
            })
            .collect()
    }
}

/// Network interface information
#[derive(Serialize, Debug, Clone)]
pub struct NetworkInterfaceInfo {
    pub name: String,
    pub ipv4: Vec<String>,
    pub ipv6: Vec<String>,
    pub upload_speed: f64,   // bytes per second
    pub download_speed: f64, // bytes per second
}

/// Network information containing all interfaces
#[derive(Serialize, Debug, Clone)]
pub struct NetworkInfo {
    pub interfaces: Vec<NetworkInterfaceInfo>,
}

impl NetworkInfo {
    pub fn fetch() -> Self {
        let networks = Networks::new_with_refreshed_list();
        let mut interfaces = Vec::new();

        // First measurement
        let mut first_measurements: std::collections::HashMap<String, (u64, u64)> =
            std::collections::HashMap::new();

        for (interface_name, network) in &networks {
            first_measurements.insert(
                interface_name.clone(),
                (network.total_transmitted(), network.total_received()),
            );
        }

        // Sleep for 1 second to measure speed
        thread::sleep(Duration::from_secs(1));

        // Refresh and take second measurement
        let networks = Networks::new_with_refreshed_list();

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

            let current_transmitted = network.total_transmitted();
            let current_received = network.total_received();

            // Calculate speeds based on 1-second difference
            let (upload_speed, download_speed) = if let Some((prev_transmitted, prev_received)) =
                first_measurements.get(interface_name)
            {
                let upload_diff = current_transmitted.saturating_sub(*prev_transmitted) as f64;
                let download_diff = current_received.saturating_sub(*prev_received) as f64;

                (
                    upload_diff,   // bytes per second (over 1 second)
                    download_diff, // bytes per second (over 1 second)
                )
            } else {
                (0.0, 0.0)
            };

            interfaces.push(NetworkInterfaceInfo {
                name: interface_name.to_owned(),
                upload_speed,
                download_speed,
                ipv4,
                ipv6,
            });
        }

        Self { interfaces }
    }
}

/// Complete machine information for initial registration
#[derive(Serialize, Debug, Clone)]
pub struct MachineInfo {
    pub system_info: SystemInfo,
    pub cpu_info: CPUInfo,
    pub memory_info: MemoryInfo,
    pub network_info: NetworkInfo,
    pub disk_info: Vec<DiskInfo>,
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

/// Runtime metrics for periodic monitoring
#[derive(Serialize, Debug, Clone)]
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
