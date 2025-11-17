//! Error types for the BeEyes application

use thiserror::Error;

/// Unified error type for BeEyes operations
#[derive(Error, Debug)]
pub enum BeEyesError {
    /// Network request related errors
    #[error("网络请求失败 msg={0}")]
    RequestError(String),

    /// Server registration errors
    #[error("机器注册错误 code={0}, message={1}")]
    RegisterError(u16, String),

    /// Machine information reporting errors
    #[error("上报机器信息错误 code={0}, message={1}")]
    ReportMachineInfoError(u16, String),

    /// Runtime information reporting errors
    #[error("上报运行信息错误 code={0}, message={1}")]
    ReportRuntimeInfoError(u16, String),

    /// Configuration validation errors
    #[error("配置验证错误: {0}")]
    ConfigValidationError(#[from] BeEyesConfigValidationError),

    /// General I/O errors
    #[error("I/O错误: {0}")]
    IoError(#[from] std::io::Error),

    /// System information collection errors
    #[error("系统信息获取错误: {0}")]
    SystemInfoError(String),
}

impl From<anyhow::Error> for BeEyesError {
    fn from(err: anyhow::Error) -> Self {
        BeEyesError::RequestError(err.to_string())
    }
}

/// Configuration validation specific errors
#[derive(Error, Debug)]
pub enum BeEyesConfigValidationError {
    #[error("Token为空")]
    TokenEmpty,

    #[error("URL为空")]
    URLEmpty,
}

/// Legacy error type for backwards compatibility
pub type ReportError = BeEyesError;

