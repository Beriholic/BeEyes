package model

type CPUInfo struct {
	Name      string
	CoreCount int
	Usage     float64
}
type MemoryInfo struct {
	MemoryTotal     float64
	MemoryUsed      float64
	MemoryAvailable float64
	MemoryPercent   float64
	SwapTotal       float64
	SwapFree        float64
	SwapCached      float64
	SwapUsed        float64
	SwapPercent     float64
}

type DiskInfo struct {
	Total   float64
	Used    float64
	Free    float64
	Percent float64
}

type NetworkInfo struct {
	Interfaces []InterfaceInfo
}

type InterfaceInfo struct {
	Name          string
	IPv4          []string
	IPv6          []string
	UploadSpeed   uint64
	DownloadSpeed uint64
}

type SystemInfo struct {
	Name          string `json:"name"`
	KernelVersion string `json:"kernel_version"`
	OSVersion     string `json:"os_version"`
	HostName      string `json:"host_name"`
}
