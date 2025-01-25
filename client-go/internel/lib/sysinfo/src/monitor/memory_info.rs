
use serde::{Deserialize, Serialize};
use sysinfo::System;

use crate::utils::{byte_to_gb, two_decimals};

#[derive(Debug, Serialize, Deserialize)]
pub struct MemoryInfo {
    total_memory: f64,
    used_memory: f64,
    free_memory: f64,
    percent_memory: f64,

    total_swap: f64,
    used_swap: f64,
    free_swap: f64,
    percent_swap: f64,
}

impl MemoryInfo {
    pub fn new() -> MemoryInfo {
        let mut sys = System::new_all();
        sys.refresh_all();

        let total_memory = byte_to_gb(sys.total_memory());
        let used_memory = byte_to_gb(sys.used_memory());
        let free_memory = byte_to_gb(sys.free_memory());
        let percent_memory = two_decimals(used_memory / total_memory);

        let total_swap = byte_to_gb(sys.total_swap());
        let used_swap = byte_to_gb(sys.used_swap());
        let free_swap = byte_to_gb(sys.free_swap());
        let percent_swap = two_decimals(used_swap / total_swap);

        // 直接返回 MemoryInfo 实例
        MemoryInfo {
            total_memory,
            used_memory,
            free_memory,
            percent_memory,
            total_swap,
            used_swap,
            free_swap,
            percent_swap,
        }
    }

    pub fn to_json(&self) -> String {
        serde_json::to_string(&self).unwrap()
    }
}
