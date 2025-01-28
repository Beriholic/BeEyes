package xyz.beriholic.beeyes.entity.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.Fastjson2TypeHandler;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import xyz.beriholic.beeyes.entity.vo.request.MachineInfoVO;

import java.util.List;

@Slf4j
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("tb_client_detail")
@Accessors(chain = true)
public class ClientDetail {
    @TableId
    Integer id;

    String osName;
    String kernelVersion;

    String osVersion;
    String cpuArch;
    String cpuName;
    Integer cpuCoreCount;
    Double totalMemory;
    Double totalSwap;
    Double totalDiskSize;

    @TableField(typeHandler = Fastjson2TypeHandler.class)
    List<MachineInfoVO.NetworkInterfaceInfo> networkInterfaceInfo;

    public static ClientDetail from(int clientId, MachineInfoVO vo) {
        return new ClientDetail()
                .setId(clientId)
                .setOsName(vo.getSystemInfo().getOsName())
                .setKernelVersion(vo.getSystemInfo().getKernelVersion())
                .setOsVersion(vo.getSystemInfo().getOsVersion())
                .setCpuArch(vo.getSystemInfo().getCpuArch())
                .setCpuName(vo.getCpuInfo().getName())
                .setCpuCoreCount(vo.getCpuInfo().getCoreCount())
                .setTotalMemory(vo.getMemoryInfo().getTotalMemory())
                .setTotalSwap(vo.getMemoryInfo().getTotalSwap())
                .setTotalDiskSize(vo.getDiskInfo().getTotal())
                .setNetworkInterfaceInfo(vo.getNetworkInterfaceInfo());
    }
}
