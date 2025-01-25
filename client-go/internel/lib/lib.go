package lib

import (
	"embed"
	"fmt"
	"os"
	"os/exec"
)

//go:embed sysinfo/target/release/sysinfo
var sysinfoBin embed.FS

type EmbeddedBinary struct {
	sysInfoTempFilePath string
}

func NewEmbeddedBinary() (*EmbeddedBinary, error) {
	binaryName := "sysinfo/target/release/sysinfo"
	if os.PathSeparator == '\\' { // Windows
		binaryName = "sysinfo\\target\\release\\sysinfo.exe"
	}

	binData, err := sysinfoBin.ReadFile(binaryName)
	if err != nil {
		return nil, fmt.Errorf("error reading embedded binary: %w", err)
	}

	tmpFile, err := os.CreateTemp("", "sysinfo")
	if err != nil {
		return nil, fmt.Errorf("error creating temp file: %w", err)
	}

	if _, err := tmpFile.Write(binData); err != nil {
		return nil, fmt.Errorf("error writing to temp file: %w", err)
	}
	tmpFile.Close()

	if err := os.Chmod(tmpFile.Name(), 0755); err != nil {
		return nil, fmt.Errorf("error setting executable permissions: %w", err)
	}

	return &EmbeddedBinary{
		sysInfoTempFilePath: tmpFile.Name(),
	}, nil
}

func (e *EmbeddedBinary) Execute() ([]byte, error) {
	cmd := exec.Command(e.sysInfoTempFilePath)
	return cmd.Output()
}

func (e *EmbeddedBinary) Cleanup() {
	if e.sysInfoTempFilePath != "" {
		os.Remove(e.sysInfoTempFilePath)
	}
}
