package api

import (
	"encoding/json"
	"fmt"
	"io"
	"log/slog"
	"net/http"
	"sync"
	"time"

	"github.com/beriholic/beeyesc/internel/config"
	"github.com/beriholic/beeyesc/internel/model"
)

var (
	apiInscene *Api
	initApi    sync.Once
)

type Api struct {
	cfg *config.BeEyesConfig
}

func NewApi() *Api {
	initApi.Do(func() {
		apiInscene = &Api{
			cfg: config.NewBeEyesConfig(),
		}
	})
	return apiInscene
}

func newClient() *http.Client {
	return &http.Client{
		Timeout: time.Second * 5,
	}
}

func (a *Api) RegisterToServer() error {
	slog.Info("注册到服务端...", "url", a.cfg.Url, "token", a.cfg.Token)

	client := newClient()

	req, err := http.NewRequest("GET", fmt.Sprintf("%s/api/client/register", a.cfg.Url), nil)
	if err != nil {
		return fmt.Errorf("创建请求失败: %s", err)
	}
	req.Header.Add("Authorization", a.cfg.Token)

	resp, err := client.Do(req)
	if err != nil {
		return fmt.Errorf("请求失败: %s", err)
	}
	defer resp.Body.Close()

	var response model.Response
	body, err := io.ReadAll(resp.Body)
	if err != nil {
		return err
	}

	if err = json.Unmarshal(body, &response); err != nil {
		return err
	}

	if resp.StatusCode == http.StatusOK {
		slog.Info("客户端注册成功", "address", a.cfg.Url, "token", a.cfg.Token)
		return nil
	} else {
		return fmt.Errorf("客户端注册失败: status: %d, message: %s", resp.StatusCode, response.Msg)

	}
}
