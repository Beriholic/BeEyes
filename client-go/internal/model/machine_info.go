package model

type MachineInfo struct {
	Systeminfo  *SystemInfo             `json:"system_info"`
	CPUInfo     *CPUInfo                `json:"cpu_info"`
	MemoryInfo  *MemoryInfo             `json:"memory_info"`
	NetworkInfo []*NetworkInterfaceInfo `json:"network_interface_info"`
}
