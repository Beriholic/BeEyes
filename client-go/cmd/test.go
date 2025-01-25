package cmd

import (
	"fmt"
	"log/slog"

	"github.com/beriholic/beeyesc/internel/api"
	"github.com/beriholic/beeyesc/internel/config"
	"github.com/beriholic/beeyesc/internel/server"
	"github.com/bytedance/sonic"
	"github.com/spf13/cobra"
)

var testCmd = &cobra.Command{
	Use:   "test",
	Short: "test",
	Long:  `test`,
	Run: func(cmd *cobra.Command, args []string) {
		config.VertifyConfigFile()
		api.NewApi().RegisterToServer()
		ser := server.NewMonitorService()
		machineInfo, err := ser.FetchMachineInfo()
		if err != nil {
			slog.Error("fetch machine info error", "err", err)
			return
		}
		json, err := sonic.Marshal(machineInfo)
		if err != nil {
			slog.Error("marshal machine info error", "err", err)
			return
		}
		fmt.Println(string(json))
	},
}

func init() {
	rootCmd.AddCommand(testCmd)
}
