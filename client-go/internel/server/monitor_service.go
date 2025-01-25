package server

import (
	"fmt"
	"sync"

	"github.com/beriholic/beeyesc/internel/model"
	"github.com/beriholic/beeyesc/internel/monitor"
	"github.com/bytedance/sonic"
)

type MonitorService struct {
	monitor *monitor.Monitor
}

var (
	onceMonitorService sync.Once
	monitorService     *MonitorService
)

func NewMonitorService() *MonitorService {
	onceMonitorService.Do(func() {
		monitorService = &MonitorService{
			monitor: monitor.NewMonitor(),
		}
	})
	return monitorService
}

func (s *MonitorService) FetchMachineInfo() (*model.MachineInfo, error) {
	systemInfo, _ := s.monitor.FetchSystemInfo()
	systemInfoJson, _ := sonic.Marshal(systemInfo)
	fmt.Println(string(systemInfoJson))

	cpu, _ := s.monitor.FetchCPUInfo(0)

	cpuJson, _ := sonic.Marshal(cpu)
	fmt.Println(string(cpuJson))

	mem, _ := s.monitor.FetchMemoryInfo()
	memJson, _ := sonic.Marshal(mem)

	fmt.Println(string(memJson))

	disk, _ := s.monitor.FetchDiskInfo()
	diskJson, _ := sonic.Marshal(disk)

	fmt.Println(string(diskJson))

	netInfo, _ := s.monitor.FetchNetwork()
	netInfoJson, _ := sonic.Marshal(netInfo)
	fmt.Println(string(netInfoJson))

	return nil, nil
}
