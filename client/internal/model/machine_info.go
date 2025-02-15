package model

type MachineInfo struct {
	Systeminfo  *SystemInfo             `json:"system_info,omitempty"`
	CPUInfo     *CPUInfo                `json:"cpu_info,omitempty"`
	MemoryInfo  *MemoryInfo             `json:"memory_info,omitempty"`
	NetworkInfo []*NetworkInterfaceInfo `json:"network_interface_info,omitempty"`
	DiskInfo    *DiskInfo               `json:"disk_info,omitempty"`
}
