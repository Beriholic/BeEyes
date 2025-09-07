use crate::models::RestResp;
use anyhow::Result;
use common::errors::report_error::ReportError;
use config::BeEyesConfig;
use once_cell::sync::Lazy;
use reqwest::Client;
use serde::Serialize;
use snowflaked::Generator;
use std::sync::Arc;
use tklog::error;


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
        let trace_id: u64 = Generator::new(0).generate();

        let req = self.client
            .post(format!("{}{}", self.config.url, url))
            .header("BeEyes-Trace", trace_id.to_string())
            .header("Content-Type", "application/json")
            .header("Authorization", &self.config.token);
        let req = match body {
            Some(body) => req.json(&body),
            None => req,
        };
        let resp = req.send().await;

        let resp = match resp {
            Ok(resp) => {
                if !resp.status().is_success() {
                    error!("发起post请求失败: ", resp.status());
                    return Err(ReportError::RequestError(resp.status().to_string()).into());
                }
                resp
            }
            Err(e) => {
                error!("发起post请求失败: ", e);
                return Err(ReportError::RequestError(e.to_string()).into());
            }
        };
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