package cv.beriholic.beeyes.models.dto.system;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 网络接口信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NetworkInterfaceInfo implements Serializable {
    @Serial
    private static final long serialVersionUID = -6787311378747478706L;
    /**
     * 网络接口名称
     */
    private String name;

    /**
     * IPv4地址列表
     */
    private List<String> ipv4;

    /**
     * IPv6地址列表
     */
    private List<String> ipv6;

    /**
     * 上传速度（字节）
     */
    private Long upload_speed;

    /**
     * 下载速度（字节）
     */
    private Long download_speed;
}
