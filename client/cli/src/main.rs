mod models;
mod handlers;
mod utils;

#[tokio::main]
async fn main() {
    handlers::cmd::handle_commands().await.expect("Failed to handle commands");
}
