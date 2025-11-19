package cv.beriholic.beeyes.models.dto.system;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
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
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class SystemInfo implements Serializable {
    @Serial
    private static final long serialVersionUID = -7530838077983470487L;
    /**
     * 操作系统名称
     */
    private String osName;

    /**
     * 内核版本
     */
    private String kernelVersion;

    /**
     * 操作系统版本
     */
    private String osVersion;

    /**
     * CPU架构
     */
    private String cpuArch;

    /**
     * 主机名
     */
    private String hostName;
}
