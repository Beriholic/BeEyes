use thiserror::Error;

#[derive(Error, Debug)]
pub enum ReportError {
    #[error("网络请求失败 msg={0}")]
    RequestError(String),
    #[error("机器注册错误 code={0}, message={1}")]
    RegisterError(u16, String),
    #[error("上报机器信息错误 code={0}, message={1}")]
    ReportMachineInfoError(u16, String),
    #[error("上报运行信息错误 code={0}, message={1}")]
    ReportRuntimeInfoError(u16, String),
}
impl From<anyhow::Error> for ReportError {
    fn from(err: anyhow::Error) -> Self {
        ReportError::RequestError(err.to_string())
    }
}
