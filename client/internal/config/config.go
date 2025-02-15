package config

import (
	"fmt"
	"os"
	"path/filepath"
	"sync"

	"github.com/charmbracelet/huh"
	"github.com/spf13/viper"
)

const configFilePath = "$HOME/.config/beeyes/config.toml"

type BeEyesConfig struct {
	Token string
	Url   string
}

func Verify() error {
	cfg := NewBeEyesConfig()
	if cfg.Url == "" {
		return fmt.Errorf("服务端地址未设置，请使用 `beeyes config` 设置")
	}
	if cfg.Token == "" {
		return fmt.Errorf("客户端 Token 未设置，请使用 `beeyes config` 设置")
	}
	return nil
}

var (
	configOnce   sync.Once
	beEyesConfig *BeEyesConfig
)

func NewBeEyesConfig() *BeEyesConfig {
	configOnce.Do(func() {
		beEyesConfig = loadConfig()
	})

	return beEyesConfig
}

func Create() {
	expandedPath := os.ExpandEnv(configFilePath)

	configDir := filepath.Dir(expandedPath)
	if err := os.MkdirAll(configDir, os.ModePerm); err != nil {
		fmt.Printf("Failed to create config directory: %v\n", err)
		return
	}

	viper.SetConfigFile(expandedPath)
	_ = viper.ReadInConfig()

	url := ""
	token := ""

	cfg := loadConfig()

	if cfg != nil {
		url = cfg.Url
		token = cfg.Token
	}

	form := huh.NewForm(
		huh.NewGroup(
			huh.NewInput().
				Title("服务端地址 [http(s)://ip:port || http(s)://domain]").
				Validate(func(s string) error {
					if s == "" {
						return fmt.Errorf("服务端地址不能为空")
					}
					return nil
				}).
				Value(&url),
			huh.NewInput().
				Title("Token").
				Validate(func(s string) error {
					if s == "" {
						return fmt.Errorf("Token 不能为空")
					}
					return nil
				}).
				Value(&token),
		),
	).WithTheme(huh.ThemeBase())

	if err := form.Run(); err != nil {
		fmt.Printf("Failed to get user input: %v\n", err)
		return
	}

	viper.Set("url", url)
	viper.Set("token", token)

	if err := viper.WriteConfigAs(expandedPath); err != nil {
		fmt.Printf("Failed to write config file: %v\n", err)
		return
	}

	fmt.Printf("配置文件保存至 %s\n", expandedPath)
}

func loadConfig() *BeEyesConfig {
	expandedPath := os.ExpandEnv(configFilePath)

	viper.SetConfigFile(expandedPath)

	if err := viper.ReadInConfig(); err != nil {
		fmt.Printf("Failed to read config file: %v\n", err)
		return nil
	}

	return &BeEyesConfig{
		Url:   viper.GetString("url"),
		Token: viper.GetString("token"),
	}
}

func VertifyConfigFile() {
	expandedPath := os.ExpandEnv(configFilePath)

	if _, err := os.Stat(expandedPath); os.IsNotExist(err) {
		fmt.Printf("配置文件 %s 不存在，请使用 be-eyes config 命令进行配置\n", expandedPath)
		os.Exit(1)
	}
}
