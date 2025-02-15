package monitor

import (
	"encoding/json"
	"fmt"

	"github.com/beriholic/beeyesc/internal/lib"
	"github.com/beriholic/beeyesc/internal/model"
)

func (m *Monitor) FetchSystemInfo() (*model.SystemInfo, error) {
	binary, err := lib.NewEmbeddedBinary()
	if err != nil {
		return nil, err
	}

	defer binary.Cleanup()
	output, err := binary.Execute("system")
	if err != nil {
		return nil, fmt.Errorf("error calling Rust binary: %w", err)
	}

	var sysInfo model.SystemInfo
	if err := json.Unmarshal(output, &sysInfo); err != nil {
		return nil, fmt.Errorf("error parsing JSON: %w", err)
	}

	return &sysInfo, nil

}
