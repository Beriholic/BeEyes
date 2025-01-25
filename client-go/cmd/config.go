package cmd

import (
	"github.com/beriholic/beeyesc/internel/config"
	"github.com/spf13/cobra"
)

var configCmd = &cobra.Command{
	Use:   "config",
	Short: "Set the config file",
	Long:  `Set the config file`,
	Run: func(cmd *cobra.Command, args []string) {
		config.Create()
	},
}

func init() {
	rootCmd.AddCommand(configCmd)
}
