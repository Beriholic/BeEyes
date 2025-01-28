package monitor

import "sync"

var (
	monitor     *Monitor
	monitorOnce sync.Once
)

type Monitor struct{}

func NewMonitor() *Monitor {
	monitorOnce.Do(func() {
		if monitor == nil {
			monitor = &Monitor{}
		}
	})
	return monitor
}
