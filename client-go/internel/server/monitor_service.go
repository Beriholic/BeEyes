package server

import (
	"fmt"
	"sync"
	"time"

	"github.com/beriholic/beeyesc/internel/model"
	"github.com/beriholic/beeyesc/internel/monitor"
)

type MonitorService struct {
	monitor *monitor.Monitor
}

var (
	initMonitorService     sync.Once
	monitorServiceInstence *MonitorService
)

func NewMonitorService() *MonitorService {
	initMonitorService.Do(func() {
		monitorServiceInstence = &MonitorService{
			monitor: monitor.NewMonitor(),
		}
	})
	return monitorServiceInstence
}

func (s *MonitorService) FetchMachineInfo() (*model.MachineInfo, error) {
	sysinfo, err := s.monitor.FetchSystemInfo()
	if err != nil {
		return nil, fmt.Errorf("获取系统信息失败: %v", err)
	}
	cpuInfo, err := s.monitor.FetchCPUInfo(time.Second * 1)
	if err != nil {
		return nil, fmt.Errorf("获取CPU信息失败: %v", err)
	}
	memInfo, err := s.monitor.FetchMemoryInfo()
	if err != nil {
		return nil, fmt.Errorf("获取内存信息失败: %v", err)
	}
	netInfo, err := s.monitor.FetchNetwork()
	if err != nil {
		return nil, fmt.Errorf("获取网络信息失败: %v", err)
	}

	return &model.MachineInfo{
		Systeminfo:  sysinfo,
		CPUInfo:     cpuInfo,
		MemoryInfo:  memInfo,
		NetworkInfo: netInfo.Interfaces,
	}, nil
}

func (s *MonitorService) FetchRuntimeInfo() (*model.RuntimeInfo, error) {
	cpuInfo, err := s.monitor.FetchCPUInfo(time.Second * 1)
	if err != nil {
		return nil, fmt.Errorf("获取CPU信息失败: %v", err)
	}
	memInfo, err := s.monitor.FetchMemoryInfo()
	if err != nil {
		return nil, fmt.Errorf("获取内存信息失败: %v", err)
	}
	netInfo, err := s.monitor.FetchNetwork()
	if err != nil {
		return nil, fmt.Errorf("获取网络信息失败: %v", err)
	}
	diskInfo, err := s.monitor.FetchDiskInfo()
	if err != nil {
		return nil, fmt.Errorf("获取磁盘信息失败: %v", err)
	}

	return &model.RuntimeInfo{
		CPUInfo:     cpuInfo,
		MemoryInfo:  memInfo,
		DiskInfo:    diskInfo,
		NetworkInfo: netInfo,
	}, nil
}
