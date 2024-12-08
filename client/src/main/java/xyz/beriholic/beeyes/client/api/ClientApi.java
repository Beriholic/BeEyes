package xyz.beriholic.beeyes.client.api;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import xyz.beriholic.beeyes.client.entity.MachineInfo;
import xyz.beriholic.beeyes.client.entity.Response;
import xyz.beriholic.beeyes.client.utils.NetUtil;

@Slf4j
@Component
public class ClientApi {
    @Resource
    private NetUtil net;

    public boolean registerToServer(String address, String token) {
        log.info("注册到服务端...");
        log.debug("Address: {} Token: {}", address, token);

        Response response = net.doGet("/register", address, token);
        if (response.isOk()) {
            log.info("客户端注册成功");
        } else {
            log.error("客户端注册失败: {}", response.msg());
        }

        return response.isOk();
    }

    public void reportMachineInfo(MachineInfo machineInfo) {
        Response response = net.doPost("/report", machineInfo);
        if (response.isOk()) {
            log.info("机器信息上报成功");
        } else {
            log.error("机器信息上报失败: {}", response.msg());
        }
    }
}
