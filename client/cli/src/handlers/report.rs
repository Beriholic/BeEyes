use anyhow::Result;
use common::utils::json_utils;
use std::time::Duration;
use tklog::{error, info};
use tokio::time;

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
    let config = config::load_config()?;
    let machine_info = metric::machine_info::MachineInfo::fetch();
    info!("开始上报机器数据");
    reporter::apis::report_machine_info(&machine_info).await?;

    match config.detail {
        true => {
            info!("上报机器数据成功", json_utils::to_json(&machine_info)?);
        }
        false => {
            info!("上报机器数据成功");
        }
    }
    Ok(())
}

async fn reporter_runtime_info() -> Result<()> {
    let config = config::load_config()?;
    let mut interval = time::interval(Duration::from_secs(3));
    info!("3秒后开始上报机器运行数据.....");
    interval.tick().await;
    loop {
        interval.tick().await;
        let runtime_info = metric::runtime_info::RuntimeInfo::fetch();
        match reporter::apis::report_runtime_info(&runtime_info).await {
            Ok(_) => match config.detail {
                true => {
                    info!("上报运行时数据成功", json_utils::to_json(&runtime_info)?);
                }
                false => {
                    info!("上报运行时数据成功");
                }
            }
            Err(e) => {
                error!("上报运行时数据失败", e);
            }
        }
    }
}