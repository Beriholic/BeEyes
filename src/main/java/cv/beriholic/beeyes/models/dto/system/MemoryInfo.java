package cv.beriholic.beeyes.models.dto.system;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 内存信息包括RAM和交换分区
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class MemoryInfo implements Serializable {
    @Serial
    private static final long serialVersionUID = 4049866545799310455L;
    /**
     * 总内存（字节）
     */
    private Long totalMemory;

    /**
     * 已使用内存（字节）
     */
    private Long usedMemory;

    /**
     * 可用内存（字节）
     */
    private Long freeMemory;

    /**
     * 总交换分区（字节）
     */
    private Long totalSwap;

    /**
     * 已使用交换分区（字节）
     */
    private Long usedSwap;

    /**
     * 可用交换分区（字节）
     */
    private Long freeSwap;

    /**
     * 内存使用百分比
     */
    private Double percentMemory;

    /**
     * 交换分区使用百分比
     */
    private Double percentSwap;
}
