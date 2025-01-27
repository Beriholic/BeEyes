package cmd

import (
	"log/slog"
	"os"

	"github.com/beriholic/beeyesc/internel"
	"github.com/beriholic/beeyesc/internel/config"
	"github.com/spf13/cobra"
)

var registerCmd = &cobra.Command{
	Use:   "registe",
	Short: "registe to server",
	Long:  `registe to server`,
	Run: func(cmd *cobra.Command, args []string) {
		cfg := config.NewBeEyesConfig()

		slog.Info("正在注册到服务端", "url", cfg.Url, "token", cfg.Token)
		err := internel.RegisteToServer()
		if err != nil {
			slog.Info("注册到服务端失败", "err", err)
			os.Exit(1)
		}
		slog.Info("注册成功")
	},
}

func init() {
	rootCmd.AddCommand(registerCmd)
}
