use anyhow::Result;
use inquire::{Confirm, Text};

pub fn config_setting() -> Result<()> {
    let mut config = config::load_config()?;

    let url = Text::new("Server Url")
        .with_default(config.url.as_str())
        .prompt()?;
    let token = Text::new("Token")
        .with_default(config.token.as_str())
        .prompt()?;
    let detail = Confirm::new("是否展示上报数据?")
        .with_default(config.detail)
        .prompt()?;

    config.url = url;
    config.token = token;
    config.detail = detail;

    println!("新配置文件: {:?}", config);

    let confirm_save = Confirm::new("是否保存配置文件?")
        .with_default(false)
        .prompt()?;

    if confirm_save {
        config::save_config(&config)?;
        println!("配置文件已保存")
    } else {
        println!("配置文件未保存")
    }

    Ok(())
}