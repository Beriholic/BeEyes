use crate::handlers::config::config_setting;
use crate::handlers::{report, version};
use crate::models::cli::{Cli, Commands};
use anyhow::{anyhow, Result};
use clap::{CommandFactory, Parser};
use clap_complete::generate;
use std::io;

pub async fn handle_commands() -> Result<()> {
    let cli = Cli::parse();

    match &cli.command {
        Commands::Run => {
            config::verify_config()?;
            report::report_to_server().await
        }

        Commands::Completion(args) => {
            println!("正在为 {:?} 生成补全脚本...", args.shell);
            println!("----------------------------------");
            let mut cmd = <Cli as CommandFactory>::command();
            let cmd_name = cmd.get_name().to_string();
            generate(args.shell, &mut cmd, cmd_name, &mut io::stdout());
            println!("----------------------------------");
            println!("补全脚本生成完成！请将生成的脚本添加到您的 shell 配置文件中。");
            Ok(())
        }

        Commands::Config => {
            if let Err(e) = config_setting() {
                return Err(anyhow!("配置文件保存出错: {}", e));
            }
            Ok(())
        }

        Commands::Version => {
            version::print_version()
        }
    }
}

