package cv.beriholic.beeyes.models.dto.system;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 完整的机器信息用于初始注册
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class MachineInfo implements Serializable {
    @Serial
    private static final long serialVersionUID = 2392720384558399053L;
    /**
     * 系统信息
     */
    private SystemInfo systemInfo;

    /**
     * CPU信息
     */
    private CPUInfo cpuInfo;

    /**
     * 内存信息
     */
    private MemoryInfo memoryInfo;

    /**
     * 网络信息
     */
    private NetworkInfo networkInfo;

    /**
     * 磁盘信息列表
     */
    private List<DiskInfo> diskInfo;
}
