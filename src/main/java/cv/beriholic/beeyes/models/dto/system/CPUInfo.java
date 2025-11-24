package cv.beriholic.beeyes.models.dto.system;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * CPU信息和使用统计
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CPUInfo implements Serializable {
    @Serial
    private static final long serialVersionUID = -6246748910140336685L;
    /**
     * CPU名称
     */
    private String name;

    /**
     * 核心数
     */
    private Integer core_count;

    /**
     * CPU使用率
     */
    private Double usage;
}
