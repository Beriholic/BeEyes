package cmd

import (
	"github.com/beriholic/beeyesc/internel/server"
	"github.com/spf13/cobra"
)

var testCmd = &cobra.Command{
	Use:   "test",
	Short: "test",
	Long:  `test`,
	Run: func(cmd *cobra.Command, args []string) {
		ser := server.NewMonitorService()
		ser.FetchMachineInfo()
	},
}

func init() {
	rootCmd.AddCommand(testCmd)
}
