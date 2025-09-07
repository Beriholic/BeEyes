use clap::{Parser, Subcommand};
use clap_complete::Shell;

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

#[derive(Subcommand, Debug)]
pub enum Commands {
    Run,
    Completion(CompletionArgs),
    Config,
    Version,
}

#[derive(Parser, Debug)]
pub struct CompletionArgs {
    #[arg(value_enum)]
    pub shell: Shell,
}

#[derive(Parser, Debug)]
pub struct RegisterArgs {
    #[arg(value_name = "SERVER_URL")]
    pub server_url: String,
}

