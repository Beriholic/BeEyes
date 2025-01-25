package monitor

import (
	"fmt"

	"github.com/beriholic/beeyesc/internel/model"
	"github.com/shirou/gopsutil/v4/mem"
)

func (s *Monitor) FetchMemoryInfo() (*model.MemoryInfo, error) {
	memInfo, err := mem.VirtualMemory()
	if err != nil {
		return nil, fmt.Errorf("获取内存信息失败: %v\n", err)
	}

	memoryTotal := byteToGB(memInfo.Total)
	memoryUsed := byteToGB(memInfo.Used)
	memoryAvailable := byteToGB(memInfo.Available)
	memoryPercent := twoDecimals(memInfo.UsedPercent)

	swapFree := byteToGB(memInfo.SwapFree)
	swapCaced := byteToGB(memInfo.SwapCached)
	swapTotal := byteToGB(memInfo.SwapTotal)
	swapUsed := twoDecimals(swapTotal - swapFree - swapCaced)
	swapPercent := twoDecimals(float64(swapUsed) / float64(swapTotal) * 100)

	return &model.MemoryInfo{
		MemoryTotal:     memoryTotal,
		MemoryUsed:      memoryUsed,
		MemoryAvailable: memoryAvailable,
		MemoryPercent:   memoryPercent,
		SwapFree:        swapFree,
		SwapCached:      swapCaced,
		SwapTotal:       swapTotal,
		SwapUsed:        swapUsed,
		SwapPercent:     swapPercent,
	}, nil
}
