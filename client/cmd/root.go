package cmd

import (
	"fmt"
	"log/slog"
	"os"

	"github.com/beriholic/beeyesc/internal"
	"github.com/spf13/cobra"
)

const logo = `
 ________  _______   _______       ___    ___ _______   ________      
|\   __  \|\  ___ \ |\  ___ \     |\  \  /  /|\  ___ \ |\   ____\     
\ \  \|\ /\ \   __/|\ \   __/|    \ \  \/  / | \   __/|\ \  \___|_    
 \ \   __  \ \  \_|/_\ \  \_|/__   \ \    / / \ \  \_|/_\ \_____  \   
  \ \  \|\  \ \  \_|\ \ \  \_|\ \   \/  /  /   \ \  \_|\ \|____|\  \  
   \ \_______\ \_______\ \_______\__/  / /      \ \_______\____\_\  \ 
    \|_______|\|_______|\|_______|\___/ /        \|_______|\_________\
                                 \|___|/                  \|_________|
`

var rootCmd = &cobra.Command{
	Use:   "beeyesc",
	Short: "BeEyes Client",
	Long:  `BeEyes Client`,
	Run: func(cmd *cobra.Command, args []string) {
		fmt.Print(logo)

		slog.Info("开始运行系统监测")
		if err := internal.RunMonitor(); err != nil {
			slog.Info("运行系统监测出错", "err", err)
			os.Exit(1)
		}
	},
}

func Execute() {
	if err := rootCmd.Execute(); err != nil {
		fmt.Fprintln(os.Stderr, err)
		os.Exit(1)
	}
}
