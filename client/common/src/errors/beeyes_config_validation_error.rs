use thiserror::Error;

#[derive(Error, Debug)]
pub enum BeEyesConfigValidationError {
    #[error("Token为空")]
    TokenEmpty,
    #[error("URL为空")]
    URLEmpty,
}


