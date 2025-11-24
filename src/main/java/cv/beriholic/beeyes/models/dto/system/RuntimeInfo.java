package cv.beriholic.beeyes.models.dto.system;

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
    private CPUInfo cpu_info;

    /**
     * 内存信息
     */
    private MemoryInfo memory_info;

    /**
     * 磁盘信息列表
     */
    private List<DiskInfo> disk_info;

    /**
     * 网络信息
     */
    private NetworkInfo network_info;
}
