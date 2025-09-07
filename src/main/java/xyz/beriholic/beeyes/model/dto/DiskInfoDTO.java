package xyz.beriholic.beeyes.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DiskInfoDTO {
    private String name;
    private String fileSystem;
    private Long total;
    private Long used;
    private Long free;
    private Double percent;
    private String kind;
}
