package xyz.beriholic.beeyes.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class NetworkInterfaceDTO {
    private String name;
    private List<String> ipv4;
    private List<String> ipv6;
    private Long uploadSpeed;
    private Long downloadSpeed;
}
