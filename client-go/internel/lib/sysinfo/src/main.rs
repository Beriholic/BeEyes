use std::env;

use monitor::{memory_info::MemoryInfo, system_info::SystemInfo};

mod monitor;
mod utils;

fn main() {
    let args: Vec<String> = env::args().collect();

    if let Some(arg) = args.get(1).map(|s| s.as_str()) {
        match arg {
            "system" => {
                println!("{}", SystemInfo::new().to_json())
            }
            "memory" => {
                println!("{}", MemoryInfo::new().to_json())
            }
            _ => {}
        }
    }
}
