package xyz.beriholic.beeyes.client.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class InterfaceInfo {
    private String name;
    private String[] ipv4Address;
    private String[] ipv6Address;
}
