use crate::client::REPORTER_CLIENT;
use anyhow::Result;
use common::errors::report_error::ReportError;
use metric::machine_info::MachineInfo;
use metric::runtime_info::RuntimeInfo;
use tklog::error;

pub async fn register_to_server() -> Result<(), ReportError> {
    let resp = REPORTER_CLIENT.post::<String>("/api/client/register", None).await?;
    if !resp.is_success() {
        error!("注册失败: ", resp.message);
        return Err(ReportError::RegisterError(resp.code, resp.message));
    }
    Ok(())
}

pub async fn report_machine_info(machine_info: &MachineInfo) -> Result<(), ReportError> {
    let resp = REPORTER_CLIENT
        .post("/api/client/report/machine", Some(machine_info))
        .await?;
    if !resp.is_success() {
        return Err(ReportError::ReportMachineInfoError(resp.code, resp.message));
    }
    Ok(())
}

pub async fn report_runtime_info(runtime_info: &RuntimeInfo) -> Result<(), ReportError> {
    let resp = REPORTER_CLIENT
        .post("/api/client/report/runtime", Some(runtime_info))
        .await?;
    if !resp.is_success() {
        return Err(ReportError::ReportRuntimeInfoError(resp.code, resp.message));
    }
    Ok(())
}