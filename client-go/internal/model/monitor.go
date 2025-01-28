package model

type CPUInfo struct {
	Name      string  `json:"name"`
	CoreCount int     `json:"core_count"`
	Usage     float64 `json:"usage"`
}

type MemoryInfo struct {
	TotalMemory   float64 `json:"total_memory,omitempty"`
	UsedMemory    float64 `json:"used_memory,omitempty"`
	FreeMemory    float64 `json:"free_memory,omitempty"`
	PercentMemory float64 `json:"percent_memory,omitempty"`
	TotalSwap     float64 `json:"total_swap,omitempty"`
	UsedSwap      float64 `json:"used_swap,omitempty"`
	FreeSwap      float64 `json:"free_swap,omitempty"`
	PercentSwap   float64 `json:"percent_swap,omitempty"`
}

type DiskInfo struct {
	Total   float64 `json:"total,omitempty"`
	Used    float64 `json:"used,omitempty"`
	Free    float64 `json:"free,omitempty"`
	Percent float64 `json:"percent,omitempty"`
}

type NetworkInfo struct {
	Interfaces []*NetworkInterfaceInfo `json:"interfaces,omitempty"`
}

type NetworkInterfaceInfo struct {
	Name          string   `json:"name,omitempty"`
	IPv4          []string `json:"ipv4,omitempty"`
	IPv6          []string `json:"ipv6,omitempty"`
	UploadSpeed   uint64   `json:"upload_speed,omitempty"`
	DownloadSpeed uint64   `json:"download_speed,omitempty"`
}

type SystemInfo struct {
	OSName        string `json:"os_name,omitempty"`
	KernelVersion string `json:"kernel_version,omitempty"`
	OSVersion     string `json:"os_version,omitempty"`
	CPUArch       string `json:"cpu_arch,omitempty"`
}
