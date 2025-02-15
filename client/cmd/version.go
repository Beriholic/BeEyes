package cmd

import (
	"fmt"

	"github.com/spf13/cobra"
)

const version = "0.1.0"

var versionCmd = &cobra.Command{
	Use:   "version",
	Short: "print the version of the beeyesc",
	Long:  `print the version of the beeyesc`,
	Run: func(cmd *cobra.Command, args []string) {
		fmt.Printf("beeyes version %s\n", version)
	},
}

func init() {
	rootCmd.AddCommand(versionCmd)
}
