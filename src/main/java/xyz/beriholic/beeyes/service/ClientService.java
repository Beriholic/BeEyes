package xyz.beriholic.beeyes.service;

import com.baomidou.mybatisplus.extension.service.IService;
import xyz.beriholic.beeyes.entity.dto.Client;
import xyz.beriholic.beeyes.entity.vo.request.MachineInfoVO;
import xyz.beriholic.beeyes.entity.vo.response.ClientMetricVO;

import java.util.List;

public interface ClientService extends IService<Client> {
    Client getClientById(int id);

    Client getClientByToken(String token);

    String getRegisterToken();

    boolean verifyAndRegister(String token);

    void reportClientInfo(int clientId, MachineInfoVO vo);
//
//    void reportRuntimeInfo(int clientId, @Valid RuntimeInfoVO vo);

    List<ClientMetricVO> getAllClientMetric();
}
