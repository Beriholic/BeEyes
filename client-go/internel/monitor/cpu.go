package monitor

import (
	"fmt"
	"time"

	"github.com/beriholic/beeyesc/internel/model"
	"github.com/shirou/gopsutil/v4/cpu"
)

func (m *Monitor) FetchCPUInfo(timespan time.Duration) (*model.CPUInfo, error) {
	cpuInfo, err := cpu.Info()
	if err != nil {
		return nil, fmt.Errorf("获取CPU信息失败: %v\n", err)
	}
	if len(cpuInfo) == 0 {
		return nil, fmt.Errorf("获取CPU信息失败: CPU数量为0\n")
	}

	name := cpuInfo[0].ModelName
	coreCount, err := cpu.Counts(true)
	if err != nil {
		return nil, fmt.Errorf("获取CPU信息失败: %v\n", err)
	}

	usage, err := cpu.Percent(timespan, false)
	if err != nil {
		return nil, fmt.Errorf("获取CPU信息失败: %v\n", err)
	}

	return &model.CPUInfo{
		Name:      name,
		CoreCount: coreCount,
		Usage:     twoDecimals(usage[0]),
	}, nil

}
