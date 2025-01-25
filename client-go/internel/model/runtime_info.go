package model

type RuntimeInfo struct {
	Timestamp            int64   `json:"timestamp"`
	CpuUsage             float64 `json:"cpuUsage"`
	MemoryUsage          float64 `json:"memoryUsage"`
	DiskUsage            float64 `json:"diskUsage"`
	NetworkUploadSpeed   float64 `json:"networkUploadSpeed"`
	NetworkDownloadSpeed float64 `json:"networkDownloadSpeed"`
	DiskWriteSpeed       float64 `json:"diskWriteSpeed"`
	DiskReadSpeed        float64 `json:"diskReadSpeed"`
}
