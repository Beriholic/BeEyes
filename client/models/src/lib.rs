//! Centralized models for the BeEyes client application
//!
//! This crate consolidates all data models used throughout the application,
//! organized by domain for better maintainability and clarity.

pub mod cli;
pub mod config;
pub mod errors;
pub mod api;
pub mod system;

// Re-export commonly used types for convenience
pub use cli::{Cli, Commands, CompletionArgs, RegisterArgs};
pub use config::BeEyesConfig;
pub use errors::{BeEyesError, ReportError, BeEyesConfigValidationError};
pub use api::RestResp;
pub use system::{
    SystemInfo, CPUInfo, MemoryInfo, DiskInfo, NetworkInfo, NetworkInterfaceInfo,
    MachineInfo, RuntimeInfo
};