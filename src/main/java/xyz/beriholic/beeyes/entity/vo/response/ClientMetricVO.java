package xyz.beriholic.beeyes.entity.vo.response;

import com.alibaba.fastjson2.JSON;
import lombok.Data;
import lombok.experimental.Accessors;
import xyz.beriholic.beeyes.entity.dto.Client;
import xyz.beriholic.beeyes.entity.dto.ClientDetail;
import xyz.beriholic.beeyes.entity.dto.NetworkInterfaceInfo;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Data
@Accessors(chain = true)
public class ClientMetricVO {
    int id;
    boolean online;
    String name;
    String location;
    String osName;
    String osArch;
    String osVersion;
    int osBitSize;
    String cpuName;
    List<String> ipList;
    int cpuCoreCount;
    double memorySize;
    double cpuUsage;
    double memoryUsage;
    double diskSize;
    double diskTotalSize;
    double networkUploadSpeed;
    double networkDownloadSpeed;

    public static ClientMetricVO from(Client client) {
        return new ClientMetricVO()
                .setId(client.getId())
                .setName(client.getName())
                .setLocation(client.getLocation());
    }

    public void addDataFromClientDetail(ClientDetail detail) {
        this.osArch = detail.getOsArch();
        this.osName = detail.getOsName();
        this.osVersion = detail.getOsVersion();
        this.osBitSize = detail.getOsBitSize();
        this.cpuName = detail.getCpuName();
        this.memorySize = detail.getMemorySize();
        this.cpuCoreCount = detail.getCpuCoreCount();
        this.diskSize = detail.getDiskSize();
        this.diskTotalSize = detail.getDiskTotalSize();

        CopyOnWriteArrayList<String> ipList = new CopyOnWriteArrayList<>();
        NetworkInterfaceInfo network = JSON.parseObject(detail.getNetworkInterfaceInfo(), NetworkInterfaceInfo.class);
        Collections.addAll(ipList, network.getIpv4Addr());
        Collections.addAll(ipList, network.getIpv6Addr());

        this.ipList = ipList;
    }

//    public void addDataFromRuntimeInfo(RuntimeInfoVO runtime) {
//        this.cpuUsage = runtime.getCpuUsage();
//        this.memoryUsage = runtime.getMemoryUsage();
//        this.networkUploadSpeed = runtime.getNetworkUploadSpeed();
//        this.networkDownloadSpeed = runtime.getNetworkDownloadSpeed();
//    }
}
