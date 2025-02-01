package model

type RuntimeInfo struct {
	Timestamp   int64        `json:"timestamp"`
	CPUInfo     *CPUInfo     `json:"cpu_info,omitempty"`
	MemoryInfo  *MemoryInfo  `json:"memory_info,omitempty"`
	DiskInfo    *DiskInfo    `json:"disk_info,omitempty"`
	NetworkInfo *NetworkInfo `json:"network_info,omitempty"`
}
