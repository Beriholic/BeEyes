use anyhow::Result;
use common::utils::json_utils;
use std::time::Duration;
use tklog::{error, info};

pub async fn report_to_server() -> Result<()> {
    register_to_server().await?;
    reporter_machine_info().await?;
    reporter_runtime_info().await?;
    Ok(())
}

async fn register_to_server() -> Result<()> {
    info!("开始注册到中心");
    reporter::apis::register_to_server().await?;
    info!("成功注册到中心");
    Ok(())
}

async fn reporter_machine_info() -> Result<()> {
    info!("开始上报机器数据");
    let machine_info = metric::machine_info::MachineInfo::fetch();
    reporter::apis::report_machine_info(&machine_info).await?;
    info!("上报机器数据成功", json_utils::to_json(&machine_info)?);
    Ok(())
}

async fn reporter_runtime_info() -> Result<()> {
    let mut interval = tokio::time::interval(Duration::from_secs(2));
    info!("开始上报运行时数据");
    loop {
        interval.tick().await;
        let runtime_info = metric::runtime_info::RuntimeInfo::fetch();

        match reporter::apis::report_runtime_info(&runtime_info).await {
            Ok(_) => {
                info!("上报运行时数据成功", json_utils::to_json(&runtime_info)?);
            }
            Err(e) => {
                error!("上报运行时数据失败", e);
            }
        }
    }
}