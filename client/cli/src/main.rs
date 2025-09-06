mod models;
mod handlers;
mod utils;

#[tokio::main]
pub async fn main() {
    handlers::cmd::handle_commands().await.expect("执行命令失败");
}
