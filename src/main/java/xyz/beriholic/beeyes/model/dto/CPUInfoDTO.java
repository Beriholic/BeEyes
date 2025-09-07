package xyz.beriholic.beeyes.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CPUInfoDTO {
    private String name;
    private Integer coreCount;
    private Double usage;
}
