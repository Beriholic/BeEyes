package model

type NetworkInterfaceInfo struct {
	Name     string   `json:"name"`
	Ipv4Addr []string `json:"ipv4Addr"`
	Ipv6Addr []string `json:"ipv6Addr"`
}
