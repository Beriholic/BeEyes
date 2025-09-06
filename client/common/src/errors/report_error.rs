use thiserror::Error;

#[derive(Error, Debug)]
pub enum ReportError {
    #[error("网络请求失败")]
    RequestError,
    #[error("机器注册错误 code={0}, message={1}")]
    RegisterError(u16, String),
}
