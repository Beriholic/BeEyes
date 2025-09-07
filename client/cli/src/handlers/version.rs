use anyhow::Result;

pub fn print_version() -> Result<()> {
    println!();
    println!("BeEyes CLI version: {}", env!("CARGO_PKG_VERSION"));
    Ok(())
}

