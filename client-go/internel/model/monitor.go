package model

type CPUInfo struct {
	Name      string  `json:"name"`
	CoreCount int     `json:"core_count"`
	Usage     float64 `json:"usage"`
}

type MemoryInfo struct {
	TotalMemory   float64 `json:"total_memory"`
	UsedMemory    float64 `json:"used_memory"`
	FreeMemory    float64 `json:"free_memory"`
	PercentMemory float64 `json:"percent_memory"`
	TotalSwap     float64 `json:"total_swap"`
	UsedSwap      float64 `json:"used_swap"`
	FreeSwap      float64 `json:"free_swap"`
	PercentSwap   float64 `json:"percent_swap"`
}

type DiskInfo struct {
	Total   float64 `json:"total"`
	Used    float64 `json:"used"`
	Free    float64 `json:"free"`
	Percent float64 `json:"percent"`
}

type NetworkInfo struct {
	Interfaces []*NetworkInterfaceInfo
}

type NetworkInterfaceInfo struct {
	Name          string   `json:"name"`
	IPv4          []string `json:"ipv4"`
	IPv6          []string `json:"ipv6"`
	UploadSpeed   uint64   `json:"upload_speed"`
	DownloadSpeed uint64   `json:"download_speed"`
}

type SystemInfo struct {
	Name          string `json:"name"`
	KernelVersion string `json:"kernel_version"`
	OSVersion     string `json:"os_version"`
	HostName      string `json:"host_name"`
}
