package xyz.beriholic.beeyes.model.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import xyz.beriholic.beeyes.model.dto.*;

import java.util.List;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ReportMachineInfoRequest {
    private SystemInfoDTO systemInfo;
    private CPUInfoDTO cpuInfo;
    private MemoryInfoDTO memoryInfo;
    private NetworkInfoDTO networkInterfaceInfo;
    private List<DiskInfoDTO> diskInfo;
}