use crate::client::REPORTER_CLIENT;
use anyhow::Result;
use common::errors::report_error::ReportError;

pub async fn register_to_server() -> Result<()> {
    let resp = REPORTER_CLIENT.post::<String>("/api/client/register", None).await?;
    if !resp.is_success() {
        return Err(ReportError::RegisterError(resp.code, resp.message).into());
    }
    Ok(())
}