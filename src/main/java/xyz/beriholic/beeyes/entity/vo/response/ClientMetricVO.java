package xyz.beriholic.beeyes.entity.vo.response;

import com.alibaba.fastjson2.JSONObject;
import lombok.Data;
import lombok.experimental.Accessors;
import xyz.beriholic.beeyes.entity.dto.Client;
import xyz.beriholic.beeyes.entity.dto.ClientDetail;
import xyz.beriholic.beeyes.entity.dto.RuntimeInfo;
import xyz.beriholic.beeyes.entity.vo.request.MachineInfoVO;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

@Data
@Accessors(chain = true)
public class ClientMetricVO {
    Integer id;
    Boolean online;
    String name;
    String location;


    //Detail
    String osName;
    String kernelVersion;
    String osVersion;
    String cpuArch;
    String cpuName;
    Integer cpuCoreCount;
    Double totalMemory;
    Double totalSwap;
    Double totalDiskSize;


    //Runtime
    Double cpuUsage;
    Double memoryUsage;
    Double swapUsage;
    Double networkUploadSpeed;
    Double networkDownloadSpeed;
    List<String> ipList;

    public static ClientMetricVO from(Client client) {
        return new ClientMetricVO()
                .setId(client.getId())
                .setName(client.getName())
                .setLocation(client.getLocation());
    }

    public void addDataFromClientDetail(ClientDetail detail) {
        this.osName = detail.getOsName();
        this.kernelVersion = detail.getKernelVersion();
        this.osVersion = detail.getOsVersion();
        this.cpuArch = detail.getCpuArch();
        this.cpuCoreCount = detail.getCpuCoreCount();
        this.totalMemory = detail.getTotalMemory();
        this.totalSwap = detail.getTotalSwap();
        this.totalDiskSize = detail.getTotalDiskSize();
        this.cpuName = detail.getCpuName();

        CopyOnWriteArrayList<String> ipList = new CopyOnWriteArrayList<>();

        MachineInfoVO.NetworkInterfaceInfo networkInterfaceInfo = JSONObject.parseObject(detail.getNetworkInterfaceInfoJSON(), MachineInfoVO.NetworkInterfaceInfo.class);


        if (Objects.nonNull(networkInterfaceInfo)) {
            ipList.addAll(networkInterfaceInfo.getIpv4());
            ipList.addAll(networkInterfaceInfo.getIpv6());
        }

        this.ipList = ipList;
    }

    public void addDataFromRuntimeInfo(RuntimeInfo runtime) {
        this.cpuUsage = runtime.getCpuUsage();
        this.memoryUsage = runtime.getMemoryUsage();
        this.swapUsage = runtime.getSwapUsage();
        this.networkUploadSpeed = runtime.getNetworkUploadSpeed();
        this.networkDownloadSpeed = runtime.getNetworkDownloadSpeed();
    }

}