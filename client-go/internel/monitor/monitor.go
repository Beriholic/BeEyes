package monitor

import "sync"

var (
	monitor     *Monitor
	mointorOnce sync.Once
)

type Monitor struct{}

func NewMonitor() *Monitor {
	mointorOnce.Do(func() {
		if monitor == nil {
			monitor = &Monitor{}
		}
	})
	return monitor
}
