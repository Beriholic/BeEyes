//! CLI-related models for command-line interface

use clap::{Parser, Subcommand};
use clap_complete::Shell;

/// Main CLI application structure
#[derive(Parser, Debug)]
#[command(
    author = "Beriholic",
    version,
    about = "BeEyes CLI",
    long_about = None
)]
#[command(propagate_version = true)]
pub struct Cli {
    #[command(subcommand)]
    pub command: Commands,
}

/// Available CLI commands
#[derive(Subcommand, Debug)]
pub enum Commands {
    /// Run the BeEyes monitoring service
    Run,
    /// Generate shell completion scripts
    Completion(CompletionArgs),
    /// Manage configuration
    Config,
    /// Show version information
    Version,
}

/// Arguments for shell completion generation
#[derive(Parser, Debug)]
pub struct CompletionArgs {
    #[arg(value_enum)]
    pub shell: Shell,
}

/// Arguments for server registration (deprecated - command removed)
#[derive(Parser, Debug)]
pub struct RegisterArgs {
    #[arg(value_name = "SERVER_URL")]
    pub server_url: String,
}