use anyhow::Result;
use serde::Serialize;

pub fn to_json<T: Serialize>(value: &T) -> Result<String> {
    let json = serde_json::to_string(value)?;
    Ok(json)
}