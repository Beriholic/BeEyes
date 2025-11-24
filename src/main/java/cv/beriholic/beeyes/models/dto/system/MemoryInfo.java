package cv.beriholic.beeyes.models.dto.system;

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
public class MemoryInfo implements Serializable {
    @Serial
    private static final long serialVersionUID = 4049866545799310455L;
    /**
     * 总内存（字节）
     */
    private Long total_memory;

    /**
     * 已使用内存（字节）
     */
    private Long used_memory;

    /**
     * 可用内存（字节）
     */
    private Long free_memory;

    /**
     * 总交换分区（字节）
     */
    private Long total_swap;

    /**
     * 已使用交换分区（字节）
     */
    private Long used_swap;

    /**
     * 可用交换分区（字节）
     */
    private Long free_swap;

    /**
     * 内存使用百分比
     */
    private Double percent_memory;

    /**
     * 交换分区使用百分比
     */
    private Double percent_swap;
}
