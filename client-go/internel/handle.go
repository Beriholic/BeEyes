package internel

import (
	"log/slog"
	"time"

	"github.com/beriholic/beeyesc/internel/api"
	"github.com/beriholic/beeyesc/internel/server"
)

func RunMonitor() error {
	api := api.NewApi()
	server := server.NewMonitorService()

	slog.Info("正在获取机器信息")
	machineInfo, err := server.FetchMachineInfo()
	if err != nil {
		return err
	}
	slog.Info("获取机器信息成功")

	slog.Info("正在上报机器信息")
	err = api.ReportMachineInfo(machineInfo)
	if err != nil {
		return err
	}
	slog.Info("上报机器信息成功")

	for {
		runtimeInfo, err := server.FetchRuntimeInfo()
		if err != nil {
			slog.Error("获取运行时信息失败", "err", err)
			return nil
		}
		err = api.ReportRuntimeInfo(runtimeInfo)
		if err != nil {
			slog.Error("上报运行时信息失败", "err", err)
			return nil
		}
		time.Sleep(time.Second * 5)
	}
}
func RegisteToServer() error {
	err := api.NewApi().RegisterToServer()
	if err != nil {
		return err
	}
	return nil
}
