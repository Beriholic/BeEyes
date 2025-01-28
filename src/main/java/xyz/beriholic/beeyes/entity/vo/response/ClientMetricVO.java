package xyz.beriholic.beeyes.entity.vo.response;

import lombok.Data;
import lombok.experimental.Accessors;
import xyz.beriholic.beeyes.entity.dto.Client;
import xyz.beriholic.beeyes.entity.dto.ClientDetail;

import java.util.List;

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
        return;
    }

//    public void addDataFromRuntimeInfo(RuntimeInfoVO runtime) {
//        this.cpuUsage = runtime.getCpuUsage();
//        this.memoryUsage = runtime.getMemoryUsage();
//        this.networkUploadSpeed = runtime.getNetworkUploadSpeed();
//        this.networkDownloadSpeed = runtime.getNetworkDownloadSpeed();
//    }
}
