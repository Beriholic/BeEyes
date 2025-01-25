package model

type MachineInfo struct {
	OsArch               string               `json:"osArch"`
	OsName               string               `json:"osName"`
	OsVersion            string               `json:"osVersion"`
	OsBitSize            int                  `json:"osBitSize"`
	CpuName              string               `json:"cpuName"`
	CpuCoreCount         int                  `json:"cpuCoreCount"`
	MemorySize           float64              `json:"memorySize"`
	DiskSize             float64              `json:"diskSize"`
	DiskTotalSize        float64              `json:"diskTotalSize"`
	NetworkInterfaceInfo NetworkInterfaceInfo `json:"networkInterfaceInfo"`
}
