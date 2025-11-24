package cv.beriholic.beeyes.models.dto.system;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 系统信息包括操作系统详情
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SystemInfo implements Serializable {
    @Serial
    private static final long serialVersionUID = -7530838077983470487L;
    /**
     * 操作系统名称
     */
    private String os_name;

    /**
     * 内核版本
     */
    private String kernel_version;

    /**
     * 操作系统版本
     */
    private String os_version;

    /**
     * CPU架构
     */
    private String cpu_arch;

    /**
     * 主机名
     */
    private String hostname;
}
