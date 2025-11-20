package cv.beriholic.beeyes.models.dto.system;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
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
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
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
    private Integer coreCount;

    /**
     * CPU使用率
     */
    private Double usage;
}
