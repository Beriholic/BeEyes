package xyz.beriholic.beeyes.entity.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InterfaceInfo {
    private String name;
    private String[] ipv4Address;
    private String[] ipv6Address;
}
