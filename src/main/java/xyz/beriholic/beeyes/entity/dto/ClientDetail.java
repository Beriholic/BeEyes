package xyz.beriholic.beeyes.entity.dto;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import xyz.beriholic.beeyes.entity.vo.request.ClientReportVO;

@Slf4j
@Data
@AllArgsConstructor
@TableName("tb_client_detail")
public class ClientDetail {
    @TableId
    Integer id;

    String osArch;
    String osName;
    String osVersion;
    int osBitSize;
    String cpuName;
    int cpuCoreCount;
    double memorySize;
    double diskSize;
    double diskTotalSize;
    String NetworkInterfaceInfo;

    public static ClientDetail from(int clientId, ClientReportVO vo) {
        return new ClientDetail(
                clientId,
                vo.getOsArch(),
                vo.getOsName(),
                vo.getOsVersion(),
                vo.getOsBitSize(),
                vo.getCpuName(),
                vo.getCpuCoreCount(),
                vo.getMemorySize(),
                vo.getDiskSize(),
                vo.getDiskTotalSize(),
                JSON.toJSONString(vo.getNetworkInterfaceInfo())
        );
    }
}
