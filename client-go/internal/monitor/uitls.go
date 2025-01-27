package monitor

import "math"

func byteToGB(bytes uint64) float64 {
	return twoDecimals(float64(bytes) / (1024 * 1024 * 1024))
}

func twoDecimals(number float64) float64 {
	return math.Round(number*100) / 100
}
