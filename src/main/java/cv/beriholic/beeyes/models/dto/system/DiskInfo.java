package cv.beriholic.beeyes.models.dto.system;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 磁盘信息和使用统计
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class DiskInfo implements Serializable {
    @Serial
    private static final long serialVersionUID = 986779629743583069L;
    /**
     * 磁盘名称
     */
    private String name;

    /**
     * 文件系统类型
     */
    private String fileSystem;

    /**
     * 总空间（字节）
     */
    private Long total;

    /**
     * 已使用空间（字节）
     */
    private Long used;

    /**
     * 可用空间（字节）
     */
    private Long free;

    /**
     * 使用比例
     */
    private Double percent;

    /**
     * 磁盘类型
     */
    private String kind;
}
