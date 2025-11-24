package cv.beriholic.beeyes.models.dto.system;

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
public class MachineInfo implements Serializable {
    @Serial
    private static final long serialVersionUID = 2392720384558399053L;
    /**
     * 系统信息
     */
    private SystemInfo system_info;

    /**
     * CPU信息
     */
    private CPUInfo cpu_info;

    /**
     * 内存信息
     */
    private MemoryInfo memory_info;

    /**
     * 网络信息
     */
    private NetworkInfo network_info;

    /**
     * 磁盘信息列表
     */
    private List<DiskInfo> disk_info;
}
