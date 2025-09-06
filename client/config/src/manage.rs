use crate::BeEyesConfig;
use anyhow::{Context, Result};
use std::fs::{self, File};
use std::io::{Read, Write};
use std::path::Path;

const CONFIG_FILE_PATH: &str = "$HOME/.config/beeyes/config.toml";

pub fn save_config(config: &BeEyesConfig) -> Result<()> {
    let config_path = get_config_path()?;

    // 确保配置目录存在
    if let Some(parent) = Path::new(&config_path).parent() {
        fs::create_dir_all(parent).context("无法创建配置目录")?;
    }

    let config_str = toml::to_string(&config).context("无法序列化配置")?;
    let mut file = File::create(&config_path).context("无法创建配置文件")?;
    file.write_all(config_str.as_bytes()).context("无法写入配置文件")?;

    Ok(())
}

pub fn load_config() -> Result<BeEyesConfig> {
    let config_path = get_config_path()?;

    // 检查文件是否存在
    if !Path::new(&config_path).exists() {
        let default_config = BeEyesConfig::default();
        save_config(&default_config).context("无法创建默认配置文件")?;
        return Ok(default_config);
    }

    let mut file = File::open(&config_path).context("无法打开配置文件")?;
    let mut contents = String::new();
    file.read_to_string(&mut contents).context("无法读取配置文件")?;

    let config: BeEyesConfig = toml::from_str(&contents).context("无法解析配置文件")?;

    Ok(config)
}

pub fn verify_config() -> Result<bool> {
    let config = load_config()?;

    // 验证token不为空
    if config.token.trim().is_empty() {
        return Ok(false);
    }

    // 验证URL格式
    if config.url.trim().is_empty() {
        return Ok(false);
    }

    Ok(true)
}

fn get_config_path() -> Result<String> {
    let home = std::env::var("HOME").context("无法获取HOME环境变量")?;
    let config_path = CONFIG_FILE_PATH.replace("$HOME", &home);
    Ok(config_path)
}
