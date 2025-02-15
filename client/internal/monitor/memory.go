package monitor

import (
	"encoding/json"
	"fmt"

	"github.com/beriholic/beeyesc/internal/lib"
	"github.com/beriholic/beeyesc/internal/model"
)

func (s *Monitor) FetchMemoryInfo() (*model.MemoryInfo, error) {
	binary, err := lib.NewEmbeddedBinary()
	if err != nil {
		return nil, err
	}

	defer binary.Cleanup()
	output, err := binary.Execute("memory")
	if err != nil {
		return nil, fmt.Errorf("error calling Rust binary: %w", err)
	}

	var memInfo model.MemoryInfo
	if err := json.Unmarshal(output, &memInfo); err != nil {
		return nil, fmt.Errorf("error parsing JSON: %w  json: %v", err, output)
	}

	return &memInfo, nil
}
