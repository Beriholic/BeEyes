package xyz.beriholic.beeyes.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MemoryInfoDTO {
    private Long totalMemory;
    private Long usedMemory;
    private Long freeMemory;
    private Long totalSwap;
    private Long usedSwap;
    private Long freeSwap;
    private Double percentMemory;
    private Double percentSwap;
}
