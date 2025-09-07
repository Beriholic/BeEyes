package xyz.beriholic.beeyes.entity.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import xyz.beriholic.beeyes.model.request.ReportMachineInfoRequest;

@Slf4j
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("tb_client_detail")
@Accessors(chain = true)
public class ClientDetail {
    @TableId
    Long id;
    String osName;
    String kernelVersion;
    String osVersion;
    String cpuArch;
    String cpuName;
    Integer cpuCoreCount;
    Double totalMemory;
    Double totalSwap;
    Double totalDiskSize;

    @TableField("network_interface_info")
    String networkInterfaceInfoJSON;

    public static ClientDetail from(long clientId, ReportMachineInfoRequest vo) {
        return new ClientDetail()
                .setId(clientId)
                .setOsName(vo.getSystemInfo().getOsName())
                .setKernelVersion(vo.getSystemInfo().getKernelVersion())
                .setOsVersion(vo.getSystemInfo().getOsVersion())
                .setCpuArch(vo.getSystemInfo().getCpuArch())
                .setCpuName(vo.getCpuInfo().getName())
                .setCpuCoreCount(vo.getCpuInfo().getCoreCount())
                .setTotalMemory(100D)
                .setTotalSwap(10D)
                .setTotalDiskSize(100D)
                .setNetworkInterfaceInfoJSON("");
//                .setTotalMemory(vo.getMemoryInfo().getTotalMemory().doubleValue())//FIXME Unit
//                .setTotalSwap(vo.getMemoryInfo().getTotalSwap().doubleValue())//FIXME Unit
//                .setTotalDiskSize(vo.getDiskInfo().getTotal().)
//                .setNetworkInterfaceInfoJSON(JSON.toJSONString(vo.getNetworkInterfaceInfo().getFirst()));

    }
}
