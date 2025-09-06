use crate::models::RestResp;
use anyhow::Result;
use common::errors::report_error::ReportError;
use config::BeEyesConfig;
use once_cell::sync::Lazy;
use reqwest::Client;
use serde::Serialize;
use std::sync::Arc;

pub struct ReporterClient {
    client: Client,
    config: BeEyesConfig,
}

impl ReporterClient {
    fn new(client: Client, config: BeEyesConfig) -> Self {
        Self {
            client,
            config,
        }
    }

    pub async fn post<T: Serialize>(&self, url: &str, body: Option<T>) -> Result<RestResp> {
        let req = self.client
            .post(format!("{}{}", self.config.url, url))
            .header("Content-Type", "application/json")
            .header("Authorization", &self.config.token);
        let req = match body {
            Some(body) => req.json(&body),
            None => req,
        };
        let resp = req.send().await?;

        if !resp.status().is_success() {
            return Err(ReportError::RequestError.into());
        }

        let resp = resp.text().await?;
        let resp = serde_json::from_str(&resp)?;
        Ok(resp)
    }
}

pub static REPORTER_CLIENT: Lazy<Arc<ReporterClient>> = Lazy::new(|| {
    let config = match config::load_config() {
        Ok(config) => { config }
        Err(e) => {
            eprintln!("加载配置文件失败: {}", e);
            std::process::exit(1);
        }
    };
    let client = Client::new();
    Arc::new(ReporterClient::new(client, config))
});