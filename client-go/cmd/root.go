package cmd

import (
	"fmt"
	"os"

	"github.com/spf13/cobra"
)

var rootCmd = &cobra.Command{
	Use:   "beeyesc",
	Short: "BeEyes Client",
	Long:  `BeEyes Client`,
	Run: func(cmd *cobra.Command, args []string) {
		fmt.Println("你踏入了无人的荒原")
	},
}

func Execute() {
	if err := rootCmd.Execute(); err != nil {
		fmt.Fprintln(os.Stderr, err)
		os.Exit(1)
	}
}
