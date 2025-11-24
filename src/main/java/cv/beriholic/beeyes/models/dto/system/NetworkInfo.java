package cv.beriholic.beeyes.models.dto.system;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 网络信息包含所有接口
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NetworkInfo implements Serializable {
    @Serial
    private static final long serialVersionUID = -7478877064119568674L;
    /**
     * 网络接口列表
     */
    private List<NetworkInterfaceInfo> interfaces;
}
