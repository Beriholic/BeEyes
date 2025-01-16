package xyz.beriholic.beeyes.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NetworkInterfaceInfo {
    private String name;
    private String[] ipv4Addr;
    private String[] ipv6Addr;
}
