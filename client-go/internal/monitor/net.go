package monitor

import (
	"fmt"
	"log/slog"
	"net"
	"strings"
	"time"

	"github.com/beriholic/beeyesc/internal/model"
	gopsutilnet "github.com/shirou/gopsutil/v4/net"
)

func (m *Monitor) FetchNetwork() (*model.NetworkInfo, error) {
	interfaces, err := net.Interfaces()
	if err != nil {
		return nil, fmt.Errorf("获取网络接口失败: %v", err)
	}

	networkInfo := &model.NetworkInfo{
		Interfaces: make([]*model.NetworkInterfaceInfo, 0),
	}

	interfaceIndexMap := make(map[string]int)

	for _, iface := range interfaces {
		if !strings.HasPrefix(iface.Name, "et") &&
			!strings.HasPrefix(iface.Name, "en") &&
			!strings.HasPrefix(iface.Name, "wl") {
			continue
		}

		interfaceInfo := model.NetworkInterfaceInfo{
			Name: iface.Name,
			IPv4: make([]string, 0),
			IPv6: make([]string, 0),
		}

		addrs, err := iface.Addrs()
		if err != nil {
			slog.Error("获取接口地址失败",
				"接口名称", iface.Name,
				"错误信息", err.Error(),
			)
			continue
		}

		for _, addr := range addrs {
			ipNet, ok := addr.(*net.IPNet)
			if !ok {
				continue
			}

			if ipNet.IP.To4() != nil {
				interfaceInfo.IPv4 = append(interfaceInfo.IPv4, ipNet.IP.String())
			} else if ipNet.IP.To16() != nil {
				interfaceInfo.IPv6 = append(interfaceInfo.IPv6, ipNet.IP.String())
			}
		}

		networkInfo.Interfaces = append(networkInfo.Interfaces, &interfaceInfo)
		interfaceIndexMap[iface.Name] = len(networkInfo.Interfaces) - 1
	}

	initStats, err := gopsutilnet.IOCounters(true)
	if err != nil {
		return nil, fmt.Errorf("获取初始网络统计信息失败: %v", err)
	}

	time.Sleep(time.Second)

	finalStats, err := gopsutilnet.IOCounters(true)
	if err != nil {
		return nil, fmt.Errorf("获取最终网络统计信息失败: %v", err)
	}

	for i := range initStats {
		initial := initStats[i]
		final := finalStats[i]

		if !strings.HasPrefix(initial.Name, "et") &&
			!strings.HasPrefix(initial.Name, "en") &&
			!strings.HasPrefix(initial.Name, "wl") {
			continue
		}

		uploadSpeed := final.BytesSent - initial.BytesSent
		downloadSpeed := final.BytesRecv - initial.BytesRecv

		index, exists := interfaceIndexMap[initial.Name]
		if !exists {
			continue
		}

		networkInfo.Interfaces[index].UploadSpeed = uploadSpeed
		networkInfo.Interfaces[index].DownloadSpeed = downloadSpeed
	}

	return networkInfo, nil
}
