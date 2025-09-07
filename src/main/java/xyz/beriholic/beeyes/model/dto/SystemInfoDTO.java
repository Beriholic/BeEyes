package xyz.beriholic.beeyes.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SystemInfoDTO {
    private String osName;
    private String kernelVersion;
    private String osVersion;
    private String cpuArch;
    private String hostName;
}
