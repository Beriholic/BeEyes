use crate::handlers::{report, version};
use crate::models::cli::{Cli, Commands};
use anyhow::Result;
use clap::{CommandFactory, Parser};
use clap_complete::generate;
use std::io;

pub async fn handle_commands() -> Result<()> {
    let cli = Cli::parse();

    match &cli.command {
        Commands::Run => {
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

        Commands::Config(args) => {
            println!("Configuration file path set to: {}", args.file);
            Ok(())
        }

        Commands::Register(args) => {
            println!("Attempting to register with server at: {}", args.server_url);
            Ok(())
        }

        Commands::Version => {
            version::print_version()
        }
    }
}

