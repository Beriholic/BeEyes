pub fn byte_to_gb(bytes: u64) -> f64 {
    return two_decimals(bytes as f64 / (1024.0 * 1024.0 * 1024.0));
}
pub fn two_decimals(number: f64) -> f64 {
    return (number * 100.0).round() / 100.0;
}
