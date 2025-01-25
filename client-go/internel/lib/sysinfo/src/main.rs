use serde::{Deserialize, Serialize};
use sysinfo::System;

#[derive(Debug, Serialize, Deserialize)]
struct SystemInfo {
    name: String,
    kernel_version: String,
    os_version: String,
    host_name: String,
}
impl SystemInfo {
    fn new() -> SystemInfo {
        let mut sys = System::new_all();
        sys.refresh_all();
        SystemInfo {
            name: System::name().unwrap(),
            kernel_version: System::kernel_version().unwrap(),
            os_version: System::os_version().unwrap(),
            host_name: System::host_name().unwrap(),
        }
    }
    fn to_json(&self) -> String {
        serde_json::to_string(self).unwrap()
    }
}

fn main() {
    let sysinfo = SystemInfo::new();
    println!("{}", sysinfo.to_json());
}
