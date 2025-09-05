use anyhow::Result;

pub async fn report_to_server() -> Result<()> {
    reporter::start_scheduler().await?;
    tokio::signal::ctrl_c().await?;
    Ok(())
}