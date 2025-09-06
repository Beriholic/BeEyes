use anyhow::Result;

pub async fn report_to_server() -> Result<()> {
    reporter::apis::register_to_server().await?;
    println!("注册成功！");
    Ok(())
}