package monitor

import (
	"fmt"

	"github.com/beriholic/beeyesc/internel/model"
	"github.com/shirou/gopsutil/v4/disk"
)

func (m *Monitor) FetchDiskInfo() (*model.DiskInfo, error) {
	usage, err := disk.Usage("/")
	if err != nil {
		return nil, fmt.Errorf("获取磁盘信息失败: %v\n", err)
	}

	total := byteToGB(usage.Total)
	used := byteToGB(usage.Used)
	free := byteToGB(usage.Free)
	percent := twoDecimals(usage.UsedPercent)

	//TODO disk io stat

	return &model.DiskInfo{
		Total:   total,
		Used:    used,
		Free:    free,
		Percent: percent,
	}, nil
}
