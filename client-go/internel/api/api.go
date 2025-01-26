package api

import (
	"bytes"
	"fmt"
	"io"
	"log/slog"
	"net/http"
	"sync"
	"time"

	"github.com/beriholic/beeyesc/internel/config"
	"github.com/beriholic/beeyesc/internel/model"
	"github.com/bytedance/sonic"
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

func (a *Api) doRequest(method, url, body string) (*model.Response, error) {
	client := &http.Client{
		Timeout: time.Second * 2,
	}

	reqBody := bytes.NewBufferString(body)

	req, err := http.NewRequest(method, a.cfg.Url+url, reqBody)
	if err != nil {
		return nil, fmt.Errorf("创建请求失败: %v", err)
	}

	req.Header.Add("Authorization", a.cfg.Token)
	req.Header.Add("Content-Type", "application/json")

	resp, err := client.Do(req)
	if err != nil {
		return nil, fmt.Errorf("请求失败: %v", err)
	}
	defer resp.Body.Close()

	respBody, err := io.ReadAll(resp.Body)
	if err != nil {
		return nil, fmt.Errorf("读取响应体失败: %v", err)
	}

	var response model.Response
	if err := sonic.Unmarshal(respBody, &response); err != nil {
		return nil, fmt.Errorf("解析响应体失败: %v", err)
	}

	return &response, nil
}

func (a *Api) RegisterToServer() error {
	resp, err := a.doRequest("POST", "/api/client/register", "")
	if err != nil {
		return err
	}
	if resp.Code != 200 {
		return fmt.Errorf("注册失败: %v", resp.Msg)
	}
	return nil
}

func (a *Api) ReportMachineInfo(info *model.MachineInfo) error {
	_json, err := sonic.Marshal(info)
	if err != nil {
		return err
	}
	json := string(_json)
	slog.Info("机器信息", "info", json)

	resp, err := a.doRequest("POST", "/api/client/report/machine", json)
	if err != nil {
		return err
	}
	if resp.Code != 200 {
		return fmt.Errorf("上报机器信息失败: %v", resp.Msg)
	}

	return nil
}

func (a *Api) ReportRuntimeInfo(info *model.RuntimeInfo) error {
	_json, err := sonic.Marshal(info)
	if err != nil {
		return err
	}
	json := string(_json)
	slog.Info("运行信息", "info", json)

	resp, err := a.doRequest("POST", "/api/client/report/runtime", json)
	if err != nil {
		return err
	}
	if resp.Code != 200 {
		return fmt.Errorf("上报运行时信息失败: %v", resp.Msg)
	}
	return nil
}
