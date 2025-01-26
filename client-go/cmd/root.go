package cmd

import (
	"fmt"
	"log/slog"
	"os"

	"github.com/beriholic/beeyesc/internel"
	"github.com/spf13/cobra"
)

var rootCmd = &cobra.Command{
	Use:   "beeyesc",
	Short: "BeEyes Client",
	Long:  `BeEyes Client`,
	Run: func(cmd *cobra.Command, args []string) {
		slog.Info("开始运行系统监测")
		if err := internel.RunMonitor(); err != nil {
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
