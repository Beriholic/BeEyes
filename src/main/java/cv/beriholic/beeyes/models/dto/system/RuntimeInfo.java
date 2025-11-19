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
 * 运行时指标用于定期监控
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class RuntimeInfo implements Serializable {
    @Serial
    private static final long serialVersionUID = -850417733986509022L;
    /**
     * 时间戳（毫秒）
     */
    private Long timestamp;

    /**
     * CPU信息
     */
    private CPUInfo cpuInfo;

    /**
     * 内存信息
     */
    private MemoryInfo memoryInfo;

    /**
     * 磁盘信息列表
     */
    private List<DiskInfo> diskInfo;

    /**
     * 网络信息
     */
    private NetworkInfo networkInfo;
}
