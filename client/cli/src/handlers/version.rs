use crate::utils::banner;
use anyhow::Result;

pub fn print_version() -> Result<()> {
    banner::print_logo();
    println!();
    println!("BeEyes CLI version: {}", env!("CARGO_PKG_VERSION"));
    Ok(())
}

