package xyz.beriholic.beeyes.model.request;


import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import xyz.beriholic.beeyes.model.dto.CPUInfoDTO;
import xyz.beriholic.beeyes.model.dto.DiskInfoDTO;
import xyz.beriholic.beeyes.model.dto.MemoryInfoDTO;
import xyz.beriholic.beeyes.model.dto.NetworkInfoDTO;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ReportRuntimeInfoRequest {
    private Long timestamp;
    private CPUInfoDTO cpuInfo;
    private MemoryInfoDTO memoryInfo;
    private List<DiskInfoDTO> diskInfo;
    private NetworkInfoDTO networkInfo;
}