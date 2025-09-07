use crate::utils::banner::print_logo;
use tklog::error;

mod models;
mod handlers;
mod utils;

#[tokio::main]
pub async fn main() {
    print_logo();
    match handlers::cmd::handle_commands().await {
        Ok(_) => {}
        Err(e) => {
            error!("执行命令失败", e)
        }
    }
}
